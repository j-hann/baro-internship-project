package com.baro.barointern.global.auth;


import com.baro.barointern.domain.user.repository.UserRepository;
import com.baro.barointern.domain.user.entity.User;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

	private final UserRepository userRepository;

	public UserDetailsServiceImpl(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	/**
	 * spring security가 사용자 정보 조회할 수 있도록
	 */
	@Override
	public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {
		//사용자 정보 조회
		User user = userRepository.findByName(userName)
			.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

		return new UserDetailsImpl(user);
	}
}
