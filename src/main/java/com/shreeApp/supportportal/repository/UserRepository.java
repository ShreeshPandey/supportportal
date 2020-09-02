package com.shreeApp.supportportal.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.shreeApp.supportportal.domain.User;

public interface UserRepository extends JpaRepository<User, Long> {

	User findUserByUserName(String username);
	User findUserByEmail(String email);
}
