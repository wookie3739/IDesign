package com.my.interrior.client.cart;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.my.interrior.client.shop.ShopEntity;
import com.my.interrior.client.shop.ShopOptionValueEntity;
import com.my.interrior.client.shop.ShopOptionValueRepository;
import com.my.interrior.client.shop.ShopRepository;
import com.my.interrior.client.user.UserEntity;
import com.my.interrior.client.user.UserRepository;

import jakarta.servlet.http.HttpSession;

@Service
public class CartService {
	
	@Autowired
	private ShopRepository shopRepository;
	
	@Autowired
	private HttpSession session;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private ShopOptionValueRepository shopOptionValueRepository;
	
	@Autowired
	private CartRepository cartRepository;

	
	public void inCart(List<Long> optionValueIds, Long shopNo, int quantity) {

		ShopEntity shopNO = shopRepository.findById(shopNo)
				.orElseThrow(() -> new RuntimeException("Shop not found with id: " + shopNo));
		;
		String userId = (String) session.getAttribute("UId");

		UserEntity userEntity = userRepository.findByUId(userId);
		CartEntity cartEntity = new CartEntity();
		cartEntity.setQuantity(quantity);
		cartEntity.setUserEntity(userEntity);
		cartEntity.setShopEntity(shopNO);

		List<CartOptionEntity> cartOptions = new ArrayList<>();

		for (Long id : optionValueIds) {
		    ShopOptionValueEntity optionValue = shopOptionValueRepository.findById(id)
		        .orElseThrow(() -> new RuntimeException("Option value not found: " + id));
		    
		    CartOptionEntity cartOption = new CartOptionEntity();
		    cartOption.setCartEntity(cartEntity);
		    cartOption.setShopOptionValueEntity(optionValue);
		    
		    cartOptions.add(cartOption);
		}
		cartEntity.setCartOptions(cartOptions);
		cartRepository.save(cartEntity);

	}
	
	public List<CartEntity> findAllCarts(String userId){
		return cartRepository.findByUserEntity_UId(userId);
	}
	
	public int getAllCartsByUserId(String userId) {
		return cartRepository.countByUserEntity_UId(userId);
	}
}
