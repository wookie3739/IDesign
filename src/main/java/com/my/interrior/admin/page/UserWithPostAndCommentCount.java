package com.my.interrior.admin.page;

import com.my.interrior.client.user.UserEntity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserWithPostAndCommentCount {
	private UserEntity user;
	private int postCount;
	private int commentCount;

	public UserWithPostAndCommentCount(UserEntity user, int postCount, int commentCount) {
		this.user = user;
		this.postCount = postCount;
		this.commentCount = commentCount;
	}
}
