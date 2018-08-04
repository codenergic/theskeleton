/*
 * Copyright 2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *  *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *  *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.codenergic.theskeleton.core.social;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.codenergic.theskeleton.core.security.User;
import org.codenergic.theskeleton.social.SocialConnectionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.social.FacebookAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.social.UserIdSource;
import org.springframework.social.config.annotation.EnableSocial;
import org.springframework.social.config.annotation.SocialConfigurerAdapter;
import org.springframework.social.connect.ConnectionFactoryLocator;
import org.springframework.social.connect.UsersConnectionRepository;
import org.springframework.social.connect.support.OAuth2ConnectionFactory;
import org.springframework.social.connect.web.ProviderSignInController;
import org.springframework.social.connect.web.ProviderSignInUtils;
import org.springframework.social.connect.web.SignInAdapter;
import org.springframework.social.google.config.boot.GoogleAutoConfiguration;

@Configuration
@EnableSocial
@Import({FacebookAutoConfiguration.class, GoogleAutoConfiguration.class})
public class SocialConfig extends SocialConfigurerAdapter {
	/**
	 * <a href="https://github.com/jhipster/generator-jhipster/issues/2349">generator-jhipster/issues/2349</a>
	 */
	private static final String[] PROFILE_FIELDS = {
		"id", "about", "age_range", "birthday", "context", "cover", "currency", "devices", "education", "email",
		"favorite_athletes", "favorite_teams", "first_name", "gender", "hometown", "inspirational_people", "installed", "install_type",
		"is_verified", "languages", "last_name", "link", "locale", "location", "meeting_for", "middle_name", "name", "name_format",
		"political", "quotes", "payment_pricepoints", "relationship_status", "religion", "security_settings", "significant_other",
		"sports", "test_group", "timezone", "third_party_id", "updated_time", "verified", "video_upload_limits", "viewer_can_send_gift",
		"website", "work"
	};

	private final SocialConnectionRepository socialConnectionRepository;

	public SocialConfig(SocialConnectionRepository socialConnectionRepository) {
		this.socialConnectionRepository = socialConnectionRepository;
	}

	@PostConstruct
	public void init() throws ClassNotFoundException, NoSuchFieldException, IllegalAccessException {
		Field field = Class.forName("org.springframework.social.facebook.api.UserOperations").
			getDeclaredField("PROFILE_FIELDS");
		field.setAccessible(true);
		Field modifiers = field.getClass().getDeclaredField("modifiers");
		modifiers.setAccessible(true);
		modifiers.setInt(field, field.getModifiers() & ~Modifier.FINAL);
		field.set(null, PROFILE_FIELDS);
	}

	@Autowired
	public void connectionFactories(ConnectionFactoryLocator connectionFactoryLocator) {
		OAuth2ConnectionFactory<?> fbConnectionFactory = (OAuth2ConnectionFactory<?>) connectionFactoryLocator.getConnectionFactory("facebook");
		if (fbConnectionFactory != null)
			fbConnectionFactory.setScope("email public_profile user_link");
	}

	@Override
	public UserIdSource getUserIdSource() {
		return () -> {
			Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
			if (authentication.getPrincipal() instanceof User)
				return ((User) authentication.getPrincipal()).getId();
			return authentication.getName();
		};
	}

	@Override
	public UsersConnectionRepository getUsersConnectionRepository(ConnectionFactoryLocator connectionFactoryLocator) {
		return new SocialUsersConnectionService(connectionFactoryLocator, socialConnectionRepository);
	}

	@Bean
	public ProviderSignInController providerSignInController(ConnectionFactoryLocator connectionFactoryLocator, UsersConnectionRepository usersConnectionRepository, SignInAdapter signInAdapter) {
		ProviderSignInController signInController =
			new ProviderSignInController(connectionFactoryLocator, usersConnectionRepository, signInAdapter);
		signInController.setSignUpUrl("/registration");
		return signInController;
	}

	@Bean
	public ProviderSignInUtils providerSignInUtils(ConnectionFactoryLocator connectionFactoryLocator, UsersConnectionRepository connectionRepository) {
		return new ProviderSignInUtils(connectionFactoryLocator, connectionRepository);
	}

	@Bean
	public SignInAdapter signInAdapter(UserDetailsService userDetailsService) {
		RequestCache requestCache = new HttpSessionRequestCache();
		return (userId, connection, request) -> {
			UserDetails userDetails = userDetailsService.loadUserByUsername(userId);
			Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
			SecurityContextHolder.getContext().setAuthentication(authentication);
			SavedRequest savedRequest = requestCache.getRequest(request.getNativeRequest(HttpServletRequest.class), request.getNativeResponse(HttpServletResponse.class));
			return savedRequest == null ? null : savedRequest.getRedirectUrl();
		};
	}
}
