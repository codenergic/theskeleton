package org.codenergic.theskeleton.registration.impl;

import org.apache.commons.lang3.RandomStringUtils;
import org.codenergic.theskeleton.core.data.Activeable;
import org.codenergic.theskeleton.core.mail.EmailService;
import org.codenergic.theskeleton.registration.*;
import org.codenergic.theskeleton.user.UserEntity;
import org.codenergic.theskeleton.user.UserRepository;
import org.joda.time.DateTime;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import java.util.HashMap;
import java.util.Map;

@Service
public class RegistrationServiceImpl implements RegistrationService {
	private UserRepository userRepository;
	private RegistrationRepository registrationRepository;
	private PasswordEncoder passwordEncoder;
	private EmailService emailService;
	public RegistrationServiceImpl(UserRepository userRepository, RegistrationRepository registrationRepository,
								   PasswordEncoder passwordEncoder, EmailService emailService) {
		this.userRepository = userRepository;
		this.registrationRepository = registrationRepository;
		this.passwordEncoder = passwordEncoder;
		this.emailService = emailService;
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

	@Override
	@Transactional
	public RegistrationEntity sendConfirmationNotification(UserEntity user, String host) {
		String token = RandomStringUtils.randomAlphabetic(24);
		RegistrationEntity registration = new RegistrationEntity()
			.setToken(token)
			.setUser(user)
			.setExpiryDate(DateTime.now().plusDays(15).toDate());
		Map<String, Object> params = new HashMap<>();
		params.put("activationUrl", "http://" + host + "/registration/activate?at=" + token);
		String subject = "Registration Confirmation";
		String template = "email/registration.html";
		try {
			emailService.sendEmail(null, new InternetAddress(user.getEmail()), subject, params, template);
		} catch (AddressException e) {
			throw new RegistrationException("Unable to send activation link");
		}
		return registrationRepository.save(registration);
	}

	@Override
	@Transactional
	public boolean activateUser(String activationToken) {
		RegistrationEntity registrationEntity = registrationRepository.findByToken(activationToken);
		if (registrationEntity == null)
			throw new RegistrationException("Invalid Activation Key");
		if (registrationEntity.isTokenExpired())
			throw new RegistrationException("Activation Key is Expired");
		if (Activeable.Status.INACTIVE.getStatus() == registrationEntity.getStatus())
			throw new RegistrationException("Your Account is already activated");
		UserEntity user = registrationEntity.getUser();
		user.setEnabled(true);
		registrationEntity.setStatus(Activeable.Status.INACTIVE.getStatus());
		return true;
	}
}
