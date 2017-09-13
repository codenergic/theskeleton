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
package org.codenergic.theskeleton.role.impl;

import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.codenergic.theskeleton.privilege.PrivilegeEntity;
import org.codenergic.theskeleton.privilege.PrivilegeRepository;
import org.codenergic.theskeleton.role.RoleEntity;
import org.codenergic.theskeleton.role.RolePrivilegeEntity;
import org.codenergic.theskeleton.role.RolePrivilegeRepository;
import org.codenergic.theskeleton.role.RoleRepository;
import org.codenergic.theskeleton.role.RoleService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class RoleServiceImpl implements RoleService {
	private RoleRepository roleRepository;
	private PrivilegeRepository privilegeRepository;
	private RolePrivilegeRepository rolePrivilegeRepository;

	public RoleServiceImpl(RoleRepository roleRepository, PrivilegeRepository privilegeRepository,
			RolePrivilegeRepository rolePrivilegeRepository) {
		this.roleRepository = roleRepository;
		this.privilegeRepository = privilegeRepository;
		this.rolePrivilegeRepository = rolePrivilegeRepository;
	}

	private void assertRoleNotNull(RoleEntity role) {
		Objects.requireNonNull(role, "Role not found");
	}

	@Override
	@Transactional
	public void deleteRole(String idOrCode) {
		RoleEntity e = findRoleByIdOrCode(idOrCode);
		assertRoleNotNull(e);
		roleRepository.delete(e);
	}

	@Override
	public RoleEntity findRoleByCode(String code) {
		return roleRepository.findByCode(code);
	}

	@Override
	public RoleEntity findRoleById(String id) {
		return roleRepository.findOne(id);
	}

	@Override
	public RoleEntity findRoleByIdOrCode(String idOrCode) {
		RoleEntity role = findRoleById(idOrCode);
		return role != null ? role : findRoleByCode(idOrCode);
	}

	@Override
	public Page<RoleEntity> findRoles(Pageable pageable) {
		return roleRepository.findAll(pageable);
	}

	@Override
	public Page<RoleEntity> findRoles(String keywords, Pageable pageable) {
		return roleRepository.findByCodeOrDescriptionStartsWith(keywords, pageable);
	}

	@Override
	@Transactional
	public RoleEntity saveRole(RoleEntity role) {
		role.setId(null);
		return roleRepository.save(role);
	}

	@Override
	@Transactional
	public RoleEntity updateRole(String id, RoleEntity role) {
		RoleEntity e = findRoleByIdOrCode(id);
		assertRoleNotNull(e);
		e.setCode(role.getCode());
		e.setDescription(role.getDescription());
		return e;
	}

	@Override
	@Transactional
	public RoleEntity addPrivilegeToRole(String code, String privilegeName) {
		RoleEntity role = findRoleByCode(code);
		PrivilegeEntity privilege = privilegeRepository.findByName(privilegeName);
		return rolePrivilegeRepository.save(new RolePrivilegeEntity(role, privilege)).getRole();
	}

	@Override
	@Transactional
	public RoleEntity removePrivilegeFromRole(String code, String privilegeName) {
		RolePrivilegeEntity userRole = rolePrivilegeRepository.findByRoleCodeAndPrivilegeName(code, privilegeName);
		rolePrivilegeRepository.delete(userRole);
		return findRoleByCode(code);
	}

	@Override
	public Set<PrivilegeEntity> findPrivilegesByRoleCode(String code) {
		return rolePrivilegeRepository.findByRoleCode(code).stream()
				.map(RolePrivilegeEntity::getPrivilege)
				.collect(Collectors.toSet());
	}

	@Override
	public Set<RoleEntity> findRolesByPrivilegeName(String name) {
		return rolePrivilegeRepository.findByPrivilegeName(name).stream()
				.map(RolePrivilegeEntity::getRole)
				.collect(Collectors.toSet());
	}
}
