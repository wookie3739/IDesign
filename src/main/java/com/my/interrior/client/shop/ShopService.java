package com.my.interrior.client.shop;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

// import org.glassfish.jaxb.runtime.v2.runtime.output.StAXExStreamWriterOutput;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageImpl;

import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.my.interrior.client.cart.CartEntity;
import com.my.interrior.client.cart.CartOptionEntity;
import com.my.interrior.client.cart.CartRepository;
import com.my.interrior.client.gcs.GCSFileDeleter;
import com.my.interrior.client.shop.shopDTO.ShopDTO;
import com.my.interrior.client.shop.shopDTO.ShopOptionDTO;
import com.my.interrior.client.shop.shopDTO.ShopOptionValueDTO;
import com.my.interrior.client.user.UserEntity;
import com.my.interrior.client.user.UserRepository;

import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;

@Service
public class ShopService {

	@Autowired
	private Storage storage;

	@Autowired
	private ShopRepository shopRepository;

	@Autowired
	private ShopOptionRepository shopOptionRepository;

	@Autowired
	private ShopPhotoRepository shopPhotoRepository;

	@Autowired
	private ShopOptionValueRepository shopOptionValueRepository;

	@Autowired
	private HttpSession session;

	@Autowired
	private ShopReviewRepository shopReviewRepository;
	
	@Autowired
	private ShopReviewPhotoRepository shopReviewPhotoRepository;

	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private GCSFileDeleter gcsFileDeleter;

	@Value("${spring.cloud.gcp.storage.bucket}")
	private String bucketName;

	// GCS 파일 업로드
	public String uploadFile(MultipartFile file) throws IOException {
		// 세션값 받아오기
		String userId = (String) session.getAttribute("UId");
		// 폴더 생성을 위해 user_ + 세션값으로 받기
		String folderName = "user_" + userId;
		// 경로설정 폴더이름 /uuid-원래 파일이름
		String fileName = folderName + "/" + UUID.randomUUID().toString() + "-" + file.getOriginalFilename();
		BlobId blobId = BlobId.of(bucketName, fileName);
		BlobInfo blobInfo = BlobInfo.newBuilder(blobId).setContentType(file.getContentType()).build();
		storage.create(blobInfo, file.getBytes());
		return String.format("https://storage.googleapis.com/%s/%s", bucketName, fileName);
	}

	//
	//
	@Transactional
	public void shopWrite(ShopDTO shopData, String shopMainPhotoUrl,
	                      List<String> descriptionImageUrls) {

		System.out.println("할인률 : " + shopData.getDiscountRate());
		System.out.println("카테고리 : " +shopData.getCategory());
		System.out.println("타이틀 : " +shopData.getTitle());
	    // ShopEntity 저장
	    ShopEntity shopEntity = new ShopEntity();
	    shopEntity.setShopTitle(shopData.getTitle());
	    shopEntity.setShopCategory(shopData.getCategory());
	    shopEntity.setShopDiscont(shopData.getDiscountRate());
	    shopEntity.setShopMainPhoto(shopMainPhotoUrl);
	    shopEntity.setShopWriteTime(LocalDateTime.now());
	    shopEntity.setShopPrice(shopData.getPrice());
	    shopEntity.setShopContent("임시값");

	    shopRepository.save(shopEntity);

	    // 옵션 저장
	    for (ShopOptionDTO option : shopData.getOption()) {
	        ShopOptionEntity optionEntity = new ShopOptionEntity();
	        optionEntity.setShopEntity(shopEntity);
	        optionEntity.setShopOptionName(option.getOptionName());

	        for (ShopOptionValueDTO optionValue : option.getOptions()) {
	            ShopOptionValueEntity valueEntity = new ShopOptionValueEntity();
	            valueEntity.setShopOptionValue(optionValue.getName());
	            valueEntity.setShopOptionPrice(optionValue.getPrice());
	            valueEntity.setShopOptionEntity(optionEntity);
	            shopOptionValueRepository.save(valueEntity);
	        }

	        shopOptionRepository.save(optionEntity);
	    }

	    // 설명 이미지 URL 저장
	    for (String url : descriptionImageUrls) {
	        ShopPhotoEntity photoEntity = new ShopPhotoEntity();
	        photoEntity.setShopEntity(shopEntity);
	        photoEntity.setShopPhotoUrl(url);
	        shopPhotoRepository.save(photoEntity);
	    }
	}


	// 샵 페이지 리스트
	public Page<ShopEntity> getAllShop(Pageable pageable) {
		return shopRepository.findBySDeactivatedFalse(pageable);
	}


	// 샵 검색
	public Page<ShopEntity> searchShops(String shopTitle, String shopCategory, Integer minPrice, Integer maxPrice,
			Pageable pageable) {
		List<ShopEntity> shops = shopRepository.findByShopTitleContainingAndShopCategoryContaining(shopTitle,
				shopCategory);

		BigDecimal minPriceBigDecimal = minPrice != null ? BigDecimal.valueOf(minPrice) : BigDecimal.ZERO;
		BigDecimal maxPriceBigDecimal = maxPrice != null ? BigDecimal.valueOf(maxPrice)
				: BigDecimal.valueOf(Long.MAX_VALUE);

		List<ShopEntity> filteredShops = shops.stream().filter(shop -> {
			BigDecimal price = new BigDecimal(shop.getShopPrice());
			return price.compareTo(minPriceBigDecimal) >= 0 && price.compareTo(maxPriceBigDecimal) <= 0;
		}).collect(Collectors.toList());

		int start = (int) pageable.getOffset();
		int end = Math.min((start + pageable.getPageSize()), filteredShops.size());
		Page<ShopEntity> page = new PageImpl<>(filteredShops.subList(start, end), pageable, filteredShops.size());

		return page;
	}

	public Optional<ShopEntity> getShopById(Long shopNo) {
		return shopRepository.findById(shopNo);
	}

	public List<ShopEntity> getCartsFromShop(List<Long> shopNos) {
		return shopRepository.findByShopNoIn(shopNos);
	}


	public List<ShopPhotoEntity> getShopPhotoById(Long shopNo) {

		List<ShopPhotoEntity> shopPhoto = shopPhotoRepository.findByShopEntity_ShopNo(shopNo);

		return shopPhoto;
	}

	public List<ShopOptionEntity> getShopOptionById(Long shopNo) {

		List<ShopOptionEntity> shopOption = shopOptionRepository.findByShopEntity_ShopNo(shopNo);

		return shopOption;
	}

	public List<ShopOptionEntity> getAllShopOptions() {
		return shopOptionRepository.findAll();
	}
	
	public List<ShopReviewEntity> getShopReviewsByShopNo(Long shopNo) {
        return shopReviewRepository.findByShopEntityShopNo(shopNo);
    }

    public List<ShopReviewPhotoEntity> getShopReviewPhotosByReviewNo(Long shopReviewNo) {
        return shopReviewPhotoRepository.findByShopReviewEntityShopReviewNo(shopReviewNo);
    }
    //조회수 증가
    public void increaseViewCount(Long shopNo) {
        ShopEntity shop = shopRepository.findByShopNo(shopNo);
        shop.setShopHit(shop.getShopHit() + 1);
        shopRepository.save(shop);
    }

    //리뷰 작성
	public void shopReviewWrite(Long shopNo, double starpoint, String shopContent, MultipartFile[] descriptionImages) throws IOException  {
		ShopReviewEntity shopReviewEntity = new ShopReviewEntity();
		Optional<ShopEntity> ShopEntits = shopRepository.findById(shopNo);

	    // ShopEntity가 존재하지 않으면 예외 처리
	    if (!ShopEntits.isPresent()) {
	        throw new RuntimeException("Shop not found with id: " + shopNo);
	    }

	    ShopEntity shopEntity = ShopEntits.get();
		String userId = (String) session.getAttribute("UId");

		// UserEntity 객체 조회
		UserEntity userEntity = userRepository.findByUId(userId);
		shopReviewEntity.setShopEntity(shopEntity);
		shopReviewEntity.setShopReviewStarRating(starpoint);
		shopReviewEntity.setShopReviewContent(shopContent);
		shopReviewEntity.setUser(userEntity);
		shopReviewEntity.setShopReviewCreated(LocalDateTime.now());
		
		shopReviewRepository.save(shopReviewEntity);
		
		
        for (MultipartFile file : descriptionImages) {
        	if (!file.isEmpty()) {
        	ShopReviewPhotoEntity shopReviewPhoto = new ShopReviewPhotoEntity();
            String url = uploadFile(file);
            shopReviewPhoto.setShopReviewPhotoUrl(url);
            shopReviewPhoto.setShopReviewEntity(shopReviewEntity);
            shopReviewPhotoRepository.save(shopReviewPhoto);
        	}
        }
        
	}
	@Transactional
	public void deleteShopReview(Long shopReviewNo) {
		List<ShopReviewPhotoEntity> shopReviewPhotoDel = shopReviewPhotoRepository.findByShopReviewEntityShopReviewNo(shopReviewNo);
        for (ShopReviewPhotoEntity photo : shopReviewPhotoDel) {
            System.out.println("photo : " + photo);
            gcsFileDeleter.deleteFile(photo.getShopReviewPhotoUrl());
        }
        shopReviewPhotoRepository.deleteByShopReviewEntityShopReviewNo(shopReviewNo);
        
        System.out.println("Deleting review with ID: " + shopReviewNo);
        shopReviewRepository.deleteById(shopReviewNo);
        System.out.println("Review deleted successfully.");
    }
	
	public ShopEntity getShopEntityByShopNo(Long shopNo){
	      return shopRepository.findByShopNo(shopNo);
	}
	
	
}
