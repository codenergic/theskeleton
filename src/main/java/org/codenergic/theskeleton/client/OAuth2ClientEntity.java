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
import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.StringUtils;
import org.codenergic.theskeleton.core.data.AbstractAuditingEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.provider.ClientDetails;

@Entity
@Table(name = "ts_oauth2_client")
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
	private String resourceIdentities;
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
	private String scopes = "read" + SEPARATOR + "write";
	@Column(name = "authorized_grant_types")
	private String grantTypes = "AUTHORIZATION_CODE" + SEPARATOR + "IMPLICIT";
	@Lob
	@Column(name = "registered_redirect_uris")
	private String redirectUris;
	@Column(name = "auto_approve")
	private boolean autoApprove = false;

	@Override
	@Transient
	public Integer getAccessTokenValiditySeconds() {
		return ACCESS_TOKEN_VALIDITY_IN_SECONDS;
	}

	@Override
	@Transient
	public Map<String, Object> getAdditionalInformation() {
		return Collections.emptyMap();
	}

	@Override
	@Transient
	public Collection<GrantedAuthority> getAuthorities() {
		return Collections.emptyList();
	}

	@Override
	@Transient
	public Set<String> getAuthorizedGrantTypes() {
		String[] authorizedGrantTypes = StringUtils.split(this.getGrantTypes(), SEPARATOR);
		return authorizedGrantTypes == null ? new HashSet<>() : new HashSet<>(Arrays.asList(authorizedGrantTypes));
	}

	public OAuth2ClientEntity setAuthorizedGrantTypes(Set<OAuth2GrantType> authorizedGrantTypes) {
		return setGrantTypes(StringUtils.join(authorizedGrantTypes, SEPARATOR));
	}

	@Override
	public String getClientId() {
		return getId();
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
	public String getDescription() {
		return description;
	}

	public OAuth2ClientEntity setDescription(String description) {
		this.description = description;
		return this;
	}

	public String getGrantTypes() {
		return grantTypes;
	}

	public OAuth2ClientEntity setGrantTypes(String grantTypes) {
		this.grantTypes = grantTypes;
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

	public String getRedirectUris() {
		return redirectUris;
	}

	public OAuth2ClientEntity setRedirectUris(String redirectUris) {
		this.redirectUris = redirectUris;
		return this;
	}

	@Override
	@Transient
	public Integer getRefreshTokenValiditySeconds() {
		return REFRESH_TOKEN_VALIDITY_IN_SECONDS;
	}

	@Override
	public Set<String> getRegisteredRedirectUri() {
		String[] registeredRedirectUris = StringUtils.split(this.getRedirectUris(), SEPARATOR);
		return registeredRedirectUris == null ? new HashSet<>() : new HashSet<>(Arrays.asList(registeredRedirectUris));
	}

	public String getResourceIdentities() {
		return resourceIdentities;
	}

	public OAuth2ClientEntity setResourceIdentities(String resourceIdentities) {
		this.resourceIdentities = resourceIdentities;
		return this;
	}

	@Override
	@Transient
	public Set<String> getResourceIds() {
		String[] resourcesIds = StringUtils.split(getResourceIdentities(), SEPARATOR);
		return resourcesIds == null ? new HashSet<>() : new HashSet<>(Arrays.asList(resourcesIds));
	}

	public OAuth2ClientEntity setResourceIds(Set<String> resourceIds) {
		return setResourceIdentities(StringUtils.join(resourceIds, SEPARATOR));
	}

	@Override
	@Transient
	public Set<String> getScope() {
		String[] scope = StringUtils.split(this.getScopes(), SEPARATOR);
		return scope == null ? new HashSet<>() : new HashSet<>(Arrays.asList(scope));
	}

	public OAuth2ClientEntity setScope(Set<String> scopes) {
		return setScopes(scopes == null || scopes.isEmpty() ? getScopes() : StringUtils.join(scopes, SEPARATOR));
	}

	public String getScopes() {
		return scopes;
	}

	public OAuth2ClientEntity setScopes(String scopes) {
		this.scopes = scopes;
		return this;
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
	public boolean isScoped() {
		return scoped;
	}

	public OAuth2ClientEntity setScoped(boolean scoped) {
		this.scoped = scoped;
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
	public OAuth2ClientEntity setId(String id) {
		super.setId(id);
		return this;
	}

	public OAuth2ClientEntity setRegisteredRedirectUris(Set<String> registeredRedirectUris) {
		return setRedirectUris(StringUtils.join(registeredRedirectUris, SEPARATOR));
	}
}
