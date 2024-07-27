package com.my.interrior.client.cart;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.my.interrior.client.shop.ShopEntity;
import com.my.interrior.client.shop.ShopOptionValueEntity;
import com.my.interrior.client.shop.ShopOptionValueService;
import com.my.interrior.client.shop.ShopService;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class CartController {

	
	private final CartService cartService;
	private final ShopService shopService;
	private final CartOptionService cartOptionService;
	private final ShopOptionValueService shopOptionValueService;
	private static int total = 0;
	private static int count = 0;
	
	//cart의 loadMyCarts()랑 이거랑 연관돼있음.
	@GetMapping("/my/cart")
	public String goToCart(HttpSession session, Model model) {
		//shop_title, shop_main_photo, shop_price를 가져오기
		//cart_option에 shop_option_value가 있음 일단 옵션 빼고 가져오기
		
		
		String userId = (String) session.getAttribute("UId");
		
		List<CartEntity> cartEntities = cartService.findAllCarts(userId);
		List<Integer> cartQuantity = new ArrayList<>();
		for(int i = 0; i < cartEntities.size(); i++) {
			cartQuantity.add(cartEntities.get(i).getQuantity());
		}


		List<Long> shopNos = cartEntities.stream()
							.map(cart -> cart.getShopEntity().getShopNo())
							.collect(Collectors.toList());

		List<ShopEntity> shopEntities = shopService.getCartsFromShop(shopNos);

		//카트 가격들
//		List<String> shopPrices = shopEntities.stream()
//								.map(price -> price.getShopPrice())
//								.collect(Collectors.toList());
//		System.out.println("shopPrices: " + shopPrices);
		
		
		
		
		//이제 옵션 값들을 가져와야함 c_no를 사용하면 될 듯
		//여기에는 u_id에 해당하는 c_no가 여러 개 들어가있음.
		List<Long> cartNos = cartEntities.stream()
							.map(cart -> cart.getCNo())
							.collect(Collectors.toList());
		
		List<CartOptionEntity> cartOptions = cartOptionService.getPickedOptions(cartNos);
		//이제 c_no에 해당하는 shop_option_value_no를 사용해서 가져오자
		
		List<Long> options = cartOptions.stream()
							.map(option -> option.getShopOptionValueEntity().getShopOptionValueNo())
							.collect(Collectors.toList());
	
		System.out.println("options: " + options);
		List<ShopOptionValueEntity> shopOptionValues = new ArrayList<>();
		for(int i = 0; i < options.size(); i++) {
			shopOptionValues.add(shopOptionValueService.getShopOptionValues(options.get(i)));
		}
		
	
		count = cartService.getAllCartsByUserId(userId);
		
//		System.out.println("count: " + count);
//		System.out.println("cartEntities: " + cartEntities);
//		System.out.println("shopEntities: " + shopEntities);
		
		//합계 구하기
		for(int i = 1; i <= count; i++){
			if(shopOptionValues.get(i - 1).getShopOptionPrice() != 0) {
				//(수량 * 가격) +  옵션 값(있다면)
			total += (cartEntities.get(i - 1).getQuantity() * Integer.parseInt(cartEntities.get(i - 1).getShopEntity().getShopPrice()))
					 + shopOptionValues.get(i - 1).getShopOptionPrice();
			System.out.println("total:" + total);
			}else {
				total += (cartEntities.get(i - 1).getQuantity() * Integer.parseInt(cartEntities.get(i - 1).getShopEntity().getShopPrice()));
			}
		}
		
		NumberFormat numberFormat = NumberFormat.getNumberInstance(Locale.KOREA);
		String formattedTotal = numberFormat.format(total);
		//여기에는 shop_option_value에 해당하는 값들이 객체로 들어있음.
//		List<ShopOptionValueEntity> shopOptionValues = shopOptionValueService.getShopOptionValues(options);

		model.addAttribute("total", formattedTotal);
		model.addAttribute("cartEntities", cartEntities);
		model.addAttribute("cartOptions", cartOptions);
		// shop_option_value 테이블이 전부 들어가있음.
		model.addAttribute("shopOptionValues", shopOptionValues);
		// shop_no를 기준으로 shopEntity 테이블의 데이터들이 들어가있음.
		model.addAttribute("shopEntities", shopEntities);

		
		return "/client/cart/cart";
	}
	
	
	
}
