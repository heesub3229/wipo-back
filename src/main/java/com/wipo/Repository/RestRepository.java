package com.wipo.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.wipo.Entity.RestEntity;
import com.wipo.Entity.UserEntity;

@Repository
public interface RestRepository extends JpaRepository<RestEntity, Long> {

	@Query("SELECT r FROM RestEntity r WHERE r.create_user_sid = :user")
	List<RestEntity> getRestUserToList(@Param("user")UserEntity user);
	
}
