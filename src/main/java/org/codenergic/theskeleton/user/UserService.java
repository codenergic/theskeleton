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

import javax.validation.constraints.NotNull;

import org.codenergic.theskeleton.privilege.role.RolePrivilegeRepository;
import org.codenergic.theskeleton.privilege.role.RoleRepository;
import org.codenergic.theskeleton.user.impl.UserServiceImpl;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;

public interface UserService extends UserDetailsService {
	static UserService newInstance(PasswordEncoder passwordEncoder, RoleRepository roleRepository, UserRepository userRepository,
			UserRoleRepository userRoleRepository, RolePrivilegeRepository rolePrivilegeRepository) {
		return new UserServiceImpl(passwordEncoder, roleRepository, userRepository, userRoleRepository, rolePrivilegeRepository);
	}

	@PreAuthorize("hasAuthority('user_read')")
	UserEntity findUserByEmail(@NotNull String email);

	@PreAuthorize("hasAuthority('user_read')")
	UserEntity findUserByUsername(@NotNull String username);
}
