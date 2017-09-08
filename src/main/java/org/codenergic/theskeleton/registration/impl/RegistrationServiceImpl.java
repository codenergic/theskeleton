package org.codenergic.theskeleton.registration.impl;

import org.codenergic.theskeleton.registration.RegistrationException;
import org.codenergic.theskeleton.registration.RegistrationForm;
import org.codenergic.theskeleton.registration.RegistrationService;
import org.codenergic.theskeleton.user.UserEntity;
import org.codenergic.theskeleton.user.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RegistrationServiceImpl implements RegistrationService {
	private UserRepository userRepository;
	private PasswordEncoder passwordEncoder;

	public RegistrationServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
	}

	@Override
	public boolean isEmailExists(String email) {
		return userRepository.existsByEmail(email);
	}

	@Override
	public boolean isUsernameExists(String username) {
		return userRepository.existsByUsername(username);
	}

	@Override
	@Transactional
	public UserEntity registerUser(RegistrationForm form) {
		if (isEmailExists(form.getEmail()) || isUsernameExists(form.getUsername()))
			throw new RegistrationException("Username or email already exists");
		UserEntity user = new UserEntity()
				.setUsername(form.getUsername())
				.setEmail(form.getEmail())
				.setPassword(passwordEncoder.encode(form.getPassword()))
				.setExpiredAt(null)
				.setAccountNonLocked(true)
				.setCredentialsNonExpired(true);
		return userRepository.save(user);
	}

}
