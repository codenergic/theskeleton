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
package org.codenergic.theskeleton.user;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import org.codenergic.theskeleton.core.data.RestData;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;

@SuppressWarnings("serial")
public class UserRestData implements RestData {
	@JsonProperty
	private String id;
	@JsonProperty
	private String username;
	@JsonProperty
	private String email;
	@JsonProperty
	private String phoneNumber;
	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	private String password;
	@JsonProperty(access = Access.WRITE_ONLY)
	private Set<String> authorities = new HashSet<>();

	private UserRestData() {
	}

	private UserRestData(Builder builder) {
		this.id = builder.id;
		this.username = builder.username;
		this.email = builder.email;
		this.phoneNumber = builder.phoneNumber;
		this.password = builder.password;
		this.authorities = builder.authorities;
	}

	public String getId() {
		return id;
	}

	public String getUsername() {
		return username;
	}

	public String getEmail() {
		return email;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public String getPassword() {
		return password;
	}

	public Set<String> getAuthorities() {
		return authorities;
	}

	public UserEntity toEntity() {
		UserEntity userEntity = new UserEntity();
		userEntity.setId(id);
		userEntity.setUsername(username);
		userEntity.setEmail(email);
		userEntity.setPhoneNumber(phoneNumber);
		userEntity.setPassword(password);
		return userEntity;
	}

	/**
	 * Creates builder to build {@link UserRestData}.
	 *
	 * @return created builder
	 */
	public static Builder builder() {
		return new Builder();
	}

	public static Builder builder(UserEntity user) {
		return builder().withId(user.getId()).withUsername(user.getUsername()).withEmail(user.getEmail())
				.withPhoneNumber(user.getPhoneNumber()).withAuthorities(
						user.getAuthorities().stream().map(UserRoleEntity::getAuthority).collect(Collectors.toSet()));
	}

	/**
	 * Builder to build {@link UserRestData}.
	 */
	public static final class Builder {
		private String id;
		private String username;
		private String email;
		private String phoneNumber;
		private String password;
		private Set<String> authorities;

		private Builder() {
		}

		public Builder withId(String id) {
			this.id = id;
			return this;
		}

		public Builder withUsername(String username) {
			this.username = username;
			return this;
		}

		public Builder withEmail(String email) {
			this.email = email;
			return this;
		}

		public Builder withPassword(String password) {
			this.password = password;
			return this;
		}

		public Builder withPhoneNumber(String phoneNumber) {
			this.phoneNumber = phoneNumber;
			return this;
		}

		public Builder withAuthorities(Set<String> authorities) {
			this.authorities = authorities;
			return this;
		}

		public UserRestData build() {
			return new UserRestData(this);
		}
	}
}
