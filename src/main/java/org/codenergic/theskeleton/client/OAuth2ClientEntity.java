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
package org.codenergic.theskeleton.client;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.StringUtils;
import org.codenergic.theskeleton.core.data.AbstractAuditingEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.provider.ClientDetails;

@Entity
@SuppressWarnings("serial")
@Table(name = "ts_oauth2_client")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "authorized_grant_types")
public class OAuth2ClientEntity extends AbstractAuditingEntity implements ClientDetails, Client {
	private static final char SEPARATOR = ',';
	private static final int ACCESS_TOKEN_VALIDITY_IN_SECONDS = 21_600; // 6 hours
	private static final int REFRESH_TOKEN_VALIDITY_IN_SECONDS = 1_209_600; // 14 days
	@NotNull
	@Column(length = 300)
	private String name;
	@Column(length = 500)
	private String description;
	@Column(name = "resource_ids")
	private String resourceIds;
	@Column(name = "secret_required")
	private boolean secretRequired = true;
	@Lob
	@NotNull
	@Column(name = "client_secret")
	private String clientSecret;
	@Column
	private boolean scoped = true;
	@Lob
	@Column(name = "scopes")
	private String scope = "read" + SEPARATOR + "write";
	@Column(name = "authorized_grant_types")
	private String authorizedGrantTypes = "AUTHORIZATION_CODE" + SEPARATOR + "IMPLICIT";
	@Lob
	@Column(name = "registered_redirect_uris")
	private String registeredRedirectUris;
	@Column(name = "auto_approve")
	private boolean autoApprove = false;

	@Override
	public OAuth2ClientEntity setId(String id) {
		super.setId(id);
		return this;
	}

	@Override
	public String getName() {
		return name;
	}

	public OAuth2ClientEntity setName(String name) {
		this.name = name;
		return this;
	}

	@Override
	public String getDescription() {
		return description;
	}

	public OAuth2ClientEntity setDescription(String description) {
		this.description = description;
		return this;
	}

	@Override
	public String getClientId() {
		return getId();
	}

	@Override
	public Set<String> getResourceIds() {
		String[] resourcesIds = StringUtils.split(resourceIds, SEPARATOR);
		return resourcesIds == null ? new HashSet<>() : new HashSet<>(Arrays.asList(resourcesIds));
	}

	public OAuth2ClientEntity setResourceIds(Set<String> resourceIds) {
		this.resourceIds = StringUtils.join(resourceIds, SEPARATOR);
		return this;
	}

	@Override
	public boolean isSecretRequired() {
		return secretRequired;
	}

	public OAuth2ClientEntity setSecretRequired(boolean secretRequired) {
		this.secretRequired = secretRequired;
		return this;
	}

	@Override
	public String getClientSecret() {
		return clientSecret;
	}

	public OAuth2ClientEntity setClientSecret(String clientSecret) {
		this.clientSecret = clientSecret;
		return this;
	}

	@Override
	public boolean isScoped() {
		return scoped;
	}

	public OAuth2ClientEntity setScoped(boolean scoped) {
		this.scoped = scoped;
		return this;
	}

	@Override
	public Set<String> getScope() {
		String[] scopes = StringUtils.split(scope, SEPARATOR);
		return scopes == null ? new HashSet<>() : new HashSet<>(Arrays.asList(scopes));
	}

	public OAuth2ClientEntity setScope(Set<String> scope) {
		this.scope = StringUtils.join(scope, SEPARATOR);
		return this;
	}

	@Override
	public Set<String> getAuthorizedGrantTypes() {
		String[] grantTypes = StringUtils.split(authorizedGrantTypes, SEPARATOR);
		return grantTypes == null ? new HashSet<>() : new HashSet<>(Arrays.asList(grantTypes));
	}

	public OAuth2ClientEntity setAuthorizedGrantTypes(Set<OAuth2GrantType> authorizedGrantTypes) {
		this.authorizedGrantTypes = StringUtils.join(authorizedGrantTypes, SEPARATOR);
		return this;
	}

	@Override
	public Set<String> getRegisteredRedirectUri() {
		String[] redirectUris = StringUtils.split(registeredRedirectUris, SEPARATOR);
		return redirectUris == null ? new HashSet<>() : new HashSet<>(Arrays.asList(redirectUris));
	}

	public OAuth2ClientEntity setRegisteredRedirectUris(Set<String> registeredRedirectUris) {
		this.registeredRedirectUris = StringUtils.join(registeredRedirectUris, SEPARATOR);
		return this;
	}

	@Override
	public Collection<GrantedAuthority> getAuthorities() {
		return Collections.emptyList();
	}

	@Override
	public Integer getAccessTokenValiditySeconds() {
		return ACCESS_TOKEN_VALIDITY_IN_SECONDS;
	}

	@Override
	public Integer getRefreshTokenValiditySeconds() {
		return REFRESH_TOKEN_VALIDITY_IN_SECONDS;
	}

	@Override
	public boolean isAutoApprove(String scope) {
		return autoApprove;
	}

	public boolean isAutoApprove() {
		return autoApprove;
	}

	public OAuth2ClientEntity setAutoApprove(boolean autoApprove) {
		this.autoApprove = autoApprove;
		return this;
	}

	@Override
	public Map<String, Object> getAdditionalInformation() {
		return Collections.emptyMap();
	}
}
