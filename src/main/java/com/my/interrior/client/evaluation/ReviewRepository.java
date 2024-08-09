package com.my.interrior.client.evaluation;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.my.interrior.client.user.UserEntity;

public interface ReviewRepository extends JpaRepository<ReviewEntity, Long> {
	// @Modifying
	// @Query("update review r set r.RViews = r.RViews + 1 where r.rNo = :rNo")
	// int updateHits(Long rNo);
	int countByUser(UserEntity user);
	List<ReviewEntity> findByUserUNo(Long userUNo);
}
