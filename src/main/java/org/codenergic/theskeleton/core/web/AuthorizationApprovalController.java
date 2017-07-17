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

import org.codenergic.theskeleton.client.OAuth2ClientService;
import org.springframework.security.oauth2.provider.AuthorizationRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.ModelAndView;

@Controller
@SessionAttributes("authorizationRequest")
public class AuthorizationApprovalController {
	private static final String AUTHORIZATION_APPROVAL = "authorization_approval";

	private OAuth2ClientService clientService;

	public AuthorizationApprovalController(OAuth2ClientService clientService) {
		this.clientService = clientService;
	}

	@RequestMapping("/oauth/confirm_access")
	public ModelAndView authorizationApproval(AuthorizationRequest request) {
		ModelAndView modelAndView = new ModelAndView(AUTHORIZATION_APPROVAL);
		modelAndView.addObject("client", clientService.findClientById(request.getClientId()));
		modelAndView.addObject("authorizationRequest", request);

		return modelAndView;
	}
}
