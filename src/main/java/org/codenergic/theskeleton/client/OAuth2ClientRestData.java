package org.codenergic.theskeleton.client;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.Nullable;

import org.codenergic.theskeleton.core.data.RestData;
import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.NotEmpty;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.google.auto.value.AutoValue;

@SuppressWarnings("serial")
@AutoValue
@JsonDeserialize(builder = AutoValue_OAuth2ClientRestData.Builder.class)
abstract class OAuth2ClientRestData implements RestData {
	@Nullable
	abstract String getId();
	@NotBlank(groups = {New.class, Existing.class})
	@Nullable
	abstract String getName();
	@Nullable
	abstract String getDescription();
	@Nullable
	abstract Set<String> getResourceIds();
	@Nullable
	abstract Boolean getIsSecretRequired();
	@NotBlank(groups = {Existing.class})
	@Nullable
	abstract String getClientSecret();
	@Nullable
	abstract Boolean getIsScoped();
	@Nullable
	abstract Set<String> getScope();
	@NotEmpty(groups = {New.class, Existing.class})
	@Nullable
	abstract Set<String> getAuthorizedGrantTypes();
	@Nullable
	abstract Set<String> getRegisteredRedirectUris();
	@Nullable
	abstract Boolean getIsAutoApprove();

	static Builder builder() {
		return new AutoValue_OAuth2ClientRestData.Builder();
	}

	OAuth2ClientEntity toOAuth2ClientEntity() {
		return new OAuth2ClientEntity()
			.setId(getId())
			.setName(getName())
			.setDescription(getDescription())
			.setResourceIds(getResourceIds())
			.setSecretRequired(Optional.ofNullable(getIsSecretRequired()).orElse(false))
			.setClientSecret(getClientSecret())
			.setScoped(Optional.ofNullable(getIsScoped()).orElse(false))
			.setScope(getScope())
			.setAuthorizedGrantTypes(getAuthorizedGrantTypes()
					.stream()
					.map(OAuth2GrantType::valueOf)
					.collect(Collectors.toSet()))
			.setRegisteredRedirectUris(getRegisteredRedirectUris())
			.setAutoApprove(Optional.ofNullable(getIsAutoApprove()).orElse(false));
	}

	@AutoValue.Builder
	@JsonPOJOBuilder(withPrefix = "")
	interface Builder {
		Builder id(String id);
		Builder name(String name);
		Builder description(String description);
		Builder resourceIds(Set<String> resourceIds);
		Builder isSecretRequired(Boolean secretRequired);
		Builder clientSecret(String clientSecret);
		Builder isScoped(Boolean scoped);
		Builder scope(Set<String> scope);
		Builder authorizedGrantTypes(Set<String> authorizedGrantTypes);
		Builder registeredRedirectUris(Set<String> registeredRedirectUris);
		Builder isAutoApprove(Boolean autoApprove);

		OAuth2ClientRestData build();

		default Builder fromOAuth2ClientEntity(OAuth2ClientEntity oAuth2Client) {
			return id(oAuth2Client.getId())
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
	}

	interface New {}

	interface Existing {}
}
