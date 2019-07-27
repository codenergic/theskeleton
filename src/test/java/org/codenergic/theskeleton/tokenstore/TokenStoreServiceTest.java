package org.codenergic.theskeleton.tokenstore;

import java.util.Date;
import java.util.Optional;
import java.util.UUID;

import javax.mail.internet.MimeMessage;

import org.codenergic.theskeleton.core.mail.EmailConfig;
import org.codenergic.theskeleton.core.mail.EmailService;
import org.codenergic.theskeleton.core.mail.EmailServiceTest;
import org.codenergic.theskeleton.core.test.NoOpPasswordEncoder;
import org.codenergic.theskeleton.registration.RegistrationException;
import org.codenergic.theskeleton.user.UserEntity;
import org.codenergic.theskeleton.user.UserRepository;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.icegreen.greenmail.util.GreenMail;
import com.icegreen.greenmail.util.GreenMailUtil;
import com.icegreen.greenmail.util.ServerSetupTest;

import it.ozimov.springboot.mail.configuration.EnableEmailTools;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {EmailServiceTest.EmailTestConfiguration.class, EmailConfig.class},
	webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class TokenStoreServiceTest {

	@Autowired
	private EmailService emailService;
	@Mock
	private UserRepository userRepository;
	private TokenStoreService tokenStoreService;
	private PasswordEncoder passwordEncoder = new NoOpPasswordEncoder();
	private GreenMail greenMail;

	@Before
	public void init() {
		MockitoAnnotations.initMocks(this);
		tokenStoreService = new TokenStoreServiceImpl("", emailService, userRepository, new ObjectMapper());
		greenMail = new GreenMail(ServerSetupTest.SMTP);
		greenMail.start();
	}

	@After
	public void stop() {
		greenMail.stop();
	}

	@Test
	public void testSendEmailActivation() {
		UserEntity user = new UserEntity()
			.setId(UUID.randomUUID().toString())
			.setUsername("user")
			.setPassword(passwordEncoder.encode("user"))
			.setEmail("user@codenergic.org");
		user.setLastModifiedDate(new Date());

		TokenStoreRestData activationTokenData = tokenStoreService.sendTokenNotification(TokenStoreType.USER_ACTIVATION, user);
		TokenStoreRestData changePasswordTokenData = tokenStoreService.sendTokenNotification(TokenStoreType.CHANGE_PASSWORD, user);

		assertThat(activationTokenData.getSignedToken()).isNotBlank();
		assertThat(changePasswordTokenData.getSignedToken()).isNotBlank();
		assertThat(!activationTokenData.isExpired() && !changePasswordTokenData.isExpired()).isTrue();
		assertThat(greenMail.waitForIncomingEmail(1000, 2)).isTrue();
		MimeMessage messageActivation = greenMail.getReceivedMessages()[0];
		assertThat(GreenMailUtil.getBody(messageActivation)).contains("activate?at=");
		MimeMessage messageChangePassword = greenMail.getReceivedMessages()[1];
		assertThat(GreenMailUtil.getBody(messageChangePassword)).contains("update?rt=");

		when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
		TokenStoreRestData verifiedData = tokenStoreService.findAndVerifyToken(changePasswordTokenData.getSignedToken());
		assertThat(verifiedData.getUuid()).isEqualTo(changePasswordTokenData.getUuid());
		verify(userRepository).findById(user.getId());

		user.setEmail("@codenergic.org");
		assertThatThrownBy(() -> tokenStoreService.sendTokenNotification(TokenStoreType.USER_ACTIVATION, user))
			.isInstanceOf(RegistrationException.class);
		assertThatThrownBy(() -> tokenStoreService.sendTokenNotification(TokenStoreType.CHANGE_PASSWORD, user))
			.isInstanceOf(RegistrationException.class);
	}

	@Configuration
	@EnableAutoConfiguration
	@EnableEmailTools
	public static class EmailTestConfiguration {

	}
}
