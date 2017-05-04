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

import org.codenergic.theskeleton.user.UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.provider.token.AccessTokenConverter;
import org.springframework.security.oauth2.provider.token.DefaultAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;

@Configuration
public class SecurityConfig {
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder(10);
	}

 	/**
	 * Token converter and enhancer
	 * @return
	 */
	@Bean
	public JwtAccessTokenConverter jwtAccessTokenConverter(AccessTokenConverter accessTokenConverter) {
		JwtAccessTokenConverter tokenConverter = new JwtAccessTokenConverter();
		tokenConverter.setAccessTokenConverter(accessTokenConverter);
		return tokenConverter;
	}

	@Bean
	public AccessTokenConverter accessTokenConverter(UserService userService) {
		DefaultAccessTokenConverter accessTokenConverter = new DefaultAccessTokenConverter();
		accessTokenConverter.setUserTokenConverter(new UserAccessTokenAuthenticationConverter());
		return accessTokenConverter;
	}

	@Bean
	public TokenStore tokenStore(JwtAccessTokenConverter accessTokenConverter) {
		return new JwtTokenStore(accessTokenConverter);
	}
}
