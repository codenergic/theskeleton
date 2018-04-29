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

import java.util.Set;
import java.util.stream.Collectors;

import org.codenergic.theskeleton.privilege.PrivilegeEntity;
import org.codenergic.theskeleton.privilege.PrivilegeRepository;
import org.codenergic.theskeleton.privilege.PrivilegeService;
import org.codenergic.theskeleton.privilege.RolePrivilegeEntity;
import org.codenergic.theskeleton.privilege.RolePrivilegeRepository;
import org.codenergic.theskeleton.role.RoleEntity;
import org.codenergic.theskeleton.role.RoleRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class PrivilegeServiceImpl implements PrivilegeService {
	private final PrivilegeRepository privilegeRepository;
	private final RoleRepository roleRepository;
	private final RolePrivilegeRepository rolePrivilegeRepository;

	public PrivilegeServiceImpl(PrivilegeRepository privilegeRepository, RoleRepository roleRepository, RolePrivilegeRepository rolePrivilegeRepository) {
		this.privilegeRepository = privilegeRepository;
		this.roleRepository = roleRepository;
		this.rolePrivilegeRepository = rolePrivilegeRepository;
	}

	@Override
	@Transactional
	public RoleEntity addPrivilegeToRole(String code, String privilegeName) {
		RoleEntity role = roleRepository.findByCode(code);
		PrivilegeEntity privilege = privilegeRepository.findByName(privilegeName);
		return rolePrivilegeRepository.save(new RolePrivilegeEntity(role, privilege)).getRole();
	}

	@Override
	public PrivilegeEntity findPrivilegeById(String id) {
		return privilegeRepository.findOne(id);
	}

	@Override
	public PrivilegeEntity findPrivilegeByIdOrName(String idOrName) {
		PrivilegeEntity privilege = findPrivilegeById(idOrName);
		return privilege != null ? privilege : findPrivilegeByName(idOrName);
	}

	@Override
	public PrivilegeEntity findPrivilegeByName(String name) {
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
	@Transactional
	public RoleEntity removePrivilegeFromRole(String code, String privilegeName) {
		RolePrivilegeEntity userRole = rolePrivilegeRepository.findByRoleCodeAndPrivilegeName(code, privilegeName);
		rolePrivilegeRepository.delete(userRole);
		return roleRepository.findByCode(code);
	}
}
