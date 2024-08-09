package com.my.interrior.admin.page;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;


import com.my.interrior.client.csc.notice.NoticeEntity;
import com.my.interrior.client.csc.notice.NoticeRepository;
import com.my.interrior.client.evaluation.ReviewRepository;
import com.my.interrior.client.shop.ShopRepository;
import com.my.interrior.client.shop.ShopReviewRepository;
import com.my.interrior.client.user.UserEntity;
import com.my.interrior.client.user.UserRepository;

import jakarta.transaction.Transactional;

@Service
public class AdminPageService {
	
	@Autowired
	private NoticeRepository noticeRepository;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private ShopRepository shopRepository;
	
	@Autowired
	private ReviewRepository reviewRepository;
	
	@Autowired
	private ShopReviewRepository shopReviewRepository;
	
	@Autowired
	private NoticeRepository noticerepository;
	
	//유저 카운트
	public long getUserCount() {
        return userRepository.count();
    }
	//상점 카운트
	public long getShopCount() {
		return shopRepository.count();
	}
	//리뷰 카운트
	public long getReviewCount() {
		return reviewRepository.count();
	}
	
	public List<UserWithPostAndCommentCount> findAllUsersWithCounts() {
        List<UserEntity> users = userRepository.findAll();
        List<UserWithPostAndCommentCount> userCounts = new ArrayList<>();

        for (UserEntity user : users) {
        	int postCount = reviewRepository.countByUser(user);
        	int commentCount = shopReviewRepository.countByUser(user);
        	UserWithPostAndCommentCount dto = new UserWithPostAndCommentCount(user, postCount, commentCount);
        	userCounts.add(dto);
        }

        return userCounts;
    }
	public Page<NoticeEntity> getAllNotice(Pageable pageable){
		return noticerepository.findAll(pageable);
	}
	
	@Transactional
    public void deleteNotice(Long notNo) {
        noticeRepository.deleteById(notNo);
    }
	
}
