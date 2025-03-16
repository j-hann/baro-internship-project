package com.baro.barointern.domain.user.repository;

import com.baro.barointern.domain.user.entity.User;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.stereotype.Repository;

@Repository
public class UserRepository {

	private final Map<String, User> userStorage = new HashMap<>();
	private final Map<Long, String> userIdToUserName = new HashMap<>();
	private final AtomicLong userIdCounter = new AtomicLong(1);

	/**
	 * 사용자 저장
	 */
	public void save(User user){
		userStorage.put(user.getUserName(), user);
		userIdToUserName.put(user.getId(), user.getUserName());
	}

	/**
	 * ID 생성
	 * - 1부터 1씩 증가
	 */
	public long generateId(){
		return userIdCounter.getAndIncrement();
	}

	/**
	 * userName으로 사용자 조회
	 */
	public Optional<User> findByName(String userName) {
		return Optional.ofNullable(userStorage.get(userName));
	}

	/**
	 * userId로 사용자 조회
	 */
	public Optional<User> findById(long userId) {

		return Optional.ofNullable(userIdToUserName.get(userId))
			.map(userStorage::get);
	}
}
