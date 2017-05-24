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

import org.codenergic.theskeleton.role.RoleEntity;
import org.codenergic.theskeleton.role.RoleRepository;
import org.codenergic.theskeleton.role.RoleService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

@Service
@Transactional(readOnly = true)
public class RoleServiceImpl implements RoleService {
	private RoleRepository roleRepository;

	public RoleServiceImpl(RoleRepository roleRepository) {
		this.roleRepository = roleRepository;
	}

	@Override
	@Transactional
	public void deleteRole(String idOrCode) {
		RoleEntity e = findRoleByIdOrCode(idOrCode);
		Assert.notNull(e, "Role not found");
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
		Assert.notNull(e, "Role not found");
		e.setCode(role.getCode());
		e.setDescription(role.getDescription());
		return e;
	}
}
