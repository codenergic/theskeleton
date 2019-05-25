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

import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashMap;

import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.codenergic.theskeleton.core.mail.EmailServiceTest.EmailTestConfiguration;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.junit4.SpringRunner;

import com.icegreen.greenmail.util.GreenMail;
import com.icegreen.greenmail.util.GreenMailUtil;
import com.icegreen.greenmail.util.ServerSetupTest;

import it.ozimov.springboot.mail.configuration.EnableEmailTools;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = { EmailTestConfiguration.class, EmailConfig.class, EmailServiceImpl.class },
				webEnvironment = WebEnvironment.NONE)
public class EmailServiceTest {
	@Autowired
	private EmailService emailService;
	private GreenMail greenMail;

	@Before
	public void init() {
		MockitoAnnotations.initMocks(this);
		greenMail = new GreenMail(ServerSetupTest.SMTP);
		greenMail.start();
	}

	@After
	public void stop() {
		greenMail.stop();
	}

	@Test
	public void testSendSimpleEmail() throws MessagingException {
		String subject = "Test Email";
		String body = "Testing Email";
		String to = "vickrif@gmail.com";
		emailService.sendSimpleEmail(null, new InternetAddress(to), subject, body);
		emailService.sendSimpleEmail(to, new InternetAddress(to), subject, body);
		assertThat(greenMail.waitForIncomingEmail(1000, 2)).isTrue();
		MimeMessage message = greenMail.getReceivedMessages()[0];
		assertThat(GreenMailUtil.getBody(message)).isEqualTo(body);
		assertThat(((InternetAddress) message.getFrom()[0]).getAddress()).isEqualTo("theskeleton-test@codenergic.org");
	}

	@Test
	public void testSendEmail() throws MessagingException {
		String subject = "Test Email";
		String to = "vickrif@gmail.com";
		emailService.sendEmail(null, new InternetAddress(to), subject, new HashMap<>(), "email/test.html");
		emailService.sendEmail(to, new InternetAddress(to), subject, new HashMap<>(), "email/test.html");
		assertThat(greenMail.waitForIncomingEmail(1000, 2)).isTrue();
		MimeMessage message = greenMail.getReceivedMessages()[0];
		assertThat(GreenMailUtil.getBody(message)).contains("TheSkeleton Test");
		assertThat(((InternetAddress) message.getFrom()[0]).getAddress()).isEqualTo("theskeleton-test@codenergic.org");
	}

	@Configuration
	@EnableAutoConfiguration
	@EnableEmailTools
	public static class EmailTestConfiguration {

	}
}
