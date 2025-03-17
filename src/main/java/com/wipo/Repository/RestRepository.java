package com.wipo.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.wipo.Entity.RestEntity;

@Repository
public interface RestRepository extends JpaRepository<RestEntity, Long> {

}
