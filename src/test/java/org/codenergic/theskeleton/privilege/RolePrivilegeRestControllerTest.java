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

package org.codenergic.theskeleton.privilege;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import org.codenergic.theskeleton.core.test.EnableRestDocs;
import org.codenergic.theskeleton.core.test.InjectUserDetailsService;
import org.codenergic.theskeleton.role.RoleEntity;
import org.codenergic.theskeleton.role.RoleRestData;
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
@WebMvcTest(controllers = { RolePrivilegeRestController.class }, secure = false)
@EnableRestDocs
@InjectUserDetailsService
public class RolePrivilegeRestControllerTest {
	@Autowired
	private MockMvc mockMvc;
	@Autowired
	private ObjectMapper objectMapper;
	@MockBean
	private PrivilegeService privilegeService;

	@Test
	public void testAddPrivilegeToRole() throws Exception {
		final RoleEntity role = new RoleEntity()
			.setId("role123")
			.setCode("role123");
		when(privilegeService.addPrivilegeToRole("role123", "privilege123")).thenReturn(role);
		ResultActions resultActions = mockMvc.perform(put("/api/roles/role123/privileges")
			.content("{\"privilege\": \"privilege123\"}")
			.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andDo(document("role-privilege-create"));
		MockHttpServletResponse response = resultActions
			.andReturn()
			.getResponse();
		verify(privilegeService).addPrivilegeToRole("role123", "privilege123");
		assertThat(response.getContentAsByteArray())
			.isEqualTo(objectMapper.writeValueAsBytes(RoleRestData.builder(role).build()));
	}

	@Test
	public void testFindPrivilegesByRoleCode() throws Exception {
		final PrivilegeEntity privilege = new PrivilegeEntity()
			.setId("privilege123")
			.setName("user_list_read");
		final Set<PrivilegeEntity> privileges = new HashSet<>(Arrays.asList(privilege));
		final Set<PrivilegeRestData> expected = privileges.stream()
			.map(p -> PrivilegeRestData.builder(p).build())
			.collect(Collectors.toSet());
		when(privilegeService.findPrivilegesByRoleCode("role123")).thenReturn(privileges);
		ResultActions resultActions = mockMvc.perform(get("/api/roles/role123/privileges")
			.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andDo(document("role-privilege-read"));
		MockHttpServletResponse response = resultActions
			.andReturn()
			.getResponse();
		verify(privilegeService).findPrivilegesByRoleCode("role123");
		assertThat(response.getContentAsByteArray()).isEqualTo(objectMapper.writeValueAsBytes(expected));
	}

	@Test
	public void testRemovePrivilegeFromRole() throws Exception {
		final RoleEntity role = new RoleEntity()
			.setId("role123")
			.setCode("role123");
		when(privilegeService.removePrivilegeFromRole("role123", "privilege123")).thenReturn(role);
		ResultActions resultActions = mockMvc.perform(delete("/api/roles/role123/privileges")
			.content("{\"privilege\": \"privilege123\"}")
			.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andDo(document("role-privilege-delete"));
		MockHttpServletResponse response = resultActions
			.andReturn()
			.getResponse();
		verify(privilegeService).removePrivilegeFromRole("role123", "privilege123");
		assertThat(response.getContentAsByteArray())
			.isEqualTo(objectMapper.writeValueAsBytes(RoleRestData.builder(role).build()));
	}
}
