package com.my.interrior.client.shop;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.format.annotation.DateTimeFormat;

import com.my.interrior.client.user.UserEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

@Entity(name = "shop_review")
@Getter
@Setter
public class ShopReviewEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long shopReviewNo;
	
	@Column(name = "shop_review_created")
    private LocalDateTime shopReviewCreated;
	
	@Column(columnDefinition = "TEXT" , name = "shop_review_content")
	private String shopReviewContent;
	
	@Column(name = "shop_review_star_rating")
	private double shopReviewStarRating;
	
	@ManyToOne
	@JoinColumn(name = "u_Id", referencedColumnName = "u_id")
	private UserEntity user;
	
	@ManyToOne
	@JoinColumn(name = "shop_no")
	private ShopEntity shopEntity;
}
