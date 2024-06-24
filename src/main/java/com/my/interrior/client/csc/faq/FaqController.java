package com.my.interrior.client.csc.faq;

import java.time.LocalDate;
import org.springframework.web.util.UriComponentsBuilder;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.my.interrior.client.user.UserEntity;
import com.my.interrior.client.user.UserRepository;

import jakarta.servlet.http.HttpSession;

@Controller
public class FaqController {

	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private FaqRepository faqRepository;
	
	@Autowired
	private FaqService faqService;
	
	@GetMapping("/board/faqWrite")
	public String faqWrite() {
		return "admin/csc/faqWrite";
	}
	
	@PostMapping("/board/faqWrite")
	public String faqWrite(@ModelAttribute FaqEntity faq, HttpSession session) {

		faq.setFaqRegisteredDate(LocalDate.now());
		//이름도 가져오기
		String UId = (String) session.getAttribute("UId");
		UserEntity user = userRepository.findByUId(UId);
		faq.setFaqAuthor(user.getUName());
		faq.setUserEntity(user);
		
		faqRepository.save(faq);
		
		return "admin/csc/faqWrite";
		
	}
	//자주 묻는 질문 상세페이지
	@GetMapping("/board/faq/{no}")
	public String faqDetail(@PathVariable("no") Long faqNo, Model model) {
		
		FaqEntity faq = faqService.getFaqDetailByNo(faqNo);
		
		model.addAttribute("faq", faq);
		
		return "client/csc/faqDetail";
	}
	
	//카테고리별 페이지
	@GetMapping("/board/faq/category/{encodedCategory}")
	public String faqCategory(@PathVariable("encodedCategory") String encodedCategory,
														Model model,
							  @RequestParam(name = "page", defaultValue = "0") int page,
							  @RequestParam(name = "size", defaultValue = "10") int size) {
		String category = encodedCategory.replace("/", "-");
		System.out.println("category의 값은? : " + category);
		Page<FaqEntity> faqs = faqService.getFaqsByCategory(category, PageRequest.of(page, size));
		System.out.println("faqs의 값은?(카테고리별 페이지)" + faqs);
		model.addAttribute("faqs", faqs);
		model.addAttribute("categories", faqService.getAllCategories());
		
		return "client/csc/csc";
	}
}
