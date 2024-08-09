package com.my.interrior.admin.page;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.my.interrior.client.csc.notice.NoticeEntity;
import com.my.interrior.client.evaluation.ReviewEntity;
import com.my.interrior.client.evaluation.ReviewRepository;
import com.my.interrior.client.evaluation.ReviewService;
import com.my.interrior.client.shop.ShopReviewEntity;
import com.my.interrior.client.shop.ShopReviewRepository;
import com.my.interrior.client.shop.ShopService;
import com.my.interrior.client.user.UserDTO;
import com.my.interrior.client.user.UserEntity;
import com.my.interrior.client.user.UserService;

import jakarta.servlet.http.HttpSession;

@Controller
public class AdminPageController {

	private static final int PAGE_SIZE = 10;

	@Autowired
	private UserService userService;

	@Autowired
	private AdminPageService adminPageService;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private ReviewRepository reviewRepository;

	@Autowired
	private ReviewService reviewService;

	@Autowired
	private ShopReviewRepository shopReviewRepository;

	@Autowired
	private ShopService shopService;

	@GetMapping("/auth/adminLogin")
	public String AdminLogin() {
		return "admin/page/adminLogin";
	}

	@PostMapping("/auth/adminlogin")
	public String adminjoin(@ModelAttribute UserDTO userDTO, HttpSession session, Model model) throws Exception {
		try {
			if (!"admin".equals(userDTO.getUId())) {
				model.addAttribute("loginError", "관리자 아이디를 입력하세요.");
				return "admin/page/adminLogin";
			}

			if (userDTO.getUPw() == null || userDTO.getUPw().isEmpty()) {
				model.addAttribute("loginError", "비밀번호를 입력하세요.");
				return "admin/page/adminLogin";
			}
			String UId = userDTO.getUId();
			String UPw = userDTO.getUPw();
			UserEntity user = userService.checkLogin(UId);
			System.out.println("user" + user);
			if (user != null && passwordEncoder.matches(UPw, user.getUPw())) {
				session.setAttribute("UId", user.getUId());
				return "redirect:/admin/page/adminIndex";
			} else {
				model.addAttribute("loginError", "입력하신 정보를 확인하세요.");
				return "admin/page/adminLogin";
			}
		} catch (Exception e) {
			e.printStackTrace();
			return "error";
		}
	}

	// 인덱스 페이지
	@GetMapping("/admin/page/adminIndex")
	public String adminIndex(HttpSession session, Model model) {
		String adminId = (String) session.getAttribute("UID");
		// 유저 수 체크
		Long userCount = adminPageService.getUserCount();
		System.out.println("유저 수는 : " + userCount);
		model.addAttribute("userCount", userCount);
		// 상점 후기 체크
		Long shopCount = adminPageService.getShopCount();
		System.out.println("쇼핑몰 후기 수는  : " + shopCount);
		model.addAttribute("shopCount", shopCount);
		// 리뷰 수
		Long reviewCount = adminPageService.getReviewCount();
		System.out.println("리뷰의 수 : " + reviewCount);
		model.addAttribute("reviewCount", reviewCount);

		return "/admin/page/adminIndex";
	}

	// 회원정보 페이지
	@GetMapping("/admin/page/adminUsers")
	public String adminUsers(Model model) {
		// findAllUsersWithCounts() 메서드는 UserWithPostAndCommentCount 리스트를 반환함
		List<UserWithPostAndCommentCount> usersWithCounts = adminPageService.findAllUsersWithCounts();
		model.addAttribute("usersWithCounts", usersWithCounts);

		return "/admin/page/adminUsers"; // 뷰의 이름을 반환
	}

	// 어드민 페이지 게시글 모달
	@GetMapping("/fetchPosts")
	@ResponseBody
	public List<ReviewEntity> fetchPosts(@RequestParam("userUNo") Long userUNo) {
		// ReviewRepository에서 UNo 기준으로 데이터 가져오기
		return reviewRepository.findByUserUNo(userUNo);
	}

	// 어드민 페이지 게시글 모달 삭제
	@DeleteMapping("/deletePost")
	public ResponseEntity<Void> deletePost(@RequestParam("rNo") Long rNo) {
		reviewService.deleteReview(rNo);
		return ResponseEntity.ok().build();
	}

	// 어드민 페이지 댓글 모달
	@GetMapping("/fetchComments")
	@ResponseBody
	public List<ShopReviewEntity> fetchComments(@RequestParam("userUNo") Long userUNo) {
		return shopReviewRepository.findByUserUNo(userUNo);
	}

	// 어드민 페이지 댓글 모달 삭제
	@DeleteMapping("/deleteComment")
	public ResponseEntity<Void> deleteComment(@RequestParam("shopReviewNo") Long shopReviewNo) {
		shopService.deleteShopReview(shopReviewNo);
		return ResponseEntity.ok().build();
	}

	// 공지사항
	@GetMapping("/admin/page/adminNotice")
	public String adminNoticeList(Model model, Pageable pageable) {
		Page<NoticeEntity> notices = adminPageService.getAllNotice(PageRequest.of(pageable.getPageNumber(), PAGE_SIZE));
		model.addAttribute("notices", notices);
		model.addAttribute("currentPage", pageable.getPageNumber());
		model.addAttribute("totalPages", notices.getTotalPages());
		return "/admin/page/adminNotice";
	}

	// 공지사항 삭제
	@DeleteMapping("/deleteNotice")
	@ResponseBody
	public ResponseEntity<Void> deleteNotice(@RequestParam("noticeNo") Long noticeNo) {
		adminPageService.deleteNotice(noticeNo);
		return ResponseEntity.ok().build();
	}
}
