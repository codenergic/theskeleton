package org.codenergic.theskeleton.core.security;

import java.util.LinkedHashMap;
import java.util.Map;

import org.codenergic.theskeleton.user.UserEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.provider.token.DefaultUserAuthenticationConverter;

public class UserAccessTokenAuthenticationConverter extends DefaultUserAuthenticationConverter {
	static final String USER_ID = "user_id";

	@Override
	public Map<String, ?> convertUserAuthentication(Authentication authentication) {
		LinkedHashMap<String, Object> response = new LinkedHashMap<>(super.convertUserAuthentication(authentication));
		if (authentication.getPrincipal() instanceof UserEntity) {
			UserEntity user = (UserEntity) authentication.getPrincipal();
			response.put(USER_ID, user.getId());
		}
		return response;
	}

	@Override
	public Authentication extractAuthentication(Map<String, ?> map) {
		Authentication authentication = super.extractAuthentication(map);
		if (map.containsKey(USER_ID) && map.containsKey(USERNAME)) {
			UserEntity user = new UserEntity()
					.setId((String) map.get(USER_ID))
					.setUsername((String) map.get(USERNAME));
			return new UsernamePasswordAuthenticationToken(user, "N/A", authentication.getAuthorities());
		}
		return null;
	}
}
