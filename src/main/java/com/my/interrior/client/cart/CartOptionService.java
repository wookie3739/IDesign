package com.my.interrior.client.cart;

import java.util.List;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CartOptionService {

	private final CartOptionRepository cartOptionRepository;
	
	List<CartOptionEntity> getPickedOptions(List<Long> cartNos){
		return cartOptionRepository.findByCartEntity_CNoIn(cartNos);
	}
}
