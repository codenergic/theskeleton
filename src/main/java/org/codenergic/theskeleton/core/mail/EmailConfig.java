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
package org.codenergic.theskeleton.core.mail;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.mail.MailProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.thymeleaf.spring5.SpringTemplateEngine;

import it.ozimov.springboot.mail.configuration.EnableEmailTools;
import it.ozimov.springboot.mail.service.TemplateService;

@Configuration
@EnableEmailTools
public class EmailConfig {
	@Bean
	public TemplateService thymeleafTemplateService(SpringTemplateEngine thymeleafEngine,
													@Value("${spring.thymeleaf.suffix:.html}") String thymeleafSuffix) {
		return new ThymeleafTemplateService(thymeleafEngine, thymeleafSuffix);
	}

	@Bean
	public EmailService emailService(it.ozimov.springboot.mail.service.EmailService emailService, MailProperties mailProperties) {
		return new EmailServiceImpl(emailService, mailProperties);
	}
}
