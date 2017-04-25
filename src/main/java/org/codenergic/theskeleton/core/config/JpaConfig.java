package org.codenergic.theskeleton.core.config;

import org.codenergic.theskeleton.Application;
import org.codenergic.theskeleton.core.data.UTCDateTimeProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.auditing.DateTimeProvider;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaAuditing(dateTimeProviderRef = "dateTimeProvider")
@EnableJpaRepositories(basePackageClasses = Application.class)
public class JpaConfig {
	@Bean
	public DateTimeProvider dateTimeProvider() {
		return new UTCDateTimeProvider();
	}
}
