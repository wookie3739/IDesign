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
import lombok.ToString;

@Entity(name="shop_option_value")
@Getter
@Setter
@ToString
public class ShopOptionValueEntity {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long shopOptionValueNo;
	
	@Column(name="shop_option_value")
	private String shopOptionValue;
	
	@Column(name="shop_option_price")
	private int shopOptionPrice;
	
	@ManyToOne
	@JoinColumn(name="shop_option_no")
	private ShopOptionEntity shopOptionEntity;

}
