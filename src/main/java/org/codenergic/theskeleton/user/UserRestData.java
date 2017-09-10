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

import java.util.Optional;
import java.util.Set;

import javax.annotation.Nullable;

import org.codenergic.theskeleton.core.data.RestData;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.google.auto.value.AutoValue;

@SuppressWarnings("serial")
@AutoValue
@JsonDeserialize(builder = AutoValue_UserRestData.Builder.class)
abstract class UserRestData implements RestData {
	@Nullable
	abstract String getId();

	@Nullable
	abstract String getUsername();

	@Nullable
	abstract String getEmail();

	@Nullable
	abstract String getPhoneNumber();

	@Nullable
	abstract String getPassword();

	@Nullable
	abstract Set<String> getAuthorities();

	@Nullable
	abstract Boolean getIsNonLocked();

	static Builder builder() {
		return new AutoValue_UserRestData.Builder();
	}

	UserEntity toUserEntity() {
		return new UserEntity()
			.setId(getId())
			.setUsername(getUsername())
			.setEmail(getEmail())
			.setPhoneNumber(getPhoneNumber())
			.setPassword(getPassword())
			.setAccountNonLocked(Optional.ofNullable(getIsNonLocked()).orElse(false));
	}

	@AutoValue.Builder
	@JsonPOJOBuilder(withPrefix = "")
	interface Builder {
		Builder id(String id);

		Builder username(String username);

		Builder email(String email);

		Builder phoneNumber(String phoneNumber);

		Builder password(String password);

		Builder authorities(Set<String> authorities);

		Builder isNonLocked(Boolean nonLocked);

		UserRestData build();

		default Builder fromUserEntity(UserEntity user) {
			return id(user.getId())
				.username(user.getUsername())
				.email(user.getEmail())
				.phoneNumber(user.getPhoneNumber())
				.password(user.getPassword())
				.isNonLocked(user.isAccountNonLocked());
		}
	}
}
