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
package org.codenergic.theskeleton.role;

import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.codenergic.theskeleton.role.impl.RoleServiceImpl;
import org.codenergic.theskeleton.user.UserEntity;
import org.codenergic.theskeleton.user.UserRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class RoleServiceTest {
	private RoleService roleService;
	@Mock
	private RoleRepository roleRepository;
	@Mock
	private UserRepository userRepository;
	@Mock
	private UserRoleRepository userRoleRepository;

	@Before
	public void init() {
		MockitoAnnotations.initMocks(this);
		this.roleService = new RoleServiceImpl(roleRepository, userRepository, userRoleRepository);
	}

	@Test
	public void testAddRoleToUser() {
		RoleEntity role = new RoleEntity()
			.setId(UUID.randomUUID().toString())
			.setCode("role")
			.setPrivileges(new HashSet<>());
		role.getPrivileges();
		UserEntity user = new UserEntity()
			.setId(UUID.randomUUID().toString())
			.setUsername("user");
		UserRoleEntity result = new UserRoleEntity(user, role);
		result.setId(UUID.randomUUID().toString());
		when(roleRepository.findByCode("role")).thenReturn(Optional.of(role));
		when(userRepository.findByUsername("user")).thenReturn(Optional.of(user));
		when(userRoleRepository.save(any(UserRoleEntity.class))).thenReturn(result);
		assertThat(roleService.addRoleToUser("user", "role")).isEqualTo(user);
		verify(roleRepository).findByCode("role");
		verify(userRepository).findByUsername("user");
		verify(userRoleRepository).save(any(UserRoleEntity.class));
	}

	@Test
	@SuppressWarnings("serial")
	public void testDeleteRole() {
		RoleEntity input = new RoleEntity() {{ setId("123"); }}.setCode("user");
		when(roleRepository.findByCode("123")).thenReturn(Optional.of(input));
		roleService.deleteRole("123");
		verify(roleRepository).findByCode("123");
		verify(roleRepository).delete(input);
	}

	@Test
	public void testFindRoleByCode() {
		RoleEntity result = new RoleEntity();
		result.setCode("user");
		when(roleRepository.findByCode(eq("user"))).thenReturn(Optional.of(result));
		assertThat(roleService.findRoleByCode("user").orElse(new RoleEntity())).isEqualTo(result);
		verify(roleRepository).findByCode(eq("user"));
		when(roleRepository.findByCode(eq("admin"))).thenReturn(Optional.empty());
		assertThat(roleService.findRoleByCode("admin").isPresent()).isFalse();
		verify(roleRepository).findByCode(eq("admin"));
	}

	@Test
	@SuppressWarnings("serial")
	public void testFindRoleById() {
		RoleEntity result = new RoleEntity() {{ setId("123"); }}.setCode("user");
		when(roleRepository.findById(eq("123"))).thenReturn(Optional.of(result));
		assertThat(roleService.findRoleById("123").orElse(new RoleEntity()))
			.isEqualTo(result);
		verify(roleRepository).findById(eq("123"));
		when(roleRepository.findById(eq("124"))).thenReturn(Optional.empty());
		assertThat(roleService.findRoleById("124").isPresent()).isFalse();
		verify(roleRepository).findById(eq("124"));
	}

	@Test
	@SuppressWarnings("serial")
	public void testFindRoles() {
		RoleEntity result = new RoleEntity() {{ setId("123"); }}.setCode("user");
		Page<RoleEntity> page = new PageImpl<>(Collections.singletonList(result));
		when(roleRepository.findByCodeOrDescriptionStartsWith(anyString(), any(Pageable.class))).thenReturn(page);
		assertThat(roleService.findRoles("", new PageRequest(1, 10))).isEqualTo(page);
		verify(roleRepository).findByCodeOrDescriptionStartsWith(anyString(), any(Pageable.class));
	}

	@Test
	public void testFindRolesByUserUsername() {
		Set<UserRoleEntity> dbResult =
			new HashSet<>(Collections.singletonList(new UserRoleEntity()
				.setRole(new RoleEntity().setCode("role"))
				.setUser(new UserEntity())));
		when(userRoleRepository.findByUserUsername("user")).thenReturn(dbResult);
		Set<RoleEntity> result = roleService.findRolesByUserUsername("user");
		assertThat(result.size()).isEqualTo(1);
		assertThat(result.iterator().next()).isEqualTo(dbResult.iterator().next().getRole());
		verify(userRoleRepository).findByUserUsername("user");
	}

	@Test
	public void testRemoveRoleFromUser() {
		when(userRoleRepository.findByUserUsernameAndRoleCode(anyString(), anyString()))
			.thenReturn(Optional.of(new UserRoleEntity()));
		when(userRepository.findByUsername(anyString()))
			.thenReturn(Optional.of(new UserEntity()));
		roleService.removeRoleFromUser("", "");
		verify(userRoleRepository).delete(any(UserRoleEntity.class));
		verify(userRepository).findByUsername(anyString());
	}

	@Test
	@SuppressWarnings("serial")
	public void testSaveRole() {
		RoleEntity input = new RoleEntity() {{ setId("123"); }}.setCode("user");
		RoleEntity result = new RoleEntity().setCode(UUID.randomUUID().toString());
		when(roleRepository.save(eq(input))).thenReturn(result);
		assertThat(roleService.saveRole(input)).isEqualTo(result);
		assertThat(input.getId()).isNull();
		verify(roleRepository).save(eq(input));
	}

	@Test
	@SuppressWarnings("serial")
	public void testUpdateRole() {
		RoleEntity input = new RoleEntity() {{ setId("123"); }}.setCode("user");
		RoleEntity result = new RoleEntity() {{ setId("123"); }}.setCode(UUID.randomUUID().toString());
		when(roleRepository.findOne(anyString())).thenReturn(null);
		when(roleRepository.findByCode(eq("123"))).thenReturn(Optional.of(result));
		when(roleRepository.save(eq(input))).thenReturn(input);
		assertThat(roleService.updateRole("123", input)).isEqualTo(result);
		assertThat(result.getId()).isEqualTo(input.getId());
		assertThat(result.getCode()).isEqualTo(input.getCode());
		verify(roleRepository).findByCode(eq("123"));
	}
}
