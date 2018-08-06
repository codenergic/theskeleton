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
package org.codenergic.theskeleton.privilege.impl;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.codenergic.theskeleton.privilege.PrivilegeEntity;
import org.codenergic.theskeleton.privilege.PrivilegeNotFoundException;
import org.codenergic.theskeleton.privilege.PrivilegeRepository;
import org.codenergic.theskeleton.privilege.PrivilegeService;
import org.codenergic.theskeleton.privilege.RolePrivilegeEntity;
import org.codenergic.theskeleton.privilege.RolePrivilegeRepository;
import org.codenergic.theskeleton.role.RoleEntity;
import org.codenergic.theskeleton.role.RoleNotFoundException;
import org.codenergic.theskeleton.role.RoleRepository;
import org.codenergic.theskeleton.role.UserRoleEntity;
import org.codenergic.theskeleton.role.UserRoleRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class PrivilegeServiceImpl implements PrivilegeService {
	private final PrivilegeRepository privilegeRepository;
	private final RoleRepository roleRepository;
	private final RolePrivilegeRepository rolePrivilegeRepository;
	private final UserRoleRepository userRoleRepository;

	public PrivilegeServiceImpl(PrivilegeRepository privilegeRepository, RoleRepository roleRepository,
								RolePrivilegeRepository rolePrivilegeRepository, UserRoleRepository userRoleRepository) {
		this.privilegeRepository = privilegeRepository;
		this.roleRepository = roleRepository;
		this.rolePrivilegeRepository = rolePrivilegeRepository;
		this.userRoleRepository = userRoleRepository;
	}

	@Override
	@Transactional
	public RoleEntity addPrivilegeToRole(String code, String privilegeName) {
		RoleEntity role = roleRepository.findByCode(code)
			.orElseThrow(() -> new RoleNotFoundException(code));
		PrivilegeEntity privilege = privilegeRepository.findByName(privilegeName)
			.orElseThrow(() -> new PrivilegeNotFoundException(privilegeName));
		return rolePrivilegeRepository.save(new RolePrivilegeEntity(role, privilege)).getRole();
	}

	@Override
	public Optional<PrivilegeEntity> findPrivilegeById(String id) {
		return privilegeRepository.findById(id);
	}

	@Override
	public Optional<PrivilegeEntity> findPrivilegeByIdOrName(String idOrName) {
		return Stream.of(findPrivilegeById(idOrName), findPrivilegeByName(idOrName))
			.filter(Optional::isPresent)
			.map(Optional::get)
			.findFirst();
	}

	@Override
	public Optional<PrivilegeEntity> findPrivilegeByName(String name) {
		return privilegeRepository.findByName(name);
	}

	@Override
	public Page<PrivilegeEntity> findPrivileges(String keyword, Pageable pageable) {
		return privilegeRepository.findByNameOrDescriptionStartsWith(keyword, pageable);
	}

	@Override
	public Page<PrivilegeEntity> findPrivileges(Pageable pageable) {
		return privilegeRepository.findAll(pageable);
	}

	@Override
	public Set<PrivilegeEntity> findPrivilegesByRoleCode(String code) {
		return rolePrivilegeRepository.findByRoleCode(code).stream()
			.map(RolePrivilegeEntity::getPrivilege)
			.collect(Collectors.toSet());
	}

	@Override
	public Set<RolePrivilegeEntity> getAuthorities(UserDetails user) {
		Set<String> roles = userRoleRepository.findByUserUsername(user.getUsername()).stream()
			.map(UserRoleEntity::getRole)
			.map(RoleEntity::getCode)
			.collect(Collectors.toSet());
		return rolePrivilegeRepository.findByRoleCodeIn(roles);
	}

	@Override
	@Transactional
	public RoleEntity removePrivilegeFromRole(String code, String privilegeName) {
		rolePrivilegeRepository.findByRoleCodeAndPrivilegeName(code, privilegeName)
			.ifPresent(rolePrivilegeRepository::delete);
		return roleRepository.findByCode(code)
			.orElseThrow(() -> new RoleNotFoundException(code));
	}
}
