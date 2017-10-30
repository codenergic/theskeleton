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
package org.codenergic.theskeleton.core.security;

import org.codenergic.theskeleton.client.OAuth2ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.provider.approval.ApprovalStore;
import org.springframework.security.oauth2.provider.token.AccessTokenConverter;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.security.oauth2.provider.token.TokenStore;

@Configuration
@EnableAuthorizationServer
@ConditionalOnWebApplication
public class OAuth2AuthorizationServerConfig extends AuthorizationServerConfigurerAdapter {
	private final AccessTokenConverter accessTokenConverter;
	private final ApprovalStore approvalStore;
	private final AuthenticationManager authenticationManager;
	private final OAuth2ClientService clientService;
	private final TokenEnhancer tokenEnhancer;
	private final TokenStore tokenStore;

	public OAuth2AuthorizationServerConfig(AccessTokenConverter accessTokenConverter, ApprovalStore approvalStore, AuthenticationManager authenticationManager, OAuth2ClientService clientService, TokenEnhancer tokenEnhancer, TokenStore tokenStore) {
		this.accessTokenConverter = accessTokenConverter;
		this.approvalStore = approvalStore;
		this.authenticationManager = authenticationManager;
		this.clientService = clientService;
		this.tokenEnhancer = tokenEnhancer;
		this.tokenStore = tokenStore;
	}

	@Override
	public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
		clients.withClientDetails(clientService);
	}

	@Override
	public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
		endpoints
				.authenticationManager(authenticationManager)
				.accessTokenConverter(accessTokenConverter)
				.approvalStore(approvalStore)
				.tokenEnhancer(tokenEnhancer)
				.tokenStore(tokenStore);
	}
}
