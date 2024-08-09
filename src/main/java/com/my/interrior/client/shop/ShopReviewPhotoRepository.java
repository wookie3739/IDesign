package com.my.interrior.client.shop;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import jakarta.transaction.Transactional;

public interface ShopReviewPhotoRepository  extends JpaRepository<ShopReviewPhotoEntity, Long>{
	List<ShopReviewPhotoEntity> findByShopReviewEntityShopReviewNo(Long shopReviewNo);
	
	@Transactional
	void deleteByShopReviewEntityShopReviewNo(Long shopReviewNo);
}
