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

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.provider.token.DefaultUserAuthenticationConverter;

class UserAccessTokenAuthenticationConverter extends DefaultUserAuthenticationConverter {
	private static final String EMAIL = "email";
	private static final String USER_ID = "user_id";

	@Override
	public Map<String, ?> convertUserAuthentication(Authentication authentication) {
		LinkedHashMap<String, Object> response = new LinkedHashMap<>(super.convertUserAuthentication(authentication));
		if (authentication.getPrincipal() instanceof User) {
			User user = (User) authentication.getPrincipal();
			response.put(EMAIL, user.getEmail());
			response.put(USER_ID, user.getId());
		}
		return response;
	}

	@Override
	public Authentication extractAuthentication(Map<String, ?> map) {
		Authentication authentication = super.extractAuthentication(map);
		if (map.containsKey(USER_ID) && map.containsKey(USERNAME) && map.containsKey(EMAIL)) {
			User user = ImmutableUser.builder()
				.id((String) map.get(USER_ID))
				.email((String) map.get(EMAIL))
				.username((String) map.get(USERNAME))
				.build();
			return new UsernamePasswordAuthenticationToken(user, "N/A", authentication.getAuthorities());
		}
		throw new BadCredentialsException("Invalid token");
	}
}
