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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.codenergic.theskeleton.privilege.PrivilegeEntity;
import org.codenergic.theskeleton.privilege.PrivilegeRepository;
import org.codenergic.theskeleton.role.RoleEntity;
import org.codenergic.theskeleton.role.RolePrivilegeEntity;
import org.codenergic.theskeleton.role.RolePrivilegeRepository;
import org.codenergic.theskeleton.role.RoleRepository;
import org.codenergic.theskeleton.role.RoleService;
import org.codenergic.theskeleton.role.impl.RoleServiceImpl;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

public class RoleServiceTest {
	private RoleService roleService;
	@Mock
	private RoleRepository roleRepository;
	@Mock
	private PrivilegeRepository privilegeRepository;
	@Mock
	private RolePrivilegeRepository rolePrivilegeRepository;

	@Before
	public void init() {
		MockitoAnnotations.initMocks(this);
		this.roleService = new RoleServiceImpl(roleRepository, privilegeRepository, rolePrivilegeRepository);
	}

	@Test
	public void testFindRoleByCode() {
		RoleEntity result = new RoleEntity();
		result.setCode("user");
		when(roleRepository.findByCode(eq("user"))).thenReturn(result);
		assertThat(roleService.findRoleByCode("user")).isEqualTo(result);
		verify(roleRepository).findByCode(eq("user"));
		when(roleRepository.findByCode(eq("admin"))).thenReturn(null);
		assertThat(roleService.findRoleByCode("admin")).isNull();
		verify(roleRepository).findByCode(eq("admin"));
	}

	@Test
	@SuppressWarnings("serial")
	public void testFindRoleById() {
		RoleEntity result = new RoleEntity() {{ setId("123"); }}.setCode("user");
		when(roleRepository.findOne(eq("123"))).thenReturn(result);
		assertThat(roleService.findRoleById("123")).isEqualTo(result);
		verify(roleRepository).findOne(eq("123"));
		when(roleRepository.findOne(eq("124"))).thenReturn(null);
		assertThat(roleService.findRoleById("124")).isNull();
		verify(roleRepository).findOne(eq("124"));
	}

	@Test
	@SuppressWarnings("serial")
	public void testFindRoles() {
		RoleEntity result = new RoleEntity() {{ setId("123"); }}.setCode("user");
		Page<RoleEntity> page = new PageImpl<>(Arrays.asList(result));
		when(roleRepository.findAll(any(Pageable.class))).thenReturn(page);
		assertThat(roleService.findRoles(new PageRequest(1, 10))).isEqualTo(page);
		verify(roleRepository).findAll(any(Pageable.class));
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
		when(roleRepository.findByCode(eq("123"))).thenReturn(result);
		when(roleRepository.save(eq(input))).thenReturn(input);
		assertThat(roleService.updateRole("123", input)).isEqualTo(result);
		assertThat(result.getId()).isEqualTo(input.getId());
		assertThat(result.getCode()).isEqualTo(input.getCode());
		verify(roleRepository).findByCode(eq("123"));
	}

	@Test
	@SuppressWarnings("serial")
	public void testDeleteRole() {
		RoleEntity input = new RoleEntity() {{ setId("123"); }}.setCode("user");
		when(roleRepository.findByCode("123")).thenReturn(input);
		roleService.deleteRole("123");
		verify(roleRepository).findByCode("123");
		verify(roleRepository).delete(input);
	}

	@Test
	public void testAddPrivilegeToRole() {
		RoleEntity role = new RoleEntity()
				.setId(UUID.randomUUID().toString())
				.setCode("role");
		PrivilegeEntity privilege = new PrivilegeEntity()
				.setId(UUID.randomUUID().toString())
				.setName("privilege");
		RolePrivilegeEntity result = new RolePrivilegeEntity(role, privilege);
		result.setId(UUID.randomUUID().toString());
		when(roleRepository.findByCode("role")).thenReturn(role);
		when(privilegeRepository.findByName("privilege")).thenReturn(privilege);
		when(rolePrivilegeRepository.save(any(RolePrivilegeEntity.class))).thenReturn(result);
		assertThat(roleService.addPrivilegeToRole("role", "privilege")).isEqualTo(role);
		verify(roleRepository).findByCode("role");
		verify(privilegeRepository).findByName("privilege");
		verify(rolePrivilegeRepository).save(any(RolePrivilegeEntity.class));
	}

	@Test
	public void testRemovePrivilegeFromRole() {
		roleService.removePrivilegeFromRole("", "");
	}

	@Test
	public void testFindPrivilegesByRoleCode() {
		Set<RolePrivilegeEntity> dbResult =
				new HashSet<>(Arrays.asList(new RolePrivilegeEntity().setPrivilege(new PrivilegeEntity().setName("privilege"))));
		when(rolePrivilegeRepository.findByRoleCode("role")).thenReturn(dbResult);
		Set<PrivilegeEntity> result = roleService.findPrivilegesByRoleCode("role");
		assertThat(result.size()).isEqualTo(1);
		assertThat(result.iterator().next()).isEqualTo(dbResult.iterator().next().getPrivilege());
		verify(rolePrivilegeRepository).findByRoleCode("role");
	}

	@Test
	public void testFindRolesByPrivilegeName() {
		Set<RolePrivilegeEntity> dbResult =
				new HashSet<>(Arrays.asList(new RolePrivilegeEntity().setRole(new RoleEntity().setCode("role"))));
		when(rolePrivilegeRepository.findByPrivilegeName("privilege")).thenReturn(dbResult);
		Set<RoleEntity> result = roleService.findRolesByPrivilegeName("privilege");
		assertThat(result.size()).isEqualTo(1);
		assertThat(result.iterator().next()).isEqualTo(dbResult.iterator().next().getRole());
		verify(rolePrivilegeRepository).findByPrivilegeName("privilege");
	}
}
