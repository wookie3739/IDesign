package com.my.interrior.client.shop;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.my.interrior.client.user.UserEntity;

public interface ShopReviewRepository extends JpaRepository<ShopReviewEntity, Long>{
	int countByUser(UserEntity user);
	List<ShopReviewEntity> findByUserUNo(Long userUNo);
	List<ShopReviewEntity> findByShopEntityShopNo(Long shopNo);
}
