package org.codenergic.theskeleton.registration;

import com.icegreen.greenmail.util.GreenMail;
import com.icegreen.greenmail.util.GreenMailUtil;
import com.icegreen.greenmail.util.ServerSetupTest;
import it.ozimov.springboot.mail.configuration.EnableEmailTools;
import org.codenergic.theskeleton.core.mail.EmailConfig;
import org.codenergic.theskeleton.core.mail.EmailService;
import org.codenergic.theskeleton.core.mail.EmailServiceTest;
import org.codenergic.theskeleton.core.mail.impl.EmailServiceImpl;
import org.codenergic.theskeleton.registration.impl.RegistrationServiceImpl;
import org.codenergic.theskeleton.user.UserEntity;
import org.codenergic.theskeleton.user.UserRepository;
import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;

import javax.mail.internet.AddressException;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = { EmailServiceTest.EmailTestConfiguration.class, EmailConfig.class, EmailServiceImpl.class },
	webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class RegistrationServiceTest {
	private RegistrationService registrationService;
	@Mock
	private UserRepository userRepository;
	@Mock
	private RegistrationRepository registrationRepository;
	@Autowired
	private EmailService emailService;
	private PasswordEncoder passwordEncoder = NoOpPasswordEncoder.getInstance();
	private GreenMail greenMail;

	@Before
	public void init() {
		MockitoAnnotations.initMocks(this);
		registrationService = new RegistrationServiceImpl(userRepository, registrationRepository, passwordEncoder,
			emailService);
		greenMail = new GreenMail(ServerSetupTest.SMTP);
		greenMail.start();
	}

	@After
	public void stop() {
		greenMail.stop();
	}

	@Test
	public void testRegisterUserEmailDoesntExists() {
		when(userRepository.existsByEmail(anyString())).thenReturn(false);
		when(userRepository.existsByUsername(anyString())).thenReturn(true);
		RegistrationForm registrationForm = new RegistrationForm();
		registrationForm.setUsername("user");
		registrationForm.setPassword("password");
		registrationForm.setEmail("user@example.com");
		assertThatThrownBy(() -> registrationService.registerUser(registrationForm))
			.isInstanceOf(RegistrationException.class);
		verify(userRepository).existsByEmail(anyString());
		verify(userRepository).existsByUsername(anyString());
	}

	@Test
	public void testRegisterUserUsernameDoesntExists() {
		when(userRepository.existsByEmail(anyString())).thenReturn(true);
		when(userRepository.existsByUsername(anyString())).thenReturn(false);
		RegistrationForm registrationForm = new RegistrationForm();
		registrationForm.setUsername("user");
		registrationForm.setPassword("password");
		registrationForm.setEmail("user@example.com");
		assertThatThrownBy(() -> registrationService.registerUser(registrationForm))
			.isInstanceOf(RegistrationException.class);
		verify(userRepository).existsByEmail(anyString());
		verify(userRepository, times(0)).existsByUsername(anyString());
	}

	@Test
	public void testRegisterUser() {
		when(userRepository.existsByEmail(anyString())).thenReturn(false);
		when(userRepository.existsByUsername(anyString())).thenReturn(false);
		RegistrationForm registrationForm = new RegistrationForm();
		registrationForm.setUsername("user");
		registrationForm.setPassword("password");
		registrationForm.setEmail("user@example.com");
		registrationService.registerUser(registrationForm);
		verify(userRepository).save(any(UserEntity.class));
		verify(userRepository).existsByEmail(anyString());
		verify(userRepository).existsByUsername(anyString());
	}

	@Test
	public void testSendEmailActivation() {
		UserEntity user = new UserEntity()
			.setId(UUID.randomUUID().toString())
			.setUsername("user")
			.setPassword(passwordEncoder.encode("user"))
			.setEmail("user@codenergic.org");
		registrationService.sendConfirmationNotification(user,"localhost");
		assertThat(greenMail.waitForIncomingEmail(1000, 1)).isTrue();
		MimeMessage message = greenMail.getReceivedMessages()[0];
		assertThat(GreenMailUtil.getBody(message)).contains("activate?at=");
		user.setEmail("@codenergic.org");
		assertThatThrownBy(() -> registrationService.sendConfirmationNotification(user, "localhost"))
			.isInstanceOf(RegistrationException.class);
	}

	@Test
	public void testActivateUser() {
		DateTime expired = DateTime.now().minusDays(15);
		DateTime future = DateTime.now().plusDays(15);
		RegistrationEntity registrationEntity = new RegistrationEntity()
			.setUser(new UserEntity().setEnabled(false))
			.setExpiryDate(future.toDate())
			.setToken("TOKEN1234");
		when(registrationRepository.findByToken("TOKEN1234")).thenReturn(registrationEntity);

		registrationService.activateUser("TOKEN1234");
		//good token
		assertThat(registrationEntity.getUser().isEnabled()).isTrue();
		assertThatThrownBy(()->registrationService.activateUser("TOKEN1234"))
			.hasMessage("Your Account is already activated")
			.isInstanceOf(RegistrationException.class);
		//expired token
		registrationEntity.setExpiryDate(expired.toDate());
		assertThatThrownBy(()-> registrationService.activateUser("TOKEN1234"))
			.hasMessage("Activation Key is Expired")
			.isInstanceOf(RegistrationException.class);
		assertThatThrownBy(()->registrationService.activateUser("BADTOKEN"))
			.hasMessage("Invalid Activation Key")
			.isInstanceOf(RegistrationException.class);
	}

	@Configuration
	@EnableAutoConfiguration
	@EnableEmailTools
	public static class EmailTestConfiguration {

	}
}
