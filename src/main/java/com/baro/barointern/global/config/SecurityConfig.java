package com.baro.barointern.global.config;


import com.baro.barointern.global.jwt.JwtFilter;
import com.baro.barointern.global.jwt.JwtProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

	private final JwtProvider jwtProvider;
	private final UserDetailsService userDetailsService;

	public SecurityConfig(JwtProvider jwtProvider, UserDetailsService userDetailsService) {
		this.jwtProvider = jwtProvider;
		this.userDetailsService = userDetailsService;
	}

	/**
	 * - 회원가입 로그인은 인증 없이 접근 가능
	 * - admin으로 시작하는 url은 ADMIN 권한 있어야지 접근 가능
	 * - 이외 요청들도 인증 필요
	 * - todo : globalhandler 처리
	 */
	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
		//CSRF 보호 비활성화 (JWT 사용 시 일반적으로 비활성화)
		httpSecurity.csrf(csrf -> csrf.disable())
			.authorizeHttpRequests(auth -> auth
				.requestMatchers(
					"/swagger-ui/**",
					"swagger-ui/index.html",
					"/swagger-ui.html",
					"/v3/api-docs/**",
					"/v3/api-docs",
					"/webjars/**").permitAll()
				.requestMatchers("/signup", "/login").permitAll()
				.requestMatchers("/admin/**").hasRole("ADMIN")
				.anyRequest().authenticated())
			.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
			.addFilterBefore(jwtFilter(), UsernamePasswordAuthenticationFilter.class);

		return httpSecurity.build();
	}

	/**
	 * 비밀번호 암호화
	 */
	@Bean
	public BCryptPasswordEncoder passwordEncoder() {

		return new BCryptPasswordEncoder();
	}


	@Bean
	public JwtFilter jwtFilter() {

		return new JwtFilter(jwtProvider);
	}
}
