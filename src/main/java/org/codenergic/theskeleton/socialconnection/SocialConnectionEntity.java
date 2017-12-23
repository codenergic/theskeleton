/*
 * Copyright 2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.codenergic.theskeleton.socialconnection;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;

import org.codenergic.theskeleton.core.data.AbstractAuditingEntity;
import org.codenergic.theskeleton.user.UserEntity;

@SuppressWarnings("serial")
@Entity
@Table(name = "ts_social_connection", uniqueConstraints = {
		@UniqueConstraint(columnNames = { "user_id", "provider", "rank" }),
		@UniqueConstraint(columnNames = { "user_id", "provider", "provider_user_id" }) })
public class SocialConnectionEntity extends AbstractAuditingEntity {
	@NotNull
	@Lob
	@Column(name = "access_token")
	private String accessToken;
	@Column(name = "display_name")
	private String displayName;
	@NotNull
	@Column(name = "expire_time")
	private Long expireTime;
	@Lob
	@Column(name = "image_url")
	private String imageUrl;
	@Lob
	@Column(name = "profile_url")
	private String profileUrl;
	private String provider;
	@Column(name = "provider_user_id")
	private String providerUserId;
	@NotNull
	private int rank;
	@Lob
	@Column(name = "refresh_token")
	private String refreshToken;
	@Lob
	private String secret;
	@NotNull
	@ManyToOne
	@JoinColumn(name = "user_id")
	private UserEntity user;

	@Override
	public SocialConnectionEntity setId(String id) {
		super.setId(id);
		return this;
	}

	public String getAccessToken() {
		return accessToken;
	}
	public SocialConnectionEntity setAccessToken(String accessToken) {
		this.accessToken = accessToken;
		return this;
	}
	public String getDisplayName() {
		return displayName;
	}
	public SocialConnectionEntity setDisplayName(String displayName) {
		this.displayName = displayName;
		return this;
	}
	public Long getExpireTime() {
		return expireTime;
	}
	public SocialConnectionEntity setExpireTime(Long expireTime) {
		this.expireTime = expireTime;
		return this;
	}
	public String getImageUrl() {
		return imageUrl;
	}
	public SocialConnectionEntity setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
		return this;
	}
	public String getProfileUrl() {
		return profileUrl;
	}
	public SocialConnectionEntity setProfileUrl(String profileUrl) {
		this.profileUrl = profileUrl;
		return this;
	}
	public String getProvider() {
		return provider;
	}
	public SocialConnectionEntity setProvider(String provider) {
		this.provider = provider;
		return this;
	}
	public String getProviderUserId() {
		return providerUserId;
	}
	public SocialConnectionEntity setProviderUserId(String providerUserId) {
		this.providerUserId = providerUserId;
		return this;
	}
	public int getRank() {
		return rank;
	}
	public SocialConnectionEntity setRank(int rank) {
		this.rank = rank;
		return this;
	}
	public String getRefreshToken() {
		return refreshToken;
	}
	public SocialConnectionEntity setRefreshToken(String refreshToken) {
		this.refreshToken = refreshToken;
		return this;
	}
	public String getSecret() {
		return secret;
	}
	public SocialConnectionEntity setSecret(String secret) {
		this.secret = secret;
		return this;
	}
	public UserEntity getUser() {
		return user;
	}
	public SocialConnectionEntity setUser(UserEntity user) {
		this.user = user;
		return this;
	}
}
