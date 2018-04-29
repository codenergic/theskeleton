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

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.codenergic.theskeleton.privilege.impl.PrivilegeServiceImpl;
import org.codenergic.theskeleton.role.RoleEntity;
import org.codenergic.theskeleton.role.RoleRepository;
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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class PrivilegeServiceTest {
	private PrivilegeService privilegeService;
	@Mock
	private PrivilegeRepository privilegeRepository;
	@Mock
	private RoleRepository roleRepository;
	@Mock
	private RolePrivilegeRepository rolePrivilegeRepository;

	@Before
	public void init() {
		MockitoAnnotations.initMocks(this);
		this.privilegeService = new PrivilegeServiceImpl(privilegeRepository, roleRepository, rolePrivilegeRepository);
	}

	@Test
	public void testFindPrivilegeByName() {
		PrivilegeEntity result = new PrivilegeEntity();
		result.setName("user");
		when(privilegeRepository.findByName(eq("user"))).thenReturn(result);
		assertThat(privilegeService.findPrivilegeByName("user")).isEqualTo(result);
		verify(privilegeRepository).findByName(eq("user"));
		when(privilegeRepository.findByName(eq("admin"))).thenReturn(null);
		assertThat(privilegeService.findPrivilegeByName("admin")).isNull();
		verify(privilegeRepository).findByName(eq("admin"));
	}

	@Test
	@SuppressWarnings("serial")
	public void testFindPrivilegeById() {
		PrivilegeEntity result = new PrivilegeEntity() {{ setId("123"); }}.setName("user_list_read");
		when(privilegeRepository.findOne(eq("123"))).thenReturn(result);
		assertThat(privilegeService.findPrivilegeById("123")).isEqualTo(result);
		verify(privilegeRepository).findOne(eq("123"));
		when(privilegeRepository.findOne(eq("124"))).thenReturn(null);
		assertThat(privilegeService.findPrivilegeById("124")).isNull();
		verify(privilegeRepository).findOne(eq("124"));
	}

	@Test
	@SuppressWarnings("serial")
	public void testFindPrivileges() {
		PrivilegeEntity result = new PrivilegeEntity() {{ setId("123"); }}.setName("user_list_read");
		Page<PrivilegeEntity> page = new PageImpl<>(Arrays.asList(result));
		when(privilegeRepository.findAll(any(Pageable.class))).thenReturn(page);
		assertThat(privilegeService.findPrivileges(new PageRequest(1, 10))).isEqualTo(page);
		verify(privilegeRepository).findAll(any(Pageable.class));
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
		assertThat(privilegeService.addPrivilegeToRole("role", "privilege")).isEqualTo(role);
		verify(roleRepository).findByCode("role");
		verify(privilegeRepository).findByName("privilege");
		verify(rolePrivilegeRepository).save(any(RolePrivilegeEntity.class));
	}

	@Test
	public void testRemovePrivilegeFromRole() {
		privilegeService.removePrivilegeFromRole("", "");
	}

	@Test
	public void testFindPrivilegesByRoleCode() {
		Set<RolePrivilegeEntity> dbResult =
			new HashSet<>(Arrays.asList(new RolePrivilegeEntity().setPrivilege(new PrivilegeEntity().setName("privilege"))));
		when(rolePrivilegeRepository.findByRoleCode("role")).thenReturn(dbResult);
		Set<PrivilegeEntity> result = privilegeService.findPrivilegesByRoleCode("role");
		assertThat(result.size()).isEqualTo(1);
		assertThat(result.iterator().next()).isEqualTo(dbResult.iterator().next().getPrivilege());
		verify(rolePrivilegeRepository).findByRoleCode("role");
	}
}
