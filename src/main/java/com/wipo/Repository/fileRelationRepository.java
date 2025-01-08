package com.wipo.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.wipo.Entity.FileEntity;
import com.wipo.Entity.FileRelationEntity;
import com.wipo.Entity.PostEntity;

@Repository
public interface fileRelationRepository extends JpaRepository<FileRelationEntity,Long> {

	@Query("SELECT f FROM FileRelationEntity f WHERE f.post = :post")
	List<FileRelationEntity> findByPostSid(@Param("post")PostEntity post);
	
	@Query("SELECT f FROM FileRelationEntity f WHERE f.post = :post AND f.file = :file")
	FileRelationEntity findByPostAndFile(@Param("post")PostEntity post, @Param("file")FileEntity file);
	
}
