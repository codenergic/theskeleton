/*
 * Copyright 2018 original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.codenergic.theskeleton.core.security;

import org.junit.Test;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.ResourceLoader;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;

import static org.assertj.core.api.Assertions.assertThat;

public class SecurityConfigTest {
	private SecurityConfig securityConfig = new SecurityConfig();

	@Test
	public void testAccessTokenConverter() throws Exception {
		ResourceLoader resourceLoader = new DefaultResourceLoader();
		JwtAccessTokenConverter hmacTokenConverter = (JwtAccessTokenConverter) securityConfig.accessTokenConverter("", resourceLoader);
		assertThat(hmacTokenConverter.getKey()).containsKeys("alg", "value");
		assertThat(hmacTokenConverter.getKey()).containsEntry("alg", "HMACSHA256");
		assertThat(hmacTokenConverter.getKey().get("value")).hasSize(6);
		JwtAccessTokenConverter hmacTokenConverter2 = (JwtAccessTokenConverter) securityConfig.accessTokenConverter("1234", resourceLoader);
		assertThat(hmacTokenConverter2.getKey()).containsKeys("alg", "value");
		assertThat(hmacTokenConverter2.getKey()).containsEntry("alg", "HMACSHA256");
		assertThat(hmacTokenConverter2.getKey().get("value")).hasSize(4);
		JwtAccessTokenConverter rsaTokenConverter = (JwtAccessTokenConverter) securityConfig.accessTokenConverter("classpath:/jwt-signing-key.pem", resourceLoader);
		rsaTokenConverter.afterPropertiesSet();
		assertThat(rsaTokenConverter.isPublic()).isTrue();
		assertThat(rsaTokenConverter.getKey()).containsKeys("alg", "value");
		assertThat(rsaTokenConverter.getKey()).containsEntry("alg", "SHA256withRSA");
	}

	/**
	 * Just make coverage happy
	 */
	@Test
	public void testPasswordEncoder() {
		assertThat(securityConfig.passwordEncoder()).isInstanceOf(BCryptPasswordEncoder.class);
	}

	/**
	 * Just make coverage happy
	 */
	@Test
	public void testTokenStore() {
		TokenStore tokenStore = securityConfig.tokenStore(new JwtAccessTokenConverter());
		assertThat(tokenStore).isInstanceOf(JwtTokenStore.class);
	}
}
