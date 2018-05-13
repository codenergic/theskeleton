package org.codenergic.theskeleton.client;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.google.auto.value.AutoValue;
import org.codenergic.theskeleton.core.data.RestData;
import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.NotEmpty;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@SuppressWarnings("serial")
@AutoValue
@JsonDeserialize(builder = AutoValue_OAuth2ClientRestData.Builder.class)
abstract class OAuth2ClientRestData implements RestData {
	static Builder builder() {
		return new AutoValue_OAuth2ClientRestData.Builder();
	}

	static Builder builder(OAuth2ClientEntity oAuth2Client) {
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
	abstract Set<String> getAuthorizedGrantTypes();

	@NotBlank(groups = {Existing.class})
	@Nullable
	abstract String getClientSecret();

	@Nullable
	abstract String getDescription();

	@Nullable
	abstract String getId();

	@Nullable
	abstract Boolean getIsAutoApprove();

	@Nullable
	abstract Boolean getIsScoped();

	@Nullable
	abstract Boolean getIsSecretRequired();

	@NotBlank(groups = {New.class, Existing.class})
	@Nullable
	abstract String getName();

	@Nullable
	abstract Set<String> getRegisteredRedirectUris();

	@Nullable
	abstract Set<String> getResourceIds();

	@Nullable
	abstract Set<String> getScope();

	OAuth2ClientEntity toOAuth2ClientEntity() {
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

	@AutoValue.Builder
	@JsonPOJOBuilder(withPrefix = "")
	interface Builder {
		Builder authorizedGrantTypes(Set<String> authorizedGrantTypes);

		OAuth2ClientRestData build();

		Builder clientSecret(String clientSecret);

		Builder description(String description);

		Builder id(String id);

		Builder isAutoApprove(Boolean autoApprove);

		Builder isScoped(Boolean scoped);

		Builder isSecretRequired(Boolean secretRequired);

		Builder name(String name);

		Builder registeredRedirectUris(Set<String> registeredRedirectUris);

		Builder resourceIds(Set<String> resourceIds);

		Builder scope(Set<String> scope);
	}

	interface New {
	}

	interface Existing {
	}
}
