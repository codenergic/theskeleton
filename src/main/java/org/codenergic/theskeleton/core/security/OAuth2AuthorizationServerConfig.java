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

import java.security.KeyPair;
import java.security.KeyPairGenerator;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.provider.token.AccessTokenConverter;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.security.oauth2.provider.token.TokenStore;

@Configuration
@EnableAuthorizationServer
public class OAuth2AuthorizationServerConfig extends AuthorizationServerConfigurerAdapter {
	private final Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	private AccessTokenConverter accessTokenConverter;
	@Autowired
	private AuthenticationManager authenticationManager;
	@Autowired
	private TokenEnhancer tokenEnhancer;
	@Autowired
	private TokenStore tokenStore;

	@Override
	public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
		KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("EC");
		KeyPair keyPair = keyPairGenerator.generateKeyPair();
		String clientId = Base64.encodeBase64URLSafeString(keyPair.getPublic().getEncoded());
		String clientSecret = Base64.encodeBase64URLSafeString(keyPair.getPrivate().getEncoded());
		logger.info("\n\n\tDefault OAuth2 Client:\n\tID \t: {}\n\tSecret \t: {}\n", clientId, clientSecret);
		clients.inMemory()
				.withClient(clientId)
				.secret(clientSecret)
				.authorizedGrantTypes("password", "implicit", "authorization_code", "refresh_token")
				.autoApprove(true);
	}

	@Override
	public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
		endpoints
				.authenticationManager(authenticationManager)
				.accessTokenConverter(accessTokenConverter)
				.tokenEnhancer(tokenEnhancer)
				.tokenStore(tokenStore);
	}
}
