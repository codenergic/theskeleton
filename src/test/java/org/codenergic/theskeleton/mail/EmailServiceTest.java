package org.codenergic.theskeleton.mail;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

import org.codenergic.theskeleton.mail.impl.EmailServiceImpl;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

public class EmailServiceTest {
	private EmailService emailService;
	@Mock
	private JavaMailSender mailSender;
	@Value("${spring.mail.username}")
	private String fromEmail;
	private ArgumentCaptor<SimpleMailMessage> simpleMailMessageCaptor;
	
	@Before
	public void init() {
		MockitoAnnotations.initMocks(this);
		this.emailService = new EmailServiceImpl(mailSender);
		this.simpleMailMessageCaptor = ArgumentCaptor.forClass(SimpleMailMessage.class);
	}
	
	@Test
	public void testSendSimpleMessage() {
		String subject = "Test Email";
		String body = "Testing Email";
		String to = "vickrif@gmail.com";
		
		emailService.sendSimpleMessage(to, subject, body);
		verify(mailSender).send(simpleMailMessageCaptor.capture());
		assertThat(to).isEqualTo(simpleMailMessageCaptor.getValue().getTo()[0]);
		assertThat(subject).isEqualTo(simpleMailMessageCaptor.getValue().getSubject());
		assertThat(body).isEqualTo(simpleMailMessageCaptor.getValue().getText());
	}
}
