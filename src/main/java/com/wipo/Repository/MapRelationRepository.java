package com.wipo.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.wipo.Entity.MapRelationEntity;
import com.wipo.Entity.PostEntity;
import com.wipo.Entity.UserEntity;

@Repository
public interface MapRelationRepository extends JpaRepository<MapRelationEntity, Long> {
	
	@Query("SELECT m FROM MapRelationEntity m WHERE m.user = :user AND m.post = :post")
	MapRelationEntity findByPostANDUser(@Param("user")UserEntity user,@Param("post")PostEntity postEntity);

}
