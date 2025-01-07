package com.wipo.Repository;

import java.time.ZonedDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.wipo.Entity.UserEntity;
import com.wipo.Entity.UserRelationEntity;

@Repository
public interface UserRelationRepository extends JpaRepository<UserRelationEntity, Long> {

	@Query("SELECT u FROM UserRelationEntity u WHERE u.user = :user AND u.approve_flag = 'Y'")
	List<UserRelationEntity> findByIdAndUser(@Param("user")UserEntity user);
	
	@Query("SELECT u FROM UserRelationEntity u WHERE u.friend = :user AND u.approve_flag = 'Y'")
	List<UserRelationEntity> findByIdAndFriend(@Param("user")UserEntity user);
	
	@Query("SELECT u FROM UserRelationEntity u WHERE u.friend = :user AND u.approve_flag = 'W'")
	List<UserRelationEntity> findByFriendAndWait(@Param("user")UserEntity user);
	
	@Query("SELECT u FROM UserRelationEntity u WHERE ( u.user = :user AND u.friend = :friend ) OR ( u.user = :friend AND u.friend = :user )")
	UserRelationEntity findByUserToUser(@Param("user")UserEntity user,@Param("friend")UserEntity friend);
		
	@Query("SELECT u FROM UserRelationEntity u WHERE u.user = :user AND u.approve_flag != 'W' AND u.confirm_flag = 'Y' AND u.update_at BETWEEN :date AND CURRENT_TIMESTAMP")
	List<UserRelationEntity> findByApproveYesOrNoConYes(@Param("user")UserEntity user,@Param("date")ZonedDateTime date);
	
	@Query("SELECT u FROM UserRelationEntity u WHERE u.user = :user AND u.approve_flag != 'W' AND u.confirm_flag = 'N'")
	List<UserRelationEntity> findByApproveYesOrNoConNo(@Param("user")UserEntity user);
}
