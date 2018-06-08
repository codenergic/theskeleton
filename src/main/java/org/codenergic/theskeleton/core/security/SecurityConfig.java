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

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.provider.token.DefaultAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;
import org.springframework.util.ResourceUtils;

@Configuration
public class SecurityConfig {
 	/**
	 * Token converter and enhancer
	 * @return
	 */
	@Bean
	public JwtAccessTokenConverter accessTokenConverter(@Value("${security.jwt.signing-key:}") String signingKey, ResourceLoader resourceLoader) throws IOException {
		DefaultAccessTokenConverter accessTokenConverter = new DefaultAccessTokenConverter();
		accessTokenConverter.setUserTokenConverter(new UserAccessTokenAuthenticationConverter());
		JwtAccessTokenConverter jwtAccessTokenConverter = new JwtAccessTokenConverter();
		jwtAccessTokenConverter.setAccessTokenConverter(accessTokenConverter);
		if (StringUtils.isBlank(signingKey)) return jwtAccessTokenConverter;
		if (ResourceUtils.isUrl(signingKey)) {
			Resource signingKeyResource = resourceLoader.getResource(signingKey);
			signingKey = IOUtils.toString(signingKeyResource.getURI(), StandardCharsets.UTF_8);
		}
		jwtAccessTokenConverter.setSigningKey(signingKey);
		jwtAccessTokenConverter.setVerifierKey(signingKey);
		return jwtAccessTokenConverter;
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder(10);
	}

	@Bean
	public TokenStore tokenStore(JwtAccessTokenConverter accessTokenConverter) {
		return new JwtTokenStore(accessTokenConverter);
	}
}
