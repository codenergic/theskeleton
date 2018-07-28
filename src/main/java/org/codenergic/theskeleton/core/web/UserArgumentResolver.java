/*
 * Copyright 2018 the original author or authors.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.codenergic.theskeleton.core.web;

import java.util.Map;

import org.codenergic.theskeleton.core.security.ImmutableUser;
import org.codenergic.theskeleton.core.security.User;
import org.springframework.core.MethodParameter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.servlet.HandlerMapping;

public class UserArgumentResolver implements HandlerMethodArgumentResolver {
	private static final String CURRENT_USER_USERNAME = "me";
	private final UserDetailsService userDetailsService;

	public UserArgumentResolver(UserDetailsService userDetailsService) {
		this.userDetailsService = userDetailsService;
	}

	@Override
	public boolean supportsParameter(MethodParameter parameter) {
		return User.class.isAssignableFrom(parameter.getNestedParameterType()) && parameter.hasParameterAnnotation(User.Inject.class);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest,
								  WebDataBinderFactory binderFactory) {
		User.Inject injectUser = parameter.getParameterAnnotation(User.Inject.class);
		Map<String, String> uriTemplateVars =
			(Map<String, String>) webRequest.getAttribute(
				HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE, RequestAttributes.SCOPE_REQUEST);
		String username = uriTemplateVars.get(injectUser.parameterName());
		if (username == null) return null;
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (username.equals(CURRENT_USER_USERNAME) && authentication != null && authentication.getPrincipal() instanceof User) {
			return authentication.getPrincipal();
		}
		if (!username.equals(CURRENT_USER_USERNAME)) {
			UserDetails user = userDetailsService.loadUserByUsername(username);
			if (user instanceof User) return user;
		}
		return ImmutableUser.builder().username(username).build();
	}
}
