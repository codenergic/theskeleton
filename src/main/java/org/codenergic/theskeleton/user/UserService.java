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
package org.codenergic.theskeleton.user;

import java.util.Date;
import java.util.Set;

import org.codenergic.theskeleton.role.RoleEntity;
import org.codenergic.theskeleton.user.impl.UserServiceImpl;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;

public interface UserService {
	static UserService newInstance(PasswordEncoder passwordEncoder, UserRepository userRepository,
			UserRoleRepository userRoleRepository) {
		return new UserServiceImpl(passwordEncoder, userRepository, userRoleRepository);
	}

	UserEntity enableOrDisableUser(String username, boolean enabled);

	UserEntity extendsUserExpiration(String username, int amountInMinutes);

	Set<RoleEntity> findRolesByUserUsername(String username);

	UserEntity findUserByEmail(String email);

	UserEntity findUserByUsername(String username);

	Set<UserEntity> findUsersByRoleCode(String code);

	Page<UserEntity> findUsersByUsernameStartingWith(String username, Pageable pageable);

	UserEntity lockOrUnlockUser(String username, boolean unlocked);

	UserEntity saveUser(UserEntity userEntity);

	UserEntity updateUser(String username, UserEntity newUser);

	UserEntity updateUserExpirationDate(String username, Date date);

	UserEntity updateUserPassword(String username, String rawPassword);
}
