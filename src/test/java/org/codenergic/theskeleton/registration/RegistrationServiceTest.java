package org.codenergic.theskeleton.registration;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.codenergic.theskeleton.registration.impl.RegistrationServiceImpl;
import org.codenergic.theskeleton.user.UserRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

public class RegistrationServiceTest {
	private RegistrationService registrationService;
	@Mock
	private UserRepository userRepository;
	private PasswordEncoder passwordEncoder = NoOpPasswordEncoder.getInstance();

	@Before
	public void init() {
		MockitoAnnotations.initMocks(this);
		registrationService = new RegistrationServiceImpl(userRepository, passwordEncoder);
	}

	@Test
	public void testRegisterUserEmailDoesntExists() {
		when(userRepository.existsByEmail(anyString())).thenReturn(false);
		when(userRepository.existsByUsername(anyString())).thenReturn(true);
		assertThatThrownBy(() -> registrationService.registerUser(new RegistrationForm()))
				.isInstanceOf(RegistrationException.class);
		verify(userRepository).existsByEmail(anyString());
		verify(userRepository).existsByUsername(anyString());
	}

	@Test
	public void testRegisterUserUsernameDoesntExists() {
		when(userRepository.existsByEmail(anyString())).thenReturn(true);
		when(userRepository.existsByUsername(anyString())).thenReturn(false);
		assertThatThrownBy(() -> registrationService.registerUser(new RegistrationForm()))
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
		registrationService.registerUser(registrationForm);
		verify(userRepository).existsByEmail(anyString());
		verify(userRepository).existsByUsername(anyString());
	}
}
