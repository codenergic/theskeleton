package org.codenergic.theskeleton.core.social;

import org.apache.commons.lang3.RandomStringUtils;
import org.codenergic.theskeleton.socialconnection.SocialConnectionRepository;
import org.codenergic.theskeleton.socialconnection.SocialConnectionService;
import org.codenergic.theskeleton.user.UserEntity;
import org.springframework.boot.bind.RelaxedPropertyResolver;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.core.env.Environment;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.social.connect.ConnectionSignUp;
import org.springframework.social.facebook.connect.FacebookConnectionFactory;
import org.springframework.social.google.connect.GoogleConnectionFactory;

@Configuration
public class SocialConfig implements EnvironmentAware {
	private RelaxedPropertyResolver env;
	
	@Bean
	@Primary
	public SocialServiceLocator socialServiceLocator() {
		SocialServiceRegistry registry = new SocialServiceRegistry();
		registry.addSocialService(facebookService());
		registry.addSocialService(googlePlusService());
		
		return registry;
	}
	
	@Bean
	public SocialUsersConnectionService usersConnectionRepository(SocialServiceLocator locator,
			SocialConnectionRepository repository, ConnectionSignUp connectionSignUp) {
		SocialUsersConnectionService s = new SocialUsersConnectionService(locator, repository);
		s.setConnectionSignUp(connectionSignUp);
		
		return s;
	}
	
	@Bean
	@Scope(value = "request", proxyMode = ScopedProxyMode.TARGET_CLASS)
	public SocialConnectionService connectionRepository(SocialUsersConnectionService usersConnectionRepository) {
		UserEntity user = getCurrentUser();
		if (user == null) return null;
		return usersConnectionRepository.createConnectionRepository(user.getId());
	}
	
	@Bean
	public FacebookService facebookService() {
		String appId = env.getProperty("facebook.app-id");
		String appSecret = env.getProperty("facebook.app-secret");
		String redirectUri = env.getProperty("facebook.redirect-uri");
		String scope = env.getProperty("facebook.scope");
		
		FacebookConnectionFactory factory = new FacebookConnectionFactory(appId, appSecret);
		FacebookService facebookService = new FacebookService(factory);
		facebookService.setRedirectUri(redirectUri);
		facebookService.setScope(scope);
		facebookService.setState(RandomStringUtils.randomAlphanumeric(20));
		
		return facebookService;
	}
	
	@Bean
	public GooglePlusService googlePlusService() {
		String appId = env.getProperty("google.app-id");
		String appSecret = env.getProperty("google.app-secret");
		String redirectUri = env.getProperty("google.redirect-uri");
		String scope = env.getProperty("google.scope");
		
		GoogleConnectionFactory factory = new GoogleConnectionFactory(appId, appSecret);
		GooglePlusService gPlusService = new GooglePlusService(factory);
		gPlusService.setRedirectUri(redirectUri);
		gPlusService.setScope(scope);
		gPlusService.setState(RandomStringUtils.randomAlphanumeric(20));
		
		return gPlusService;
	}

	@Override
	public void setEnvironment(Environment env) {
		this.env = new RelaxedPropertyResolver(env, "spring.social.");
	}

	protected static UserEntity getCurrentUser() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication == null || !authentication.isAuthenticated()) {
			return null;
		}

		if (authentication.getPrincipal() instanceof UserEntity) {
			return (UserEntity) authentication.getPrincipal();
		}

		if (authentication.getPrincipal() instanceof String) {
			String principal = (String) authentication.getPrincipal();
			if ("anonymousUser".equals(principal)) {
				return null;
			}
			
			return new UserEntity().setUsername((String) authentication.getPrincipal());
		}
		return null;
	}
}
