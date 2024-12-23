package com.wipo.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.wipo.Entity.UserEntity;
import com.wipo.Entity.UserRelationEntity;
import com.wipo.Entity.UserRelationId;

@Repository
public interface UserRelationRepository extends JpaRepository<UserRelationEntity, UserRelationId> {

	@Query("SELECT u FROM UserRelationEntity u WHERE u.id.user = :user")
	List<UserRelationEntity> findByIdAndUser(@Param("user")UserEntity user);
	
}
