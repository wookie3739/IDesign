package com.my.interrior.client.shop;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

@Entity(name = "shop_review_photo")
@Getter
@Setter
public class ShopReviewPhotoEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long shopReviewPhotoNo;
	
	@Column
	private String shopReviewPhotoUrl;
	
	@ManyToOne
	@JoinColumn(name = "shop_review_no", referencedColumnName = "shopReviewNo")
	private ShopReviewEntity shopReviewEntity;
}
