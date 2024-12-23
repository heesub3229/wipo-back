package com.wipo.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.wipo.Entity.MapEntity;

@Repository
public interface MapRepository extends JpaRepository<MapEntity, Long> {

	@Query("SELECT m FROM MapEntity m WHERE m.x = :x AND m.y = :y")
	MapEntity findByXAndY(@Param("x")double x, @Param("y")double y);
	
}
