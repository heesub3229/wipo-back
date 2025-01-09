package com.wipo.Repository;

import java.time.ZonedDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.wipo.Entity.PostEntity;
import com.wipo.Entity.PostRelationEntity;
import com.wipo.Entity.UserEntity;

@Repository
public interface PostRelationRepository extends JpaRepository<PostRelationEntity, Long> {

	@Query("SELECT p FROM PostRelationEntity p WHERE p.user = :user AND p.post = :post")
	PostRelationEntity findByUserAndPost(@Param("user")UserEntity userEntity,@Param("post")PostEntity postEntity);
	
	@Query("SELECT p FROM PostRelationEntity p WHERE p.user = :user")
	Page<PostRelationEntity> findByUserPostRel(@Param("user")UserEntity userEntity,Pageable pageable);
	
	@Query("SELECT p FROM PostRelationEntity p WHERE p.user = :user AND p.create_at BETWEEN :date AND CURRENT_TIMESTAMP")
	List<PostRelationEntity> findByUserPostToDate(@Param("user")UserEntity userEntity,@Param("date")ZonedDateTime date);
	
}
