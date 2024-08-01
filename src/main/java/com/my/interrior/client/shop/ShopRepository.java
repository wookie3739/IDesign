package com.my.interrior.client.shop;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;



@Repository
public interface ShopRepository extends JpaRepository<ShopEntity, Long>{
	List<ShopEntity> findByShopNoIn(List<Long> shopNos);

	ShopEntity findByShopNo(Long shopNo);

	List<ShopEntity> findByShopTitleContainingAndShopCategoryContaining(
	        String shopTitle, String shopCategory
	    );
	Optional<ShopEntity> findById(Long shopNo);
}
