package org.codenergic.theskeleton.user.profile;

import javax.annotation.Nullable;

import org.codenergic.theskeleton.core.data.RestData;
import org.immutables.value.Value;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@Value.Immutable
@JsonDeserialize(builder = ImmutableProfileRestData.Builder.class)
interface ProfileRestData extends RestData {
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
}
