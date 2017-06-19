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
import java.util.Objects;
import java.util.Set;
import java.util.TimeZone;
import java.util.UUID;
import java.util.stream.Collectors;

import org.codenergic.theskeleton.role.RoleEntity;
import org.codenergic.theskeleton.role.RoleRepository;
import org.codenergic.theskeleton.user.UserEntity;
import org.codenergic.theskeleton.user.UserRepository;
import org.codenergic.theskeleton.user.UserRoleEntity;
import org.codenergic.theskeleton.user.UserRoleRepository;
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
public class UserServiceImpl implements UserService {
	private PasswordEncoder passwordEncoder;
	private RoleRepository roleRepository;
	private UserRepository userRepository;
	private UserRoleRepository userRoleRepository;

	public UserServiceImpl(PasswordEncoder passwordEncoder, RoleRepository roleRepository, UserRepository userRepository,
			UserRoleRepository userRoleRepository) {
		this.passwordEncoder = passwordEncoder;
		this.roleRepository = roleRepository;
		this.userRepository = userRepository;
		this.userRoleRepository = userRoleRepository;
	}

	@Override
	@Transactional
	public UserEntity addRoleToUser(String username, String roleCode) {
		UserEntity user = findUserByUsername(username);
		RoleEntity role = roleRepository.findByCode(roleCode);
		return userRoleRepository.save(new UserRoleEntity(user, role)).getUser();
	}

	@Override
	@Transactional
	public void deleteUser(String username) {
		UserEntity user = findUserByUsername(username);
		Objects.requireNonNull(user, "User not found");
		userRepository.delete(user);
	}

	@Override
	@Transactional
	public UserEntity enableOrDisableUser(String username, boolean enabled) {
		UserEntity user = findUserByUsername(username);
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
	public Set<RoleEntity> findRolesByUserUsername(String username) {
		return userRoleRepository.findByUserUsername(username).stream()
				.map(UserRoleEntity::getRole)
				.collect(Collectors.toSet());
	}

	@Override
	public UserEntity findUserByEmail(String email) {
		return userRepository.findByEmail(email);
	}

	@Override
	public UserEntity findUserByUsername(String username) {
		return userRepository.findByUsername(username);
	}

	@Override
	public Set<UserEntity> findUsersByRoleCode(String code) {
		return userRoleRepository.findByRoleCode(code).stream()
				.map(UserRoleEntity::getUser)
				.collect(Collectors.toSet());
	}

	@Override
	public Page<UserEntity> findUsersByUsernameStartingWith(String username, Pageable pageable) {
		return userRepository.findByUsernameStartingWith(username, pageable);
	}

	@Override
	@Transactional
	public UserEntity lockOrUnlockUser(String username, boolean unlocked) {
		UserEntity user = findUserByUsername(username);
		user.setAccountNonLocked(unlocked);
		return user;
	}

	@Override
	@Transactional
	public UserEntity removeRoleFromUser(String username, String roleCode) {
		UserRoleEntity userRole = userRoleRepository.findByUserUsernameAndRoleCode(username, roleCode);
		userRoleRepository.delete(userRole);
		return findUserByUsername(username);
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

	@Override
	@Transactional
	public UserEntity updateUser(String username, UserEntity newUser) {
		UserEntity user = findUserByUsername(username);
		user.setUsername(newUser.getUsername());
		user.setEmail(newUser.getEmail());
		user.setPhoneNumber(newUser.getPhoneNumber());
		return user;
	}

	@Override
	@Transactional
	public UserEntity updateUserExpirationDate(String username, Date date) {
		UserEntity user = findUserByUsername(username);
		user.setExpiredAt(date);
		return user;
	}

	@Override
	@Transactional
	public UserEntity updateUserPassword(String username, String rawPassword) {
		UserEntity user = findUserByUsername(username);
		user.setPassword(passwordEncoder.encode(rawPassword));
		return user;
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		UserEntity user = findUserByUsername(username);
		if (user == null)
			user = findUserByEmail(username);
		if (user == null)
			throw new UsernameNotFoundException("Cannot find user with username or email of " + username);
		return user.setAuthorities(userRoleRepository.findByUserUsername(username));
	}
}
