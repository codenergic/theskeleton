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

import org.codenergic.theskeleton.user.UserEntity;
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
		return UserDetails.class.isAssignableFrom(parameter.getNestedParameterType()) && parameter.hasParameterAnnotation(User.class);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest,
								  WebDataBinderFactory binderFactory) {
		User user = parameter.getParameterAnnotation(User.class);
		Map<String, String> uriTemplateVars =
			(Map<String, String>) webRequest.getAttribute(
				HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE, RequestAttributes.SCOPE_REQUEST);
		String username = uriTemplateVars.get(user.parameterName());
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (CURRENT_USER_USERNAME.equals(username)) {
			if (authentication == null) return null;
			if (authentication.getPrincipal() instanceof UserEntity){
				return authentication.getPrincipal();
			} else {
				username = authentication.getName();
			}
		}
		return username == null ? null : userDetailsService.loadUserByUsername(username);
	}
}
