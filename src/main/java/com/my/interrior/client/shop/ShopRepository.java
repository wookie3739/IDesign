package com.my.interrior.client.shop;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.my.interrior.client.cart.CartEntity;

@Repository
public interface ShopRepository extends JpaRepository<ShopEntity, Long>{
	List<ShopEntity> findByShopNoIn(List<Long> shopNos);
}
