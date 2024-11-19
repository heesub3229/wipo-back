package com.wipo.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.wipo.Entity.UserEntity;

public interface UserRepository extends JpaRepository<UserEntity, Long> {

	UserEntity findByEmail(String email);
	
}
