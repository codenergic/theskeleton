package org.codenergic.theskeleton.registration;

import java.time.Duration;
import java.time.Instant;
import java.time.Period;
import java.util.Date;
import java.util.UUID;
import java.util.stream.Stream;

import org.codenergic.theskeleton.core.test.NoOpPasswordEncoder;
import org.codenergic.theskeleton.tokenstore.ImmutableTokenStoreRestData;
import org.codenergic.theskeleton.tokenstore.TokenStoreRestData;
import org.codenergic.theskeleton.tokenstore.TokenStoreService;
import org.codenergic.theskeleton.tokenstore.TokenStoreType;
import org.codenergic.theskeleton.user.UserEntity;
import org.codenergic.theskeleton.user.UserRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class RegistrationServiceTest {
	@Mock
	private UserRepository userRepository;
	@Mock
	private TokenStoreService tokenStoreService;
	private RegistrationService registrationService;
	private PasswordEncoder passwordEncoder = new NoOpPasswordEncoder();

	private final TokenStoreRestData defaultTokenStoreData = ImmutableTokenStoreRestData.builder()
		.userId("12345")
		.expiryDate(Date.from(Instant.now()))
		.tokenType(TokenStoreType.USER_ACTIVATION)
		.uuid(UUID.randomUUID())
		.signedToken("1234")
		.build();

	@Before
	public void init() {
		MockitoAnnotations.initMocks(this);
		registrationService = new RegistrationServiceImpl(userRepository, tokenStoreService, passwordEncoder);
	}

	@Test
	public void testRegisterUserUsernameDoesNotExists() {
		when(userRepository.findByUsernameOrEmailAndEnabled(anyString(), anyString(), anyBoolean()))
			.thenReturn(Stream.of(new UserEntity()));
		RegistrationForm registrationForm = new RegistrationForm();
		registrationForm.setUsername("user");
		registrationForm.setPassword("password");
		registrationForm.setEmail("user@example.com");
		assertThatThrownBy(() -> registrationService.registerUser(registrationForm))
			.isInstanceOf(RegistrationException.class);
		verify(userRepository).findByUsernameOrEmailAndEnabled(registrationForm.getUsername(), registrationForm.getEmail(), true);
	}

	@Test
	public void testRegisterUser() {
		TransactionSynchronizationManager.initSynchronization();
		when(userRepository.findByUsernameOrEmailAndEnabled(anyString(), anyString(), eq(true))).thenReturn(Stream.empty());
		when(userRepository.findByUsernameOrEmailAndEnabled(anyString(), anyString(), eq(false))).thenReturn(Stream.empty());
		RegistrationForm registrationForm = new RegistrationForm();
		registrationForm.setUsername("user");
		registrationForm.setPassword("password");
		registrationForm.setEmail("user@example.com");
		registrationService.registerUser(registrationForm);
		verify(userRepository).save(any(UserEntity.class));
		verify(userRepository).findByUsernameOrEmailAndEnabled(registrationForm.getUsername(), registrationForm.getEmail(), true);
		verify(userRepository).findByUsernameOrEmailAndEnabled(registrationForm.getUsername(), registrationForm.getEmail(), false);
	}

	@Test
	public void testActivateUser() {
		when(tokenStoreService.findAndVerifyToken("TOKEN1234"))
			.thenReturn(ImmutableTokenStoreRestData.builder()
				.from(defaultTokenStoreData)
				.user(new UserEntity().setEnabled(false))
				.expiryDate(Date.from(defaultTokenStoreData.getExpiryDate().toInstant().minus(Period.ofDays(1))))
				.build());

		registrationService.activateUser("TOKEN1234");

		verify(tokenStoreService).findAndVerifyToken("TOKEN1234");

		when(tokenStoreService.findAndVerifyToken("TOKEN1235"))
			.thenReturn(ImmutableTokenStoreRestData.builder()
				.from(defaultTokenStoreData)
				.user(new UserEntity().setEnabled(true))
				.build());

		assertThatThrownBy(() -> registrationService.activateUser("TOKEN1235"))
			.hasMessage("Your Account is already activated")
			.isInstanceOf(RegistrationException.class);

		verify(tokenStoreService).findAndVerifyToken("TOKEN1235");
	}

	@Test
	public void testChangePassword() {
		UserEntity user = new UserEntity().setPassword("1234");
		when(tokenStoreService.findAndVerifyToken("TOKEN1234"))
			.thenReturn(ImmutableTokenStoreRestData.builder()
				.from(defaultTokenStoreData)
				.expiryDate(Date.from(defaultTokenStoreData.getExpiryDate().toInstant().plus(Duration.ofHours(1))))
				.user(user)
				.build());

		registrationService.changePassword("TOKEN1234", "notsecurepassword");
		assertThat(user.getPassword()).isEqualTo("notsecurepassword");
		verify(tokenStoreService).findAndVerifyToken("TOKEN1234");

		when(tokenStoreService.findAndVerifyToken("TOKEN1235"))
			.thenReturn(ImmutableTokenStoreRestData.builder()
				.from(defaultTokenStoreData)
				.expiryDate(Date.from(defaultTokenStoreData.getExpiryDate().toInstant().minus(Duration.ofHours(1))))
				.user(user)
				.build());
		//expired token
		assertThatThrownBy(() -> registrationService.changePassword("TOKEN1235", "notsecurepassword"))
			.hasMessage("Key is Expired")
			.isInstanceOf(RegistrationException.class);
	}
}
