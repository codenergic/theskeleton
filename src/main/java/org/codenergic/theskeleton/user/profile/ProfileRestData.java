package org.codenergic.theskeleton.user.profile;

import javax.annotation.Nullable;

import org.codenergic.theskeleton.core.data.RestData;
import org.codenergic.theskeleton.user.UserEntity;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.auto.value.AutoValue;

@SuppressWarnings("serial")
@AutoValue
@JsonDeserialize(builder = AutoValue_ProfileRestData.Builder.class)
abstract class ProfileRestData implements RestData {
	static Builder builder() {
		return new AutoValue_ProfileRestData.Builder();
	}

	static Builder builder(UserEntity user) {
		return builder()
			.username(user.getUsername())
			.email(user.getEmail())
			.phoneNumber(user.getPhoneNumber())
			.pictureUrl(user.getPictureUrl());
	}

	@Nullable
	abstract String getEmail();

	@Nullable
	abstract String getPassword();

	@Nullable
	abstract String getPhoneNumber();

	@Nullable
	abstract String getPictureUrl();

	@Nullable
	abstract String getUsername();

	UserEntity toUserEntity() {
		return new UserEntity()
			.setUsername(getUsername())
			.setEmail(getEmail())
			.setPhoneNumber(getPhoneNumber())
			.setPassword(getPassword());
	}

	@AutoValue.Builder
	interface Builder extends RestData.Builder {
		ProfileRestData build();

		Builder email(String email);

		Builder password(String password);

		Builder phoneNumber(String phoneNumber);

		Builder pictureUrl(String url);

		Builder username(String username);
	}
}
