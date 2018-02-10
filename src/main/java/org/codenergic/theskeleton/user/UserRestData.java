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

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.google.auto.value.AutoValue;
import org.codenergic.theskeleton.core.data.RestData;
import org.codenergic.theskeleton.core.web.ValidationConstants;
import org.hibernate.validator.constraints.NotBlank;

import javax.annotation.Nullable;
import javax.validation.constraints.Pattern;
import java.util.Optional;
import java.util.Set;

@SuppressWarnings("serial")
@AutoValue
@JsonDeserialize(builder = AutoValue_UserRestData.Builder.class)
public abstract class UserRestData implements RestData {
	public static Builder builder() {
		return new AutoValue_UserRestData.Builder();
	}

	@Nullable
	abstract Set<String> getAuthorities();

	@NotBlank(groups = {New.class, Existing.class})
	@Nullable
	@Pattern(regexp = ValidationConstants.EMAIL_REGEX, message = "Not a valid email address", groups = {New.class, Existing.class})
	abstract String getEmail();

	@Nullable
	abstract String getId();

	@Nullable
	abstract Boolean getIsNonLocked();

	@Nullable
	abstract String getPassword();

	@Nullable
	abstract String getPhoneNumber();

	@Nullable
	abstract String getPictureUrl();

	@NotBlank(groups = {New.class, Existing.class})
	@Nullable
	abstract String getUsername();

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
	public interface Builder {
		Builder authorities(Set<String> authorities);

		UserRestData build();

		Builder email(String email);

		default Builder fromUserEntity(UserEntity user) {
			return id(user.getId())
				.username(user.getUsername())
				.email(user.getEmail())
				.phoneNumber(user.getPhoneNumber())
				.isNonLocked(user.isAccountNonLocked())
				.pictureUrl(user.getPictureUrl());
		}

		Builder id(String id);

		Builder isNonLocked(Boolean nonLocked);

		Builder password(String password);

		Builder phoneNumber(String phoneNumber);

		Builder pictureUrl(String pictureUrl);

		Builder username(String username);
	}

	interface New {
	}

	interface Existing {
	}
}
