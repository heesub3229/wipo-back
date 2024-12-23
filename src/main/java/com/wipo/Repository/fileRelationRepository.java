package com.wipo.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.wipo.Entity.FileRelationEntity;
import com.wipo.Entity.FileRelationId;
import com.wipo.Entity.PostEntity;

@Repository
public interface fileRelationRepository extends JpaRepository<FileRelationEntity,FileRelationId> {

	@Query("SELECT f FROM FileRelationEntity f WHERE f.id.post_sid = :postSid")
	List<FileRelationEntity> findByPostSid(@Param("postSid")Long postSid);
	
}
