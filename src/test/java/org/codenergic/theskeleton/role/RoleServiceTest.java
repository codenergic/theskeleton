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
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.UUID;

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

	@Before
	public void init() {
		MockitoAnnotations.initMocks(this);
		this.roleService = new RoleServiceImpl(roleRepository);
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
		when(roleRepository.findByCode(eq("123"))).thenReturn(result);
		when(roleRepository.save(eq(input))).thenReturn(input);
		assertThat(roleService.updateRole("123", input)).isEqualTo(result);
		assertThat(result.getId()).isEqualTo(input.getId());
		assertThat(result.getCode()).isEqualTo(input.getCode());
		verify(roleRepository).findByCode(eq("123"));
	}
}
