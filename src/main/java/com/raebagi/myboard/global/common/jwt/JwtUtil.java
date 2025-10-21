package com.raebagi.myboard.global.common.jwt;

import java.security.Key;
import java.util.Base64;
import java.util.Date;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.raebagi.myboard.user.domain.entity.UserRoleEnum;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j(topic = "jwtUtil")
public class JwtUtil {

	// ===== JWT 상수 =====
	public static final String AUTHORIZATION_HEADER = "Authorization"; // Access Token 헤더 키
	public static final String REFRESH_TOKEN_COOKIE_NAME = "RefreshToken"; // Refresh Token 쿠키 이름
	public static final String AUTHORIZATION_KEY = "auth"; // 클레임에 저장할 권한 키
	public static final String BEARER_PREFIX = "Bearer "; // Bearer 접두사

	private static final long ACCESS_TOKEN_TIME = 60 * 60 * 1000L; // Access Token 유효기간: 60분
	private static final long REFRESH_TOKEN_TIME = 14 * 24 * 60 * 60 * 1000L; // Refresh Token 유효기간: 14일

	@Value("${jwt.secret.key}") // application.yml에 설정된 Base64 인코딩 비밀 키
	private String secretKey;

	private Key key; // JWT 서명용 Key 객체
	private final SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256; // 서명 알고리즘

	// ===== Bean 초기화 후 SecretKey를 Key 객체로 변환 =====
	@PostConstruct
	public void init() {
		byte[] bytes = Base64.getDecoder().decode(secretKey);
		key = Keys.hmacShaKeyFor(bytes);
	}

	// ===== Access Token 생성 =====
	public String createAccessToken(String username, UserRoleEnum role) {
		Date date = new Date();

		return BEARER_PREFIX + Jwts.builder()
			.setSubject(username)                     // 'sub' 클레임: 사용자 식별값
			.claim(AUTHORIZATION_KEY, role.getAuthority()) // 'auth' 클레임: 사용자 권한
			.claim("jti", UUID.randomUUID().toString())    // 'jti' 클레임: 토큰 고유 ID (블랙리스트 용, 현재는 미사용)
			.setExpiration(new Date(date.getTime() + ACCESS_TOKEN_TIME)) // 토큰 만료 시간
			.setIssuedAt(date)                        // 발급 시간
			.signWith(key, signatureAlgorithm)        // 서명: 위변조 방지
			.compact();                               // JWT 문자열로 변환
	}

	// ===== Refresh Token 생성 =====
	public String createRefreshToken(String username, UserRoleEnum role) {
		Date date = new Date();

		return Jwts.builder()
			.setSubject(username)
			.claim(AUTHORIZATION_KEY, role.getAuthority())
			.claim("jti", UUID.randomUUID().toString()) // jti 추가
			.setExpiration(new Date(date.getTime() + REFRESH_TOKEN_TIME))
			.setIssuedAt(date)
			.signWith(key, signatureAlgorithm)
			.compact();
	}

	// ===== 요청 헤더에서 JWT 추출 =====
	public static String getJwtFromHeader(HttpServletRequest request) {
		String bearerToken = request.getHeader(AUTHORIZATION_HEADER);
		log.info("JwtUtil - Authorization header Raw Value: {}", bearerToken);

		if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_PREFIX)) {
			return bearerToken.substring(7); // Bearer 접두사 제거
		}
		return null;
	}

	// ===== Access Token 검증 =====
	public boolean validateToken(String token) {
		try {
			// 토큰 파싱 및 서명 검증
			Jwts.parserBuilder()
				.setSigningKey(key)
				.build()
				.parseClaimsJws(token);

			return true; // 정상 토큰
		} catch (ExpiredJwtException e) {
			log.error("Expired Jwt Token");
			throw e; // 만료 토큰은 재발급 로직에서 처리
		} catch (Exception e) {
			log.error("Invalid Jwt Token");
			return false; // 변조/형식 오류
		}
	}

	// ===== Refresh Token 쿠키 삭제 (로그아웃) =====
	public void deleteRefreshToken(HttpServletResponse response) {
		Cookie cookie = new Cookie(REFRESH_TOKEN_COOKIE_NAME, null);
		cookie.setHttpOnly(true);
		cookie.setMaxAge(0); // 즉시 만료
		cookie.setPath("/");
		response.addCookie(cookie);
	}

	// ===== JWT에서 사용자 정보 추출 =====
	public Claims getUserInfoFromToken(String token) {
		try {
			return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
		} catch (ExpiredJwtException e) {
			throw e; // 만료 토큰도 필요 시 Claims 확인 가능
		}
	}

	// ===== JWT에서 jti 추출 =====
	public String getJtiFromToken(String token) {
		return getUserInfoFromToken(token).get("jti", String.class);
	}

	// ===== JWT 남은 만료 시간(ms) 계산 =====
	public long getExpirationRemainingTime(String token) {
		Date expiration = getUserInfoFromToken(token).getExpiration();
		return expiration.getTime() - new Date().getTime();
	}
}
