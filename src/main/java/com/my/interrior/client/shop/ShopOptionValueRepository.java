package com.my.interrior.client.shop;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ShopOptionValueRepository extends JpaRepository<ShopOptionValueEntity, Long>{
	
	
	ShopOptionValueEntity findByShopOptionValueNo(Long options);
}
