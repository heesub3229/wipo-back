package com.wipo.Repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.wipo.Entity.RcptEntity;
import com.wipo.Entity.UserEntity;

@Repository
public interface RcptRepository extends JpaRepository<RcptEntity, Long> {

	@Query("SELECT r FROM RcptEntity r WHERE r.date >= :startDate AND r.date <= :endDate AND r.create_user_sid = :user ORDER BY r.date")
	List<RcptEntity> findByDateAndUser(@Param("startDate")String  startDate,@Param("endDate")String  endDate,@Param("user")UserEntity userEntity);
	
	@Query("SELECT r.type, SUM(r.amount) FROM RcptEntity r WHERE r.date = :date AND r.create_user_sid = :user GROUP BY r.type")
	List<Object[]> getDateSumAmount(@Param("date")String  date,@Param("user")UserEntity userEntity);
	
	@Query("SELECT r.type, SUM(r.amount) FROM RcptEntity r WHERE r.date >= :startDate AND r.date <= :endDate AND r.create_user_sid = :user GROUP BY r.type")
	List<Object[]> getMonthSumAmount(@Param("startDate")String  startDate,@Param("endDate")String  endDate,@Param("user")UserEntity userEntity);

	@Query("SELECT SUBSTR(r.date, 0, 8) , r.type, SUM(r.amount) FROM RcptEntity r WHERE r.date = :date AND r.create_user_sid = :user GROUP BY SUBSTR(r.date, 0, 8), r.type ORDER BY SUBSTR(r.date, 0, 8) DESC")
	List<Object[]> getMonthSumAmountToDate(@Param("date")String date,@Param("user")UserEntity userEntity);
	
}
