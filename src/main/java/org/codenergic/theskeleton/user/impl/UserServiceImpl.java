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

import java.util.Calendar;
import java.util.Date;
import java.util.Optional;
import java.util.Set;
import java.util.TimeZone;
import java.util.UUID;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.codenergic.theskeleton.privilege.RolePrivilegeEntity;
import org.codenergic.theskeleton.privilege.RolePrivilegeRepository;
import org.codenergic.theskeleton.user.UserAdminService;
import org.codenergic.theskeleton.user.UserEntity;
import org.codenergic.theskeleton.user.UserRepository;
import org.codenergic.theskeleton.user.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService, UserAdminService {
	private final PasswordEncoder passwordEncoder;
	private final UserRepository userRepository;
	private final RolePrivilegeRepository rolePrivilegeRepository;

	public UserServiceImpl(PasswordEncoder passwordEncoder, UserRepository userRepository, RolePrivilegeRepository rolePrivilegeRepository) {
		this.passwordEncoder = passwordEncoder;
		this.userRepository = userRepository;
		this.rolePrivilegeRepository = rolePrivilegeRepository;
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
	public Optional<UserEntity> findUserByEmail(String email) {
		return userRepository.findByEmail(email);
	}

	@Override
	public Optional<UserEntity> findUserByUsername(String username) {
		return userRepository.findByUsername(username);
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
				Set<RolePrivilegeEntity> rolePrivileges = u.getRoles().stream()
					.flatMap(role -> rolePrivilegeRepository.findByRoleCode(role.getRole().getCode()).stream())
					.collect(Collectors.toSet());
				return u.setAuthorities(rolePrivileges);
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
	public UserEntity saveUser(UserEntity userEntity) {
		userEntity.setId(null);
		userEntity.setExpiredAt(null);
		userEntity.setEnabled(true);
		userEntity.setAccountNonLocked(true);
		userEntity.setCredentialsNonExpired(true);
		userEntity.setPassword(UUID.randomUUID().toString());
		return userRepository.save(userEntity);
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
}
