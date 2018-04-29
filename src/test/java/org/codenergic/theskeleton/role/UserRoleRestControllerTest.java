/*
 * Copyright 2018 original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.codenergic.theskeleton.role;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import org.codenergic.theskeleton.core.test.EnableRestDocs;
import org.codenergic.theskeleton.core.test.InjectUserDetailsService;
import org.codenergic.theskeleton.user.UserEntity;
import org.codenergic.theskeleton.user.UserRestData;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import com.fasterxml.jackson.databind.ObjectMapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@EnableSpringDataWebSupport
@WebMvcTest(controllers = {UserRoleRestController.class}, secure = false)
@EnableRestDocs
@InjectUserDetailsService
public class UserRoleRestControllerTest {
	@Autowired
	private MockMvc mockMvc;
	@Autowired
	private ObjectMapper objectMapper;
	@MockBean
	private RoleService roleService;

	@Test
	public void testAddRoleToUser() throws Exception {
		final UserEntity user = new UserEntity()
			.setId("123")
			.setUsername("user123")
			.setPassword("123456");
		when(roleService.addRoleToUser("user123", "role123")).thenReturn(user);
		ResultActions resultActions = mockMvc.perform(put("/api/users/user123/roles")
			.content("{\"role\": \"role123\"}")
			.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andDo(document("user-role-create"));
		MockHttpServletResponse response = resultActions
			.andReturn()
			.getResponse();
		assertThat(response.getContentAsByteArray())
			.isEqualTo(objectMapper.writeValueAsBytes(UserRestData.builder(user).build()));
		verify(roleService).addRoleToUser("user123", "role123");
	}

	@Test
	public void testFindRolesByUserUsername() throws Exception {
		final RoleEntity role = new RoleEntity()
			.setId("role123")
			.setCode("role123");
		final Set<RoleEntity> roles = new HashSet<>(Arrays.asList(role));
		final Set<RoleRestData> expected = roles.stream()
			.map(r -> RoleRestData.builder(r).build())
			.collect(Collectors.toSet());
		when(roleService.findRolesByUserUsername("user123")).thenReturn(roles);
		ResultActions resultActions = mockMvc.perform(get("/api/users/user123/roles")
			.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andDo(document("user-role-read"));
		MockHttpServletResponse response = resultActions
			.andReturn()
			.getResponse();
		assertThat(response.getContentAsByteArray()).isEqualTo(objectMapper.writeValueAsBytes(expected));
		verify(roleService).findRolesByUserUsername("user123");
	}

	@Test
	public void testRemoveRoleFromUser() throws Exception {
		final UserEntity user = new UserEntity()
			.setId("user123");
		when(roleService.removeRoleFromUser("user123", "role123")).thenReturn(user);
		ResultActions resultActions = mockMvc.perform(delete("/api/users/user123/roles")
			.content("{\"role\": \"role123\"}")
			.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andDo(document("user-role-delete"));
		MockHttpServletResponse response = resultActions
			.andReturn()
			.getResponse();
		assertThat(response.getContentAsByteArray())
			.isEqualTo(objectMapper.writeValueAsBytes(UserRestData.builder(user).build()));
		verify(roleService).removeRoleFromUser("user123", "role123");
	}
}
