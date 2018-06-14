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
package org.codenergic.theskeleton.core.web;

import java.util.Map;

import org.springframework.ui.ModelMap;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.context.request.WebRequestInterceptor;
import org.springframework.web.servlet.HandlerMapping;

class LoggedInUserInterceptor implements WebRequestInterceptor {
	private static final String USERNAME_ATTRIBUTE = "username";
	private static final String LOGGED_IN_USER_PARAMETER = "me";

	@Override
	@SuppressWarnings("unchecked")
	public void preHandle(WebRequest request) throws Exception {
		if (request.getUserPrincipal() == null)
			return;
		Map<String, String> o = (Map<String, String>)
				request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE, WebRequest.SCOPE_REQUEST);
		if (o == null)
			return;
		if (o.get(USERNAME_ATTRIBUTE) != null && o.get(USERNAME_ATTRIBUTE).equals(LOGGED_IN_USER_PARAMETER)) {
			o.put(USERNAME_ATTRIBUTE, request.getUserPrincipal().getName());
		}
	}

	@Override
	public void postHandle(WebRequest request, ModelMap model) throws Exception {
		// do nothing
	}

	@Override
	public void afterCompletion(WebRequest request, Exception ex) throws Exception {
		// do nothing
	}
}
