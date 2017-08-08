package org.codenergic.theskeleton.mail.impl;

import org.codenergic.theskeleton.mail.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceImpl implements EmailService {
	public JavaMailSender emailSender;
	
	@Autowired
	public EmailServiceImpl(JavaMailSender emailSender) {
		this.emailSender = emailSender;
	}
	
	@Override
	@Async
	public void sendSimpleMessage(String to, String subject, String text) {
		MimeMessagePreparator messagePreparator = mimeMessage -> {
		MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage);
		messageHelper.setTo(to);
		messageHelper.setSubject(subject);
		messageHelper.setText(text);
		};
		try {
			emailSender.send(messagePreparator);
		} catch (MailException e) {}
	}
}
