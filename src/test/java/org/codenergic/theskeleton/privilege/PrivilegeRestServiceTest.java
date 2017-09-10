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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import java.util.Arrays;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

@RunWith(SpringRunner.class)
@EnableSpringDataWebSupport
@WebMvcTest(controllers = { PrivilegeRestService.class }, secure = false)
public class PrivilegeRestServiceTest {
	@Autowired
	private MockMvc mockMvc;
	@Autowired
	private ObjectMapper objectMapper;
	@MockBean
	private PrivilegeService privilegeService;

	@Test
	public void testFindPrivilegeByName() throws Exception {
		PrivilegeEntity dbResult = new PrivilegeEntity()
				.setId("123")
				.setName("12345")
				.setDescription("Description 12345");
		when(privilegeService.findPrivilegeByIdOrName("123")).thenReturn(dbResult);
		MockHttpServletResponse response = mockMvc.perform(get("/api/privileges/123"))
				.andReturn()
				.getResponse();
		verify(privilegeService).findPrivilegeByIdOrName("123");
		assertThat(response.getStatus()).isEqualTo(200);
		assertThat(response.getContentAsByteArray())
				.isEqualTo(objectMapper.writeValueAsBytes(PrivilegeRestData.builder(dbResult).build()));
	}

	@Test
	public void testFindPrivilegeByNameNotFound() throws Exception {
		when(privilegeService.findPrivilegeByIdOrName("123")).thenReturn(null);
		MockHttpServletResponse response = mockMvc.perform(get("/api/privileges/123"))
				.andReturn()
				.getResponse();
		verify(privilegeService).findPrivilegeByIdOrName("123");
		assertThat(response.getStatus()).isEqualTo(200);
		assertThat(response.getContentAsByteArray()).isEqualTo(new byte[0]);
	}

	@Test
	public void testFindPrivileges() throws Exception {
		PrivilegeEntity dbResult = new PrivilegeEntity()
				.setId("123")
				.setName("12345")
				.setDescription("Description 12345");
		Page<PrivilegeEntity> pageResponseBody = new PageImpl<>(Arrays.asList(dbResult));
		Page<PrivilegeRestData> expectedResponseBody = new PageImpl<>(Arrays.asList(PrivilegeRestData.builder(dbResult).build()));
		when(privilegeService.findPrivileges(anyString(), any())).thenReturn(pageResponseBody);
		MockHttpServletResponse response = mockMvc.perform(get("/api/privileges"))
				.andReturn()
				.getResponse();
		verify(privilegeService).findPrivileges(anyString(), any());
		assertThat(response.getStatus()).isEqualTo(200);
		assertThat(response.getContentAsByteArray()).isEqualTo(objectMapper.writeValueAsBytes(expectedResponseBody));
	}
}
