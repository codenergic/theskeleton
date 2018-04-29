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
package org.codenergic.theskeleton.privilege;

import java.util.Set;

import javax.validation.constraints.NotNull;

import org.codenergic.theskeleton.role.RoleEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;

public interface PrivilegeService {
	@PreAuthorize("hasAuthority('role_assign_privilege')")
	RoleEntity addPrivilegeToRole(@NotNull String code, @NotNull String privilegeName);

	@PreAuthorize("isAuthenticated()")
	PrivilegeEntity findPrivilegeById(@NotNull String id);

	@PreAuthorize("isAuthenticated()")
	PrivilegeEntity findPrivilegeByIdOrName(@NotNull String idOrName);

	@PreAuthorize("isAuthenticated()")
	PrivilegeEntity findPrivilegeByName(@NotNull String name);

	@PreAuthorize("isAuthenticated()")
	Page<PrivilegeEntity> findPrivileges(String keyword, Pageable pageable);

	@PreAuthorize("isAuthenticated()")
	Page<PrivilegeEntity> findPrivileges(Pageable pageable);

	@PreAuthorize("hasAuthority('role_assign_privilege')")
	Set<PrivilegeEntity> findPrivilegesByRoleCode(@NotNull String code);

	@PreAuthorize("hasAuthority('role_assign_privilege')")
	RoleEntity removePrivilegeFromRole(@NotNull String code, @NotNull String privilegeName);
}
