package com.my.interrior.client.gcs;

import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.Storage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class GCSFileDeleter {

    private final Storage storage;
    
    @Value("${spring.cloud.gcp.storage.bucket}")
	private String bucketName;

    @Autowired
    public GCSFileDeleter(Storage storage) {
        this.storage = storage;
    }

    public void deleteFile(String fileUrl) {
        // URL에서 객체 이름과 폴더 이름 추출
    	String[] urlParts = fileUrl.split("/", 5);
        if (urlParts.length < 4) {
            throw new IllegalArgumentException("Invalid URL format: " + fileUrl);
        }
        
        String objectName = urlParts[4];
        System.out.println("오브젝트 이름 :  " + objectName );
        // BlobId 생성
        BlobId blobId = BlobId.of(bucketName, objectName);

        // 파일 삭제
        boolean deleted = storage.delete(blobId);
        if (deleted) {
            System.out.println("File deleted successfully.");
        } else {
            System.out.println("File not found.");
        }
    }
}
