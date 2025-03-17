package com.wipo.Repository;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.wipo.Entity.RestEntity;
import com.wipo.Entity.RestRelationEntity;
import com.wipo.Entity.UserEntity;

@Repository
public interface RestRelationRepository extends JpaRepository<RestRelationEntity, Long> {
	
	@Query("SELECT r FROM RestRelationEntity r WHERE r.user = :user AND r.rest = :rest")
	Optional<RestRelationEntity> findByUserAndRest(@Param("user")UserEntity user,@Param("rest")RestEntity rest);
	
	@Query("SELECT r FROM RestRelationEntity r WHERE r.user = :user AND r.confirm_flag = 'Y' AND r.create_at BETWEEN :date AND CURRENT_TIMESTAMP")
	List<RestRelationEntity> findByUserListToDate(@Param("user")UserEntity user,@Param("date")ZonedDateTime date);

	@Query("SELECT r FROM RestRelationEntity r WHERE r.user = :user AND r.confirm_flag = 'N'")
	List<RestRelationEntity> findByUserList(@Param("user")UserEntity user);

}
