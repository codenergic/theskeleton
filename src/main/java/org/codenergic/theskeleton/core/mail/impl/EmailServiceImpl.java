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
package org.codenergic.theskeleton.core.mail.impl;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;

import javax.mail.internet.InternetAddress;

import org.codenergic.theskeleton.core.mail.EmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.mail.MailProperties;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import it.ozimov.springboot.mail.model.Email;
import it.ozimov.springboot.mail.model.defaultimpl.DefaultEmail;
import it.ozimov.springboot.mail.service.exception.CannotSendEmailException;

@Service
@Async
public class EmailServiceImpl implements EmailService {
	private final Logger logger = LoggerFactory.getLogger(getClass());
	private it.ozimov.springboot.mail.service.EmailService emailSender;
	private String sender;
	private String senderAlias;

	public EmailServiceImpl(it.ozimov.springboot.mail.service.EmailService emailService, MailProperties mailProps) {
		this.emailSender = emailService;
		Map<String, String> mailProperties = mailProps.getProperties();
		this.sender = mailProperties.get("sender");
		this.senderAlias = mailProperties.get("senderAlias");
	}

	@Override
	public void sendSimpleEmail(String alias, List<InternetAddress> to, String subject, String text) {
		try {
			emailSender.send(buildEmail(new InternetAddress(sender, alias == null ? senderAlias : alias), to, subject, text));
		} catch (UnsupportedEncodingException e) {
			logger.error(e.getMessage(), e);
		}
	}

	@Override
	public void sendEmail(String alias, List<InternetAddress> to, String subject, Map<String, Object> templateParams,
			String mailTemplate) {
		try {
			emailSender.send(buildEmail(new InternetAddress(sender, alias == null ? senderAlias : alias), to, subject, ""),
					mailTemplate, templateParams);
		} catch (CannotSendEmailException | UnsupportedEncodingException e) {
			logger.error(e.getMessage(), e);
		}
	}

	private Email buildEmail(InternetAddress from, List<InternetAddress> to, String subject, String text) {
		return DefaultEmail.builder()
				.from(from)
				.to(to)
				.subject(subject)
				.body(text)
				.encoding("UTF-8")
				.build();
	}
}
