package com.wipo.Repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.wipo.Entity.UserEntity;

public interface UserRepository extends JpaRepository<UserEntity, Long> {

	UserEntity findByEmail(String email);
	
	Optional<UserEntity> findByEmailAndPassword(String email, String password);
	
	Optional<UserEntity> findByNameAndDateBirthAndLogintype(String name,String dateBirth,String loginType);
	
	Optional<UserEntity> findByEmailAndNameAndLogintype(String email, String name,String loginType);
	
	Optional<UserEntity> findByEmailAndLogintype(String email,String loginType);
}
