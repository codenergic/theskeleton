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

import java.util.Map;

import org.codenergic.theskeleton.core.mail.EmailService;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceImpl implements EmailService {
	private JavaMailSender emailSender;
	private MailContentBuilder mailContentBuilder;

	public EmailServiceImpl(JavaMailSender emailSender, MailContentBuilder mailContentBuilder) {
		this.emailSender = emailSender;
		this.mailContentBuilder = mailContentBuilder;
	}

	@Override
	@Async
	public void sendSimpleMessage(String[] to, String subject, String text) {
		SimpleMailMessage message = new SimpleMailMessage();
		message.setTo(to);
		message.setSubject(subject);
		message.setText(text);
		emailSender.send(message);
	}

	@Override
	@Async
	public void sendMessage(String[] to, String subject, Map<String, Object> messages, 
			String mailTemplate) {
		MimeMessagePreparator messagePreparator = mimeMessage -> {
			MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage);
			messageHelper.setTo(to);
			messageHelper.setSubject(subject);
			String content = mailContentBuilder.build(messages, mailTemplate);
			messageHelper.setText(content, true);
		};
		emailSender.send(messagePreparator);
	}
}
