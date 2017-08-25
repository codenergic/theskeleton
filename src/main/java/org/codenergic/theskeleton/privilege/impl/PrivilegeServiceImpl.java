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

import org.codenergic.theskeleton.privilege.PrivilegeEntity;
import org.codenergic.theskeleton.privilege.PrivilegeRepository;
import org.codenergic.theskeleton.privilege.PrivilegeService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class PrivilegeServiceImpl implements PrivilegeService {
	private PrivilegeRepository privilegeRepository;

	public PrivilegeServiceImpl(PrivilegeRepository privilegeRepository) {
		this.privilegeRepository = privilegeRepository;
	}

	@Override
	public PrivilegeEntity findPrivilegeByName(String name) {
		return privilegeRepository.findByName(name);
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
	public Page<PrivilegeEntity> findPrivileges(Pageable pageable) {
		return privilegeRepository.findAll(pageable);
	}

	@Override
	public Page<PrivilegeEntity> findPrivileges(String keyword, Pageable pageable) {
		return privilegeRepository.findByNameOrDescriptionStartsWith(keyword, pageable);
	}
}
