package com.baro.barointern.global.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class JwtFilter extends OncePerRequestFilter {

	private final JwtProvider jwtProvider;

	public JwtFilter(JwtProvider jwtProvider) {
		this.jwtProvider = jwtProvider;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
		throws ServletException, IOException {

		//요청에서 JWT 토큰 추출
		String token = extractTokenFromHeader(request);

		//토큰 검증 & 인증 객체 설정
		if(token != null && jwtProvider.validateToken(token)){
			Authentication authentication = jwtProvider.getAuthentication(token);
			SecurityContextHolder.getContext().setAuthentication(authentication);
		}

		//다음 필터로 요청 전달
		filterChain.doFilter(request, response);
	}

	/**
	 * HTTP 요청 헤더에 JWT 토큰 존재 여부 확인
	 * - Authorization 헤더 존재하는지 확인
	 * - Bearer 토큰 형식인지 확인
	 */
	public String extractTokenFromHeader(HttpServletRequest httpServletRequest){
		String authorizationHeader = httpServletRequest.getHeader(HttpHeaders.AUTHORIZATION);

		if(StringUtils.hasText(authorizationHeader) && authorizationHeader.startsWith("Bearer ")){
			return authorizationHeader.substring(7);
		}

		return null;
	}
}
