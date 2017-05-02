package org.codenergic.theskeleton.core.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.TokenStore;

@Configuration
@EnableResourceServer
public class OAuth2ResourceServerConfig extends ResourceServerConfigurerAdapter {
	@Autowired
	private AuthenticationManager authenticationManager;
	@Autowired
	private TokenStore tokenStore;

	@Override
	public void configure(HttpSecurity http) throws Exception {
		http.requestMatchers()
				.antMatchers("/api/**")
				.and()
			.authorizeRequests()
				.anyRequest().authenticated()
				.and()
			.sessionManagement()
				.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
				.and()
			.anonymous();
	}

	@Override
	public void configure(ResourceServerSecurityConfigurer resources) throws Exception {
		DefaultTokenServices tokenServices = new DefaultTokenServices();
		tokenServices.setAuthenticationManager(authenticationManager);
		tokenServices.setTokenStore(tokenStore);
		resources.tokenServices(tokenServices);
	}
}
