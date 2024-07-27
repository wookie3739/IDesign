package com.my.interrior.client.evaluation;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import jakarta.transaction.Transactional;

public interface ReviewPhotoRepository extends JpaRepository<ReviewPhotoEntity, Long>{
	List<ReviewPhotoEntity> findByReview_RNo(Long rNo);
	@Transactional
    void deleteByReviewRNo(Long rNo);
}
