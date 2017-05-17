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
