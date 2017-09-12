package org.codenergic.theskeleton.user.profile;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.google.auto.value.AutoValue;
import org.codenergic.theskeleton.core.data.RestData;
import org.codenergic.theskeleton.user.UserEntity;

import javax.annotation.Nullable;

@SuppressWarnings("serial")
@AutoValue
@JsonDeserialize(builder = AutoValue_ProfileRestData.Builder.class)
abstract class ProfileRestData implements RestData {
	@Nullable
	abstract String getUsername();

	@Nullable
	abstract String getEmail();

	@Nullable
	abstract String getPhoneNumber();

	@Nullable
	abstract String getPassword();

	@Nullable
	abstract String getPictureUrl();

	static Builder builder() {
		return new AutoValue_ProfileRestData.Builder();
	}

	UserEntity toUserEntity() {
		return new UserEntity()
			.setUsername(getUsername())
			.setEmail(getEmail())
			.setPhoneNumber(getPhoneNumber())
			.setPassword(getPassword());
	}

	@AutoValue.Builder
	@JsonPOJOBuilder(withPrefix = "")
	interface Builder {
		Builder username(String username);

		Builder email(String email);

		Builder phoneNumber(String phoneNumber);

		Builder password(String password);

		Builder pictureUrl(String url);

		ProfileRestData build();

		default Builder fromUserEntity(UserEntity user) {
			return username(user.getUsername())
				.email(user.getEmail())
				.phoneNumber(user.getPhoneNumber())
				.pictureUrl(user.getPictureUrl());
		}
	}
}
