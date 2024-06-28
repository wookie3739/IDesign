package com.my.interrior.client.user;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<UserEntity, Long> {
//save뺴고는 여기다가 다 박아라

    UserEntity findByUIdAndUPw(String UId,String UPw);
    
    UserEntity findByUId(String UId);
    
    UserEntity findByUMail(String email);
    
    UserEntity findByUPwAndUMail(String UPw,String UMail);
    
    UserEntity findByUMailAndUName(String UMail, String UName);

    boolean existsByUId(String Uid);
}
