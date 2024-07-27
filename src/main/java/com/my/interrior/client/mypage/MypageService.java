package com.my.interrior.client.mypage;

import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.my.interrior.client.gcs.GCSFileDeleter;
import com.my.interrior.client.user.UserEntity;
import com.my.interrior.client.user.UserRepository;

import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;

import java.io.IOException;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class MypageService {

    @Autowired
    private MypageRepository mypageRepository;
    
    @Autowired
	private Storage storage;
    
    @Autowired
	private HttpSession session;
    
    @Autowired
	private UserRepository userRepository;
    
    @Value("${spring.cloud.gcp.storage.bucket}")
	private String bucketName;
    
    @Autowired
    private GCSFileDeleter gcsFileDeleter;

    public UserEntity getUserInfo(String UId) {

        return mypageRepository.findByUId(UId);
    }
    
    public void deleteUserInfo(String UId) {
    	mypageRepository.deleteByUId(UId);
    	
    } 
   
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
    @Transactional
    public void updateUser(MultipartFile file, String UName, String UBirth,
    		String UMail, String UTel) throws IOException {
    	String profile = null;
    	if(file != null) {
    		profile = uploadFile(file);
    	}
    	String userId = (String) session.getAttribute("UId");
    	
    	UserEntity userEntity = userRepository.findByUId(userId);
    	
    	
    	userEntity.setUName(UName);
    	if(profile != null) {
    		String deleteGCSFileName = userEntity.getUPofile();
    		gcsFileDeleter.deleteFile(deleteGCSFileName);
    		userEntity.setUPofile(profile);
    	}
    	userEntity.setUBirth(UBirth);
    	userEntity.setUMail(UMail);
    	userEntity.setUTel(UTel);
    	userRepository.save(userEntity);
    }
    
    public void deleteUser(Long uNo) {
    	UserEntity userEntity = userRepository.findById(uNo).orElse(null);
    	
    	String deleteGCSFileName = userEntity.getUPofile();
    	gcsFileDeleter.deleteFile(deleteGCSFileName);
    	
    }

}
