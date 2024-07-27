package com.my.interrior.client.cart;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.my.interrior.client.shop.ShopEntity;
import com.my.interrior.client.shop.ShopOptionValueEntity;
import com.my.interrior.client.shop.ShopOptionValueService;
import com.my.interrior.client.shop.ShopService;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class CartRestController {
	
	private final CartService cartService;
	
	@PostMapping("/auth/cart")
    public ResponseEntity<String> goCart(
    		@RequestParam("shopNo") Long shopNo,
    		@RequestParam("options") List<Long> optionValueIds,
    		@RequestParam("quantity") int quantity, HttpSession session, Model model) {
    	
    	String userId = (String) session.getAttribute("UId");
    	
    	if(userId == null) {

    		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요한 페이지입니다. 로그인하시겠습니까?");
    	}
    	
    	cartService.inCart(optionValueIds, shopNo, quantity);
    	
    	return ResponseEntity.ok("장바구니에 성공적으로 담겼습니다. 장바구니로 이동하시겠습니까?");
    }
}
