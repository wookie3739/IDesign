package com.my.interrior.client.cart;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.my.interrior.client.user.UserEntity;
import com.my.interrior.client.user.UserRepository;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class CartRestController {
	
	private final CartService cartService;
	private final CartRepository cartRepository;
	private final UserRepository userRepository;
	private final CartOptionRepository cartOptionRepository;
	
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
	
	@Transactional
	@DeleteMapping("/delete/all/cart")
	public ResponseEntity<?> deleteAll(HttpSession session){
		
		String userId = (String) session.getAttribute("UId");
		
		UserEntity user = userRepository.findByUId(userId);
		Long userNo = user.getUNo();
		
		//cart의 c_no를 참조하고 있는 cart_option의 c_no를 지움.
		//delete from cart_option where c_no IN(select c_no from cart where u_no = 3);
		List<CartEntity> carts = cartRepository.findByUserEntity_UNo(userNo);
		System.out.println("지금 cart에 담겨있는 carts들은: " + carts);
		
		List<Long> cartNos = carts.stream()
							.map(cart -> cart.getCNo())
							.collect(Collectors.toList());
		System.out.println("cartNos의 값들은: " + cartNos);
		
		cartOptionRepository.deleteByCartEntity_CNoIn(cartNos);
		
		//이제 cart의 모든 값들을 지우자.(delete from cart where u_no = 3;
		cartRepository.deleteAllByUserEntity_UNo(userNo);
		
		//foreign key 이름 설정도 해주는 게 좋을 것 같다.
		
		return ResponseEntity.ok("success");
	}
	

	@Transactional
	@DeleteMapping("/delete/cart")
	public ResponseEntity<?> deleteCart(@RequestParam(value = "CNo", required = false) Long CNo){
		
		//delete from cart_option where c_no = (select c_no from cart where c_no = :CNo);
//		CartEntity cart = cartRepository.findByCNo(CNo);
//		
//		Long cartNo = cart.getCNo();
		
		cartOptionRepository.deleteByCartEntity_CNo(CNo);
		cartRepository.deleteByCNo(CNo);
		
		return ResponseEntity.ok("success");
	}

}
