package com.wipo.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.wipo.Entity.MapRelationEntity;
import com.wipo.Entity.MapRelationId;
import com.wipo.Entity.PostEntity;

@Repository
public interface MapRelationRepository extends JpaRepository<MapRelationEntity, MapRelationId> {
	
	@Query("SELECT m FROM MapRelationEntity m WHERE m.id.user_sid = :userSid AND m.post = :postEntity")
	MapRelationEntity findByPostANDUser(@Param("userSid")Long userSid,@Param("postEntity")PostEntity postEntity);

}
