package com.wipo.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.wipo.Entity.CheckEntity;
import com.wipo.Entity.UserEntity;

@Repository
public interface CheckRepository extends JpaRepository<CheckEntity, Long> {

	@Query("SELECT c FROM CheckEntity c WHERE c.create_user_sid = :user ORDER BY c.create_at")
	List<CheckEntity> findByCreateUserSid(@Param("user")UserEntity user);
	
}
