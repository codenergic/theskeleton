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

import java.util.Optional;

import org.codenergic.theskeleton.core.data.Activeable;
import org.codenergic.theskeleton.tokenstore.TokenStoreEntity;
import org.codenergic.theskeleton.tokenstore.TokenStoreRepository;
import org.codenergic.theskeleton.tokenstore.TokenStoreType;
import org.codenergic.theskeleton.user.UserEntity;
import org.codenergic.theskeleton.user.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class RegistrationServiceImpl implements RegistrationService {
	private UserRepository userRepository;
	private TokenStoreRepository tokenStoreRepository;
	private PasswordEncoder passwordEncoder;

	public RegistrationServiceImpl(UserRepository userRepository, TokenStoreRepository tokenStoreRepository, PasswordEncoder passwordEncoder) {
		this.userRepository = userRepository;
		this.tokenStoreRepository = tokenStoreRepository;
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

	@Override
	public Optional<UserEntity> findUserByEmail(String email) {
		return userRepository.findByEmail(email);
	}

	@Override
	@Transactional
	public boolean activateUser(String activationToken) {
		TokenStoreEntity tokenStoreEntity = tokenStoreRepository.findByTokenAndType(activationToken, TokenStoreType.USER_ACTIVATION)
			.orElseThrow(() -> new RegistrationException("Invalid Activation Key"));
		if (tokenStoreEntity.isTokenExpired())
			throw new RegistrationException("Activation Key is Expired");
		if (Activeable.Status.INACTIVE.getStatus() == tokenStoreEntity.getStatus())
			throw new RegistrationException("Your Account is already activated");
		UserEntity user = tokenStoreEntity.getUser();
		user.setEnabled(true);
		tokenStoreEntity.setStatus(Activeable.Status.INACTIVE.getStatus());
		return true;
	}

	@Override
	@Transactional
	public boolean changePassword(String activationToken, String password) {
		TokenStoreEntity tokenStoreEntity = tokenStoreRepository.findByTokenAndType(activationToken, TokenStoreType.CHANGE_PASSWORD)
			.orElseThrow(() -> new RegistrationException("Invalid Activation Key"));
		if (tokenStoreEntity.isTokenExpired())
			throw new RegistrationException("Activation Key is Expired");
		UserEntity user = tokenStoreEntity.getUser();
		user.setPassword(passwordEncoder.encode(password));
		tokenStoreEntity.setStatus(Activeable.Status.INACTIVE.getStatus());
		return true;
	}
}
