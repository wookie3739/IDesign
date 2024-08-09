package com.my.interrior.client.cart;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CartRepository extends JpaRepository<CartEntity, Long>{

	List<CartEntity> findByUserEntity_UId(String UId);
	int countByUserEntity_UId(String UId);
	List<CartEntity> findByUserEntity_UNo(Long UNo);
	void deleteAllByUserEntity_UNo(Long userNo);

//	CartEntity findByCNo(Long CNo);
	void deleteByCNo(Long CNo);

}
