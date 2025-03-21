package com.baro.barointern.global.jwt;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

@Component
public class JwtFilter extends OncePerRequestFilter {

	private final JwtProvider jwtProvider;
	private final HandlerExceptionResolver handlerExceptionResolver;

	public JwtFilter(JwtProvider jwtProvider, HandlerExceptionResolver handlerExceptionResolver) {
		this.jwtProvider = jwtProvider;
		this.handlerExceptionResolver = handlerExceptionResolver;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
		throws ServletException, IOException {

		try {
			//요청에서 JWT 토큰 추출
			String token = extractTokenFromHeader(request);

			//토큰 검증 & 인증 객체 설정
			if (token != null && jwtProvider.validateToken(token)) {
				Authentication authentication = jwtProvider.getAuthentication(token);
				SecurityContextHolder.getContext().setAuthentication(authentication);
			}

			//다음 필터로 요청 전달
			filterChain.doFilter(request, response);
		} catch (JwtException e) {
			handlerExceptionResolver.resolveException(request, response, null, e);
		}
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
