package org.codenergic.theskeleton.core.social;

import org.codenergic.theskeleton.socialconnection.SocialConnectionRepository;
import org.codenergic.theskeleton.user.UserEntity;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.social.UserIdSource;
import org.springframework.social.config.annotation.EnableSocial;
import org.springframework.social.config.annotation.SocialConfigurerAdapter;
import org.springframework.social.connect.ConnectionFactoryLocator;
import org.springframework.social.connect.UsersConnectionRepository;

@Configuration
@EnableSocial
public class SocialConfig extends SocialConfigurerAdapter {
	private final SocialConnectionRepository socialConnectionRepository;

	public SocialConfig(SocialConnectionRepository socialConnectionRepository) {
		this.socialConnectionRepository = socialConnectionRepository;
	}

	@Override
	public UserIdSource getUserIdSource() {
		return () -> {
			Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
			if (authentication.getPrincipal() instanceof UserEntity)
				return ((UserEntity) authentication.getPrincipal()).getId();
			return authentication.getName();
		};
	}

	@Override
	public UsersConnectionRepository getUsersConnectionRepository(ConnectionFactoryLocator connectionFactoryLocator) {
		return new SocialUsersConnectionService(connectionFactoryLocator, socialConnectionRepository);
	}
}
