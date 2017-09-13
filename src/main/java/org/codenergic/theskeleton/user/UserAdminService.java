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

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.codenergic.theskeleton.role.RoleEntity;
import org.codenergic.theskeleton.role.RolePrivilegeRepository;
import org.codenergic.theskeleton.role.RoleRepository;
import org.codenergic.theskeleton.user.impl.UserServiceImpl;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;

public interface UserAdminService {
	static UserAdminService newInstance(PasswordEncoder passwordEncoder, RoleRepository roleRepository, UserRepository userRepository,
			UserRoleRepository userRoleRepository, RolePrivilegeRepository rolePrivilegeRepository) {
		return new UserServiceImpl(passwordEncoder, roleRepository, userRepository, userRoleRepository, rolePrivilegeRepository);
	}

	@PreAuthorize("hasAuthority('user_assign_role')")
	UserEntity addRoleToUser(@NotNull String username, @NotNull String roleCode);

	@PreAuthorize("hasAuthority('user_delete')")
	void deleteUser(@NotNull String username);

	@PreAuthorize("hasAuthority('user_update')")
	UserEntity enableOrDisableUser(@NotNull String username, boolean enabled);

	@PreAuthorize("hasAuthority('user_update')")
	UserEntity extendsUserExpiration(@NotNull String username, int amountInMinutes);

	@PreAuthorize("hasAuthority('user_assign_role')")
	Set<RoleEntity> findRolesByUserUsername(@NotNull String username);

	@PreAuthorize("hasAuthority('user_read_all')")
	Page<UserEntity> findUsersByUsernameStartingWith(@NotNull String username, Pageable pageable);

	@PreAuthorize("hasAuthority('user_update')")
	UserEntity lockOrUnlockUser(@NotNull String username, boolean unlocked);

	@PreAuthorize("hasAuthority('user_assign_role')")
	UserEntity removeRoleFromUser(@NotNull String username, @NotNull String roleCode);

	@PreAuthorize("hasAuthority('user_write')")
	UserEntity saveUser(@NotNull @Valid UserEntity userEntity);

	@PreAuthorize("hasAuthority('user_update')")
	UserEntity updateUser(@NotNull String username, @NotNull @Valid UserEntity newUser);

	@PreAuthorize("hasAuthority('user_update')")
	UserEntity updateUserExpirationDate(@NotNull String username, Date date);

	@PreAuthorize("hasAuthority('user_update')")
	UserEntity updateUserPassword(@NotNull String username, @NotNull String rawPassword);
}
