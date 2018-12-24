/*
 * Copyright 2018 the original author or authors.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.codenergic.theskeleton.role;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.codenergic.theskeleton.user.UserEntity;
import org.codenergic.theskeleton.user.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
class RoleServiceImpl implements RoleService {
	private final RoleRepository roleRepository;
	private final UserRepository userRepository;
	private final UserRoleRepository userRoleRepository;

	public RoleServiceImpl(RoleRepository roleRepository, UserRepository userRepository, UserRoleRepository userRoleRepository) {
		this.roleRepository = roleRepository;
		this.userRepository = userRepository;
		this.userRoleRepository = userRoleRepository;
	}

	@Override
	@Transactional
	public UserEntity addRoleToUser(String username, String roleCode) {
		UserEntity user = userRepository.findByUsername(username)
			.orElseThrow(() -> new UsernameNotFoundException(username));
		RoleEntity role = roleRepository.findByCode(roleCode)
			.orElseThrow(() -> new RoleNotFoundException(roleCode));
		return userRoleRepository.save(new UserRoleEntity(user, role)).getUser();
	}

	@Override
	@Transactional
	public void deleteRole(String idOrCode) {
		RoleEntity e = findRoleByIdOrCode(idOrCode)
			.orElseThrow(IllegalArgumentException::new);
		roleRepository.delete(e);
	}

	@Override
	public Optional<RoleEntity> findRoleByCode(String code) {
		return roleRepository.findByCode(code);
	}

	@Override
	public Optional<RoleEntity> findRoleById(String id) {
		return roleRepository.findById(id);
	}

	@Override
	public Optional<RoleEntity> findRoleByIdOrCode(String idOrCode) {
		return Stream.of(findRoleById(idOrCode), findRoleByCode(idOrCode))
			.filter(Optional::isPresent)
			.map(Optional::get)
			.findFirst();
	}

	@Override
	public Page<RoleEntity> findRoles(String keywords, Pageable pageable) {
		return roleRepository.findByCodeOrDescriptionStartsWith(keywords, pageable);
	}

	@Override
	public Set<RoleEntity> findRolesByUserUsername(String username) {
		return userRoleRepository.findByUserUsername(username).stream()
			.map(UserRoleEntity::getRole)
			.collect(Collectors.toSet());
	}

	@Override
	@Transactional
	public UserEntity removeRoleFromUser(String username, String roleCode) {
		userRoleRepository.findByUserUsernameAndRoleCode(username, roleCode)
			.ifPresent(userRoleRepository::delete);
		return userRepository.findByUsername(username)
			.orElseThrow(() -> new UsernameNotFoundException(username));
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
		RoleEntity e = findRoleByIdOrCode(id)
			.orElseThrow(() -> new RoleNotFoundException(id));
		e.setCode(role.getCode());
		e.setDescription(role.getDescription());
		return e;
	}
}
