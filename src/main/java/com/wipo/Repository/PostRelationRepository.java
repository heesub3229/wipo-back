package com.wipo.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.wipo.Entity.PostRelationEntity;
import com.wipo.Entity.PostRelationId;

@Repository
public interface PostRelationRepository extends JpaRepository<PostRelationEntity, PostRelationId> {

}
