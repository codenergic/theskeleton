package org.codenergic.theskeleton.client;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.Nullable;

import org.codenergic.theskeleton.core.data.RestData;
import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.NotEmpty;
import org.immutables.value.Value;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@Value.Immutable
@JsonDeserialize(builder = ImmutableOAuth2ClientRestData.Builder.class)
interface OAuth2ClientRestData extends RestData {
	static ImmutableOAuth2ClientRestData.Builder builder() {
		return ImmutableOAuth2ClientRestData.builder();
	}

	static ImmutableOAuth2ClientRestData.Builder builder(OAuth2ClientEntity oAuth2Client) {
		return builder()
			.id(oAuth2Client.getId())
			.name(oAuth2Client.getName())
			.description(oAuth2Client.getDescription())
			.resourceIds(oAuth2Client.getResourceIds())
			.isSecretRequired(oAuth2Client.isSecretRequired())
			.clientSecret(oAuth2Client.getClientSecret())
			.isScoped(oAuth2Client.isScoped())
			.scope(oAuth2Client.getScope())
			.authorizedGrantTypes(oAuth2Client.getAuthorizedGrantTypes())
			.registeredRedirectUris(oAuth2Client.getRegisteredRedirectUri())
			.isAutoApprove(oAuth2Client.isAutoApprove());
	}

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

	default OAuth2ClientEntity toOAuth2ClientEntity() {
		return new OAuth2ClientEntity()
			.setId(getId())
			.setName(getName())
			.setDescription(getDescription())
			.setResourceIds(getResourceIds())
			.setSecretRequired(Optional.ofNullable(getIsSecretRequired()).orElse(false))
			.setClientSecret(getClientSecret())
			.setScoped(Optional.ofNullable(getIsScoped()).orElse(false))
			.setScopes(getScope())
			.setAuthorizedGrantTypes(Optional.ofNullable(getAuthorizedGrantTypes()).orElse(Collections.emptySet())
				.stream()
				.map(OAuth2GrantType::valueOf)
				.collect(Collectors.toSet()))
			.setRegisteredRedirectUris(getRegisteredRedirectUris())
			.setAutoApprove(Optional.ofNullable(getIsAutoApprove()).orElse(false));
	}

	interface New {
	}

	interface Existing {
	}
}
