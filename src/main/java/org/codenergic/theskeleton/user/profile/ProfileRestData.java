package org.codenergic.theskeleton.user.profile;

import javax.annotation.Nullable;

import org.codenergic.theskeleton.core.data.RestData;
import org.codenergic.theskeleton.user.UserEntity;
import org.immutables.value.Value;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@Value.Immutable
@JsonDeserialize(builder = ImmutableProfileRestData.Builder.class)
interface ProfileRestData extends RestData {
	static ImmutableProfileRestData.Builder builder() {
		return ImmutableProfileRestData.builder();
	}

	static ImmutableProfileRestData.Builder builder(UserEntity user) {
		return builder()
			.username(user.getUsername())
			.email(user.getEmail())
			.phoneNumber(user.getPhoneNumber())
			.pictureUrl(user.getPictureUrl());
	}

	@Nullable
	String getEmail();

	@Nullable
	String getPassword();

	@Nullable
	String getPhoneNumber();

	@Nullable
	String getPictureUrl();

	@Nullable
	String getUsername();

	default UserEntity toUserEntity() {
		return new UserEntity()
			.setUsername(getUsername())
			.setEmail(getEmail())
			.setPhoneNumber(getPhoneNumber())
			.setPassword(getPassword());
	}
}
