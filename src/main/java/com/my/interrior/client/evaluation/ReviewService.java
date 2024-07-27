package com.my.interrior.client.evaluation;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.my.interrior.client.gcs.GCSFileDeleter;
import com.my.interrior.client.user.UserEntity;
import com.my.interrior.client.user.UserRepository;

import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;

@Service
public class ReviewService {
	// GCS 빈 가져오기
	@Autowired
	private Storage storage;
	@Autowired
	// 레퍼지토리 가져오기
	private ReviewRepository reviewRepository;
	// 세션값 받아오기
	@Autowired
	private HttpSession session;
	@Autowired
	private ReviewPhotoRepository reviewPhotoRepository;
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private GCSFileDeleter gcsFileDeleter;

	// 버킷 이름 가져오기
	@Value("${spring.cloud.gcp.storage.bucket}")
	private String bucketName;

	//gcs파일 업로드
	public String uploadFile(MultipartFile file) throws IOException {
		// 세션값 받아오기
		String userId = (String) session.getAttribute("UId");
		// 풀더 생성을 위해 user_ + 세션값으로 받기
		String folderName = "user_" + userId;
		// 경로설정 풀더이름 /uuid-원래 파일이름
		String fileName = folderName + "/" + UUID.randomUUID().toString() + "-" + file.getOriginalFilename();
		BlobId blobId = BlobId.of(bucketName, fileName);
		BlobInfo blobInfo = BlobInfo.newBuilder(blobId).setContentType(file.getContentType()).build();
		storage.create(blobInfo, file.getBytes());
		return String.format("https://storage.googleapis.com/%s/%s", bucketName, fileName);
	}

	// 후기 업로드
	@Transactional
	public void uploadFileAndCreateReview(String title, String category, String content, String starRating,
			MultipartFile[] files, MultipartFile mainPhoto) throws IOException {

		List<String> fileUrls = new ArrayList<>();
		for (MultipartFile file : files) {
			String fileUrl = uploadFile(file);
			fileUrls.add(fileUrl);
		}
		
		// 썸네일 사진 업로드
	    String mainPhotoUrl = uploadFile(mainPhoto);
		// 작성자 id는 세션에서 가져옴
		String userId = (String) session.getAttribute("UId");

		// UserEntity 객체 조회
		UserEntity userEntity = userRepository.findByUId(userId);


		// 작성 시간 설정
		LocalDateTime writtenTime = LocalDateTime.now();

		// 조회수 초기화
		int views = 0;

		// 임시 프로필 string값
		

		// 리뷰 저장
		ReviewEntity review = new ReviewEntity();

		Long rNo = review.getRNo();

		System.out.println("r_no값 확인 : " + rNo);
		review.setRTitle(title);
		review.setRCategory(category);
		review.setRContent(content);
		review.setRStarRating(starRating);
		review.setRMainPhoto(mainPhotoUrl);
		review.setRViews(views);
		review.setRWrittenTime(writtenTime);
		review.setUser(userEntity);
		reviewRepository.save(review);

		List<ReviewPhotoEntity> reviewPhotos = new ArrayList<>();
		for (String fileUrl : fileUrls) {
			ReviewPhotoEntity reviewPhoto = new ReviewPhotoEntity();
			reviewPhoto.setRpPhoto(fileUrl);
			reviewPhoto.setReview(review);
			reviewPhotos.add(reviewPhoto);
		}

		// reviewPhotoRepository 사용하여 사진 저장
		reviewPhotoRepository.saveAll(reviewPhotos);

	}

	// 페이징 처리된 후기 목록 가져오기
	public Page<ReviewEntity> getAllReviews(Pageable pageable) {
		return reviewRepository.findAll(pageable);
	}
	
	// 페이징 처리된 후기 목록 가져오기(사진)
	public List<ReviewPhotoEntity> getReviewPhotosByReviewNo(Long rNo) {
	    return reviewPhotoRepository.findByReview_RNo(rNo);
	}

	// 후기 상세 페이지
	public Optional<ReviewEntity> getReviewById(Long rNo) {
		return reviewRepository.findById(rNo);
	}
	
	//public int updateHits(Long rNo) {
	//	return reviewRepository.updateHits(rNo);
	//}
	// 후기 상세 페이지(사진)
	public List<ReviewPhotoEntity> getPhotosByReviewId(Long rNo) {
	    // 1. 리뷰 번호(rNo)를 사용하여 리뷰 포토 엔티티(ReviewPhotoEntity)를 조회합니다.
		 List<ReviewPhotoEntity> reviewPhotos = reviewPhotoRepository.findByReview_RNo(rNo);
		 
		 System.out.println("백엔드 rno의 값 : " + rNo);
		 System.out.println("백엔드 reviewphotos :" + reviewPhotos);
	    
	    // 2. 조회된 리뷰 포토 목록을 반환합니다.
	    return reviewPhotos;
	}
	@Transactional
	public void updateReview(Long rNo, String title, String category, String content, String starRating,
			MultipartFile[] files, MultipartFile mainPhoto) throws IOException {
		
	    // 썸네일 사진 업로드 없을시에 null값
	    String mainPhotoUrl = null;
	    if(!mainPhoto.isEmpty()) {
	    	mainPhotoUrl = uploadFile(mainPhoto);
	    }

	    // 리뷰 조회 또는 새로 생성
	    ReviewEntity review;
	    review = reviewRepository.findById(rNo).orElse(null);

	    // 리뷰 정보 설정
	    review.setRTitle(title);
	    review.setRCategory(category);
	    review.setRContent(content);
	    review.setRStarRating(starRating);
	    if(mainPhotoUrl != null) {
	    	String deletephoto = review.getRMainPhoto();
	    	gcsFileDeleter.deleteFile(deletephoto);
	    	review.setRMainPhoto(mainPhotoUrl);
	    }
	    // 리뷰 저장
	    reviewRepository.save(review);
	    
	    List<String> fileUrls = new ArrayList<>();
	    for (MultipartFile file : files) {
	        if (!file.isEmpty()) {
	            String fileUrl = uploadFile(file);
	            fileUrls.add(fileUrl);
	        }
	    }

	    // 기존 리뷰 사진 삭제
	    if (rNo != null && !fileUrls.isEmpty()) {
	        List<ReviewPhotoEntity> reviewPhotoDel = reviewPhotoRepository.findByReview_RNo(rNo);
	        for(ReviewPhotoEntity photo : reviewPhotoDel) {
	        	gcsFileDeleter.deleteFile(photo.getRpPhoto());
	        	reviewPhotoRepository.delete(photo);
	        }
	    }

	    // 새로운 리뷰 사진 저장
	    if (!fileUrls.isEmpty()) {
	        List<ReviewPhotoEntity> reviewPhotos = new ArrayList<>();
	        for (String fileUrl : fileUrls) {
	            ReviewPhotoEntity reviewPhoto = new ReviewPhotoEntity();
	            reviewPhoto.setRpPhoto(fileUrl);
	            reviewPhoto.setReview(review);
	            reviewPhotos.add(reviewPhoto);
	        }
	        reviewPhotoRepository.saveAll(reviewPhotos);
	    }
		
	}
	public void deleteReview(Long rNo) {
		ReviewEntity reviewEntity = reviewRepository.findById(rNo).orElse(null);
		
		String deleteGCSFileName = reviewEntity.getRMainPhoto();
		gcsFileDeleter.deleteFile(deleteGCSFileName);
		List<ReviewPhotoEntity> reviewPhotoDel = reviewPhotoRepository.findByReview_RNo(rNo);
        for(ReviewPhotoEntity photo : reviewPhotoDel) {
        	gcsFileDeleter.deleteFile(photo.getRpPhoto());
        }
        reviewPhotoRepository.deleteByReviewRNo(rNo);
        
        reviewRepository.deleteById(rNo);
	}
}
