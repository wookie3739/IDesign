package com.my.interrior.client.user;

import java.time.LocalDate;
import java.util.List;

import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.my.interrior.client.csc.faq.FaqEntity;
import com.my.interrior.client.csc.inquiry.InquiryEntity;
import com.my.interrior.client.csc.notice.NoticeEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;


@Entity(name = "user")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"notices", "Inquiries", "faq"})
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonProperty("UNo")
    @Column(name = "u_no")
    private Long UNo;

    @JsonProperty("UId")
    @Column(nullable = false, unique = true, name = "u_id")
    private String UId;

    @JsonProperty("UPw")
    @Column(nullable = false, name = "u_pw")
    private String UPw;

    @JsonProperty("UName")
    @Column(nullable = false, name = "u_name")
    private String UName;

    @JsonProperty("UMail")
    @Column(nullable = false, name = "u_mail")
    private String UMail;

    @JsonProperty("UBirth")
    @Column(nullable = false, name = "u_birth")
    private String UBirth;

    @JsonProperty("UTel")
    @Column(nullable = false, name = "u_tel")
    private String UTel;

    @JsonProperty("URegister")
    @Column(name = "u_register")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Builder.Default
    private LocalDate URegister = LocalDate.now();
    
    @OneToMany(mappedBy = "userEntity")
    private List<NoticeEntity> notices;
    
    @OneToMany(mappedBy = "userEntity")
    private List<InquiryEntity> Inquiries;
    
    @OneToMany(mappedBy = "userEntity")
    private List<FaqEntity> faq;

}
