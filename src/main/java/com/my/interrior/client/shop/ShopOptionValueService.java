package com.my.interrior.client.shop;

import java.util.List;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ShopOptionValueService {

	private final ShopOptionValueRepository shopOptionValueRepository;
	
	
	public ShopOptionValueEntity getShopOptionValues(Long options){
		return shopOptionValueRepository.findByShopOptionValueNo(options);
	}
}
