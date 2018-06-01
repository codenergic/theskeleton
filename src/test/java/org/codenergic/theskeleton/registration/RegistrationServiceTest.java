package org.codenergic.theskeleton.registration;

import org.codenergic.theskeleton.core.test.NoOpPasswordEncoder;
import org.codenergic.theskeleton.registration.impl.RegistrationServiceImpl;
import org.codenergic.theskeleton.tokenstore.TokenStoreEntity;
import org.codenergic.theskeleton.tokenstore.TokenStoreRepository;
import org.codenergic.theskeleton.tokenstore.TokenStoreType;
import org.codenergic.theskeleton.user.UserEntity;
import org.codenergic.theskeleton.user.UserRepository;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class RegistrationServiceTest {
	@Mock
	private UserRepository userRepository;
	@Mock
	private TokenStoreRepository tokenStoreRepository;
	private RegistrationService registrationService;
	private PasswordEncoder passwordEncoder = new NoOpPasswordEncoder();

	@Before
	public void init() {
		MockitoAnnotations.initMocks(this);
		registrationService = new RegistrationServiceImpl(userRepository, tokenStoreRepository, passwordEncoder);
	}

	@Test
	public void testRegisterUserEmailDoesNotExists() {
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
	public void testRegisterUserUsernameDoesNotExists() {
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
	public void testActivateUser() {
		DateTime expired = DateTime.now().minusDays(15);
		DateTime future = DateTime.now().plusDays(15);
		TokenStoreEntity tokenStoreEntity = new TokenStoreEntity()
			.setUser(new UserEntity().setEnabled(false))
			.setExpiryDate(future.toDate())
			.setToken("TOKEN1234")
			.setType(TokenStoreType.USER_ACTIVATION);
		when(tokenStoreRepository.findByTokenAndType("TOKEN1234", TokenStoreType.USER_ACTIVATION))
			.thenReturn(tokenStoreEntity);

		registrationService.activateUser("TOKEN1234");
		//good token
		assertThat(tokenStoreEntity.getUser().isEnabled()).isTrue();
		assertThatThrownBy(() -> registrationService.activateUser("TOKEN1234"))
			.hasMessage("Your Account is already activated")
			.isInstanceOf(RegistrationException.class);
		//expired token
		tokenStoreEntity.setExpiryDate(expired.toDate());
		assertThatThrownBy(() -> registrationService.activateUser("TOKEN1234"))
			.hasMessage("Activation Key is Expired")
			.isInstanceOf(RegistrationException.class);
		assertThatThrownBy(() -> registrationService.activateUser("BADTOKEN"))
			.hasMessage("Invalid Activation Key")
			.isInstanceOf(RegistrationException.class);
	}

	@Test
	public void testChangePassword() {
		TokenStoreEntity tokenStoreEntity = new TokenStoreEntity()
			.setUser(new UserEntity().setPassword("securepassword"))
			.setToken("TOKEN1234")
			.setExpiryDate(DateTime.now().plusDays(15).toDate())
			.setType(TokenStoreType.CHANGE_PASSWORD);
		when(tokenStoreRepository.findByTokenAndType("TOKEN1234", TokenStoreType.CHANGE_PASSWORD))
			.thenReturn(tokenStoreEntity);

		registrationService.changePassword("TOKEN1234", "notsecurepassword");
		assertThat(tokenStoreEntity.getUser().getPassword()).isEqualTo("notsecurepassword");

		//expired token
		tokenStoreEntity.setExpiryDate(DateTime.now().minusDays(15).toDate());
		assertThatThrownBy(() -> registrationService.changePassword("TOKEN1234","notsecurepassword"))
			.hasMessage("Activation Key is Expired")
			.isInstanceOf(RegistrationException.class);
		assertThatThrownBy(() -> registrationService.changePassword("BADTOKEN","notsecurepassword"))
			.hasMessage("Invalid Activation Key")
			.isInstanceOf(RegistrationException.class);
	}
}
