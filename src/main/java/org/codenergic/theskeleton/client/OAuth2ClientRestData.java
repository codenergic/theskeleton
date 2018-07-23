package org.codenergic.theskeleton.client;

import java.util.Set;

import javax.annotation.Nullable;

import org.codenergic.theskeleton.core.data.RestData;
import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.NotEmpty;
import org.immutables.value.Value;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@Value.Immutable
@JsonDeserialize(builder = ImmutableOAuth2ClientRestData.Builder.class)
interface OAuth2ClientRestData extends RestData {
	@NotEmpty(groups = {New.class, Existing.class})
	@Nullable
	Set<String> getAuthorizedGrantTypes();

	@NotBlank(groups = {Existing.class})
	@Nullable
	String getClientSecret();

	@Nullable
	String getDescription();

	@Nullable
	String getId();

	@Nullable
	Boolean getIsAutoApprove();

	@Nullable
	Boolean getIsScoped();

	@Nullable
	Boolean getIsSecretRequired();

	@NotBlank(groups = {New.class, Existing.class})
	@Nullable
	String getName();

	@Nullable
	Set<String> getRegisteredRedirectUris();

	@Nullable
	Set<String> getResourceIds();

	@Nullable
	Set<String> getScope();

	interface New {
	}

	interface Existing {
	}
}
