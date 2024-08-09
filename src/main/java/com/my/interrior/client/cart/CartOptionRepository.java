package com.my.interrior.client.cart;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CartOptionRepository extends JpaRepository<CartOptionEntity, Long>{
	List<CartOptionEntity> findByCartEntity_CNoIn(List<Long> cartNos);
	void deleteByCartEntity_CNoIn(List<Long> cartNos);

	void deleteByCartEntity_CNo(Long CNo);

}
