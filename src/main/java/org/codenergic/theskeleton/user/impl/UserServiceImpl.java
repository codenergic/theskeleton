/*
 * Copyright 2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.codenergic.theskeleton.user.impl;

import java.io.InputStream;
import java.time.Instant;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.TimeZone;
import java.util.UUID;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.StringUtils;
import org.codenergic.theskeleton.core.security.User;
import org.codenergic.theskeleton.user.UserAuthorityService;
import org.codenergic.theskeleton.user.UserEntity;
import org.codenergic.theskeleton.user.UserOAuth2ClientApprovalEntity;
import org.codenergic.theskeleton.user.UserOAuth2ClientApprovalRepository;
import org.codenergic.theskeleton.user.UserRepository;
import org.codenergic.theskeleton.user.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.minio.MinioClient;

@Service
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {
	private static final String PICTURE_BUCKET_NAME = "profile-pictures";
	private final MinioClient minioClient;
	private final PasswordEncoder passwordEncoder;
	private final UserRepository userRepository;
	private final UserAuthorityService<? extends GrantedAuthority> userAuthorityService;
	private final UserOAuth2ClientApprovalRepository clientApprovalRepository;
	private final SessionRegistry sessionRegistry;

	public UserServiceImpl(MinioClient minioClient, PasswordEncoder passwordEncoder, UserRepository userRepository,
						   UserAuthorityService<? extends GrantedAuthority> userAuthorityService, UserOAuth2ClientApprovalRepository clientApprovalRepository,
						   SessionRegistry sessionRegistry) {
		this.minioClient = minioClient;
		this.passwordEncoder = passwordEncoder;
		this.userRepository = userRepository;
		this.userAuthorityService = userAuthorityService;
		this.clientApprovalRepository = clientApprovalRepository;
		this.sessionRegistry = sessionRegistry;
	}

	@Override
	@Transactional
	public void deleteUser(String username) {
		UserEntity user = findUserByUsername(username)
			.orElseThrow(throwUsernameNotFound(username));
		userRepository.delete(user);
	}

	@Override
	@Transactional
	public UserEntity enableOrDisableUser(String username, boolean enabled) {
		UserEntity user = findUserByUsername(username)
			.orElseThrow(throwUsernameNotFound(username));
		user.setEnabled(enabled);
		return user;
	}

	@Override
	@Transactional
	public UserEntity extendsUserExpiration(String username, int amountInMinutes) {
		Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
		calendar.add(Calendar.MINUTE, amountInMinutes);
		return updateUserExpirationDate(username, calendar.getTime());
	}

	@Override
	public List<SessionInformation> findUserActiveSessions(@NotNull String username) {
		return sessionRegistry.getAllPrincipals().stream()
			.filter(principal -> principal instanceof User)
			.distinct()
			.filter(principal -> ((User) principal).getUsername().equals(username))
			.flatMap(principal -> sessionRegistry.getAllSessions(principal, true).stream())
			.map(session -> {
				SessionInformation newSession = new SessionInformation(username, session.getSessionId(), session.getLastRequest());
				if (session.isExpired()) newSession.expireNow();
				return newSession;
			})
			.collect(Collectors.toList());
	}

	@Override
	public Optional<UserEntity> findUserByEmail(String email) {
		return userRepository.findByEmail(email);
	}

	@Override
	public Optional<UserEntity> findUserByUsername(String username) {
		return userRepository.findByUsername(username);
	}

	@Override
	public List<UserOAuth2ClientApprovalEntity> findUserOAuth2ClientApprovalByUsername(String username) {
		return clientApprovalRepository.findByUserUsername(username);
	}

	@Override
	public Page<UserEntity> findUsersByUsernameStartingWith(String username, Pageable pageable) {
		return userRepository.findByUsernameStartingWith(username, pageable);
	}

	@Override
	public UserDetails loadUserByUsername(String username) {
		return Stream.of(findUserByUsername(username), findUserByEmail(username), userRepository.findById(username))
			.filter(Optional::isPresent)
			.map(Optional::get)
			.findFirst()
			.map(u -> {
				Set<? extends GrantedAuthority> authorities = userAuthorityService.getAuthorities(u);
				return u.setAuthorities(Collections.unmodifiableSet(new HashSet<>(authorities)));
			})
			.orElseThrow(throwUsernameNotFound(username));
	}

	@Override
	@Transactional
	public UserEntity lockOrUnlockUser(String username, boolean unlocked) {
		UserEntity user = findUserByUsername(username)
			.orElseThrow(throwUsernameNotFound(username));
		user.setAccountNonLocked(unlocked);
		return user;
	}

	@Override
	@Transactional
	public void removeUserOAuth2ClientApprovalByUsername(String username, String clientId) {
		clientApprovalRepository.deleteByUserUsernameAndClientId(username, clientId);
	}

	@Override
	public void revokeUserSession(String username, String sessionId) {
		sessionRegistry.removeSessionInformation(sessionId);
	}

	@Override
	@Transactional
	public UserEntity saveUser(UserEntity user) {
		user.setId(null);
		user.setExpiredAt(null);
		user.setEnabled(true);
		user.setAccountNonLocked(true);
		user.setCredentialsNonExpired(true);
		user.setPassword(UUID.randomUUID().toString());
		return userRepository.save(user);
	}

	private Supplier<UsernameNotFoundException> throwUsernameNotFound(String username) {
		return () -> new UsernameNotFoundException(username);
	}

	@Override
	@Transactional
	public UserEntity updateUser(String username, UserEntity newUser) {
		UserEntity user = findUserByUsername(username)
			.orElseThrow(throwUsernameNotFound(username));
		user.setUsername(newUser.getUsername());
		user.setEmail(newUser.getEmail());
		user.setPhoneNumber(newUser.getPhoneNumber());
		return user;
	}

	@Override
	@Transactional
	public UserEntity updateUserExpirationDate(String username, Date date) {
		UserEntity user = findUserByUsername(username)
			.orElseThrow(throwUsernameNotFound(username));
		user.setExpiredAt(date);
		return user;
	}

	@Override
	@Transactional
	public UserEntity updateUserPassword(String username, String rawPassword) {
		UserEntity user = findUserByUsername(username)
			.orElseThrow(throwUsernameNotFound(username));
		user.setPassword(passwordEncoder.encode(rawPassword));
		return user;
	}

	@Override
	@Transactional
	public UserEntity updateUserPicture(String username, InputStream image, String contentType) throws Exception {
		UserEntity user = findUserByUsername(username)
			.orElseThrow(() -> new UsernameNotFoundException(username));
		String imageObjectName = StringUtils.join(user.getId(), "/", Long.toHexString(Instant.now().toEpochMilli()),
			"-", UUID.randomUUID().toString());
		minioClient.putObject(PICTURE_BUCKET_NAME, imageObjectName, image, contentType);
		user.setPictureUrl(minioClient.getObjectUrl(PICTURE_BUCKET_NAME, imageObjectName));
		return user;
	}
}
