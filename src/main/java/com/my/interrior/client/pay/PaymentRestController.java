package com.my.interrior.client.pay;

import java.io.IOException;
import java.math.BigDecimal;

import java.time.LocalDate;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.my.interrior.client.user.UserEntity;
import com.my.interrior.client.user.UserRepository;
import com.siot.IamportRestClient.exception.IamportResponseException;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class PaymentRestController {

	private final PaymentService paymentService;
	private final ShipmentService shipmentService;
	private final UserRepository userRepository;
	private final PaymentAndUserRepository paymentAndUserRepository;
	
	@PostMapping("/payment/prepare")
	public ResponseEntity<?> preparePayment(@ModelAttribute PrePaymentEntity request, @RequestParam("validation") int validation)
				throws IamportResponseException, IOException {
		
		BigDecimal amount = (BigDecimal) request.getAmount();
		BigDecimal validate = BigDecimal.valueOf(validation);
		System.out.println("amount의 값은: " + amount);
		System.out.println("validate의 값은: " + validate);
		if(amount.compareTo(validate) == 0) {
			
			request.setAmount(amount);
			
			paymentService.postPrepare(request);
			
			return ResponseEntity.ok("success");
		}else {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
		}
		
	}
	
	
	//결제 정보 저장
	//어떤 유저가 결제했는 지도 저장하자
	@PostMapping("/save/payment")
	public ResponseEntity<PayEntity> savePayment(@RequestBody PayEntity pay, HttpSession session) throws Exception{
		System.out.println("pay.getPaidAt(): " + pay.getPaidAt());
		pay.setPaidAt(LocalDate.now());
		
		paymentService.saveMyPayment(pay);
		
//		Long payNo = pay.getPayNo();
//		System.out.println("payNo의 값은: " + payNo);
		String userId = (String) session.getAttribute("UId");
		//u_no, pay_no 저장하기
		UserEntity user = userRepository.findByUId(userId);
//		Long userNo = user.getUNo();
		
		PaymentAndUserEntity pau = new PaymentAndUserEntity();
		pau.setPayEntity(pay);
		pau.setUserEntity(user);
		
		paymentAndUserRepository.save(pau);
		
		return ResponseEntity.ok(pay);
	}
	
	@PostMapping("/save/shipment")
	public ResponseEntity<ShipmentEntity> saveShipment(@RequestBody ShipmentEntity ship) throws Exception{
		shipmentService.saveMyShipment(ship);
		
		return ResponseEntity.ok(ship);
	}
	
	// payment랑 shipment를 담아줘야함.
		// 부득이하게 URL 쿼리 문자열이 너무 길어져서 Post를 사용함.
		@PostMapping("/payment/info")
		public ResponseEntity<?> goToPaymentInfo(@RequestBody PaymentAndShipmentDTO dto, HttpSession session) throws Exception {
			PaymentAndShipmentDTO.PaymentInfo paymentInfo = dto.getPaymentInfo();
			PaymentAndShipmentDTO.ShipmentInfo shipmentInfo = dto.getShipmentInfo();

			System.out.println("paymentInfo: " + paymentInfo);
			System.out.println("shipmentInfo: " + shipmentInfo);

			session.setAttribute("paymentRes", paymentInfo);
			session.setAttribute("shipmentRes", shipmentInfo);

			return ResponseEntity.ok("success");
		}
	
}
