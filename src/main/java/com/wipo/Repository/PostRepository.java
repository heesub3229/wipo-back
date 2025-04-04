package com.wipo.Repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.wipo.Entity.PostEntity;
import com.wipo.Entity.UserEntity;

@Repository
public interface PostRepository extends JpaRepository<PostEntity, Long>{

	@Query("SELECT p FROM PostEntity p WHERE p.create_user_sid = :userSid")
	Page<PostEntity> findByCreateUserSid(@Param("userSid")UserEntity userSid,Pageable pageable);
		
}
