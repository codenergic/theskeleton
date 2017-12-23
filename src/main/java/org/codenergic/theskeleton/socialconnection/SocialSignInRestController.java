package org.codenergic.theskeleton.socialconnection;

import java.net.URI;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.codenergic.theskeleton.core.social.SocialService;
import org.codenergic.theskeleton.core.social.SocialServiceLocator;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.UsersConnectionRepository;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/login/social")
public class SocialSignInRestController {
	private SocialServiceLocator socialServiceLocator;
	private UsersConnectionRepository connectionRepository;
	private HttpServletRequest request;

	public SocialSignInRestController(SocialServiceLocator socialServiceLocator, 
			UsersConnectionRepository connectionRepository, HttpServletRequest request) {
		this.socialServiceLocator = socialServiceLocator;
		this.connectionRepository = connectionRepository;
		this.request = request;
	}

	@GetMapping("/{provider}")
	public String socialSignIn(@PathVariable("provider") String provider) {
		return "redirect:"+URI.create(socialServiceLocator.getSocialService(provider).getAuthorizeUrl());
	}

	@GetMapping("/{provider}/callback")
	public String socialSignInCallback(@PathVariable("provider") String provider, 
			@RequestParam(name = "code", defaultValue = "") String code) {
		SocialService<?> socialService = socialServiceLocator.getSocialService(provider);
		Connection<?> connection = socialService.createConnection(code, null);
		if (socialService.isAuthorized(connection)) {
			List<String> userIds = connectionRepository.findUserIdsWithConnection(connection);
			if (userIds.size() == 1) { // Signin
				String userId = userIds.get(0);

//				credentialsService.registerAuthentication(userId);
			}
		}
		return "redirect:"+getRedirectUrlAfterLogin();
	}

	protected String getRedirectUrlAfterLogin() {
		HttpSession session = request.getSession(false);
		if (session != null) {
			SavedRequest savedRequest = (SavedRequest) session.getAttribute("SPRING_SECURITY_SAVED_REQUEST");
			if (savedRequest != null) {
				return savedRequest.getRedirectUrl();
			}
		}
		return "/";
	}
}
