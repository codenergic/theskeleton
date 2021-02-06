/*
 * Copyright 2018 the original author or authors.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package org.codenergic.theskeleton.registration;

import java.util.stream.Stream;

import org.codenergic.theskeleton.tokenstore.TokenStoreRestData;
import org.codenergic.theskeleton.tokenstore.TokenStoreService;
import org.codenergic.theskeleton.tokenstore.TokenStoreType;
import org.codenergic.theskeleton.user.UserEntity;
import org.codenergic.theskeleton.user.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.jwt.crypto.sign.InvalidSignatureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationAdapter;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@Service
@Transactional(readOnly = true)
class RegistrationServiceImpl implements RegistrationService {
	private final UserRepository userRepository;
	private final TokenStoreService tokenStoreService;
	private final PasswordEncoder passwordEncoder;

	public RegistrationServiceImpl(UserRepository userRepository, TokenStoreService tokenStoreService, PasswordEncoder passwordEncoder) {
		this.userRepository = userRepository;
		this.tokenStoreService = tokenStoreService;
		this.passwordEncoder = passwordEncoder;
	}

	@Override
	@Transactional
	public UserEntity registerUser(RegistrationForm form) {
		userRepository.findByUsernameOrEmailAndEnabled(form.getUsername(), form.getEmail(), true).findFirst().ifPresent(user -> {
			throw new RegistrationException("Username or email already exists");
		});

		Stream<UserEntity> existingUsers = userRepository.findByUsernameOrEmailAndEnabled(form.getUsername(), form.getEmail(), false);
		final UserEntity user = existingUsers.findFirst().orElse(new UserEntity())
			.setUsername(form.getUsername())
			.setEmail(form.getEmail())
			.setPassword(passwordEncoder.encode(form.getPassword()))
			.setExpiredAt(null)
			.setAccountNonLocked(true)
			.setCredentialsNonExpired(true);
		TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronizationAdapter() {
			@Override
			public void afterCommit() {
				tokenStoreService.sendTokenNotification(TokenStoreType.USER_ACTIVATION, user);
			}
		});
		return user.getId() == null ? userRepository.save(user) : user;
	}

	@Override
	@Transactional
	public void activateUser(String activationToken) {
		try {
			TokenStoreRestData token = tokenStoreService.findAndVerifyToken(activationToken);
			UserEntity user = (UserEntity) token.getUser();
			if (user.isEnabled()) {
				throw new RegistrationException("Your Account is already activated");
			}
			user.setEnabled(true);
		} catch (InvalidSignatureException e) {
			throw new RegistrationException("Invalid Activation Key");
		}
	}

	@Override
	@Transactional
	public void changePassword(String activationToken, String password) {
		try {
			TokenStoreRestData token = tokenStoreService.findAndVerifyToken(activationToken);
			if (token.isExpired()) {
				throw new RegistrationException("Key is Expired");
			}
			UserEntity user = (UserEntity) token.getUser();
			user.setPassword(passwordEncoder.encode(password));
		} catch (InvalidSignatureException e) {
			throw new RegistrationException("Invalid Token");
		}
	}
}
