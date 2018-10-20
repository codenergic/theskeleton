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
package org.codenergic.theskeleton.core.security;

import java.util.Arrays;
import java.util.Collections;

import org.codenergic.theskeleton.user.UserService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.RememberMeServices;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
@ConditionalOnWebApplication
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
	private final UserService userService;
	private final PasswordEncoder passwordEncoder;
	private final RememberMeServices rememberMeServices;
	private final SessionRegistry sessionRegistry;

	public WebSecurityConfig(UserService userService, PasswordEncoder passwordEncoder,
							 RememberMeServices rememberMeServices, SessionRegistry sessionRegistry) {
		this.userService = userService;
		this.passwordEncoder = passwordEncoder;
		this.rememberMeServices = rememberMeServices;
		this.sessionRegistry = sessionRegistry;
	}

	@Bean
	@Override
	public AuthenticationManager authenticationManagerBean() throws Exception {
		return super.authenticationManagerBean();
	}

	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(userService)
				.passwordEncoder(passwordEncoder);
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.authorizeRequests()
				.antMatchers("/oauth/authorize").authenticated()
				.and()
			.formLogin().permitAll()
				.loginPage("/login")
				.loginProcessingUrl("/auth/login")
				.failureUrl("/login?error")
				.and()
			.rememberMe()
				.rememberMeParameter("remember-me")
				.rememberMeServices(rememberMeServices)
				.and()
			.logout()
				.invalidateHttpSession(true)
				.logoutSuccessHandler(new HttpStatusReturningLogoutSuccessHandler())
				.logoutUrl("/auth/logout")
				.permitAll()
				.and()
			.headers()
				.frameOptions().sameOrigin()
				.and()
			.sessionManagement()
				.maximumSessions(10)
				.sessionRegistry(sessionRegistry)
				.and()
			.sessionCreationPolicy(SessionCreationPolicy.ALWAYS)
				.and()
			.csrf()
				.requireCsrfProtectionMatcher(new AntPathRequestMatcher("/oauth/authorize"))
				.disable()
			.cors();
	}

	/**
	 * <a href="https://github.com/spring-projects/spring-boot/issues/5834#issuecomment-296370088">See explanation here</a>
	 */
	@Bean
	public FilterRegistrationBean corsFilterBean() {
		final CorsConfiguration configuration = new CorsConfiguration();
		configuration.setAllowedOrigins(Collections.singletonList("*"));
		configuration.setAllowedMethods(Arrays.asList("HEAD", "GET", "POST", "PUT", "DELETE", "PATCH"));
		// setAllowCredentials(true) is important, otherwise:
		// The value of the 'Access-Control-Allow-Origin' header in the response must not be the wildcard '*' when the request's credentials mode is 'include'.
		configuration.setAllowCredentials(true);
		// setAllowedHeaders is important! Without it, OPTIONS preflight request
		// will fail with 403 Invalid CORS request
		configuration.setAllowedHeaders(Arrays.asList("Authorization", "Cache-Control", "Content-Type"));
		final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", configuration);
		FilterRegistrationBean corsFilter = new FilterRegistrationBean<>(new CorsFilter(source));
		corsFilter.setOrder(Ordered.HIGHEST_PRECEDENCE);
		return corsFilter;
	}
}
