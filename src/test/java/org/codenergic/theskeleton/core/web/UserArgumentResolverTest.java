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

package org.codenergic.theskeleton.core.web;

import org.codenergic.theskeleton.user.UserEntity;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class UserArgumentResolverTest {
	@Mock
	private UserDetailsService userDetailsService;
	private MockMvc mockMvc;

	@Before
	public void init() {
		MockitoAnnotations.initMocks(this);
		mockMvc = MockMvcBuilders
			.standaloneSetup(new UserArgumentResolverTestController())
			.setCustomArgumentResolvers(new UserArgumentResolver(userDetailsService))
			.build();
	}

	@Test
	public void testUserArgumentResolver1() throws Exception {
		when(userDetailsService.loadUserByUsername("username")).thenReturn(new UserEntity().setId("username2"));
		MockHttpServletResponse response = mockMvc.perform(get("/test1/username")
			.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andReturn()
			.getResponse();
		assertThat(response.getStatus()).isEqualTo(200);
		assertThat(response.getContentAsString()).contains("username212345");
		verify(userDetailsService).loadUserByUsername("username");
	}

	@Test
	public void testUserArgumentResolver2() throws Exception {
		when(userDetailsService.loadUserByUsername("username")).thenReturn(new UserEntity().setId("username2"));
		MockHttpServletResponse response = mockMvc.perform(get("/test2/username")
			.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andReturn()
			.getResponse();
		assertThat(response.getStatus()).isEqualTo(200);
		assertThat(response.getContentAsString()).contains("username212345");
		verify(userDetailsService).loadUserByUsername("username");
	}

	@Test
	public void testUserArgumentResolver3() throws Exception {
		when(userDetailsService.loadUserByUsername("username")).thenReturn(new UserEntity().setId("username2"));
		MockHttpServletResponse response = mockMvc.perform(get("/test3/username")
			.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andReturn()
			.getResponse();
		assertThat(response.getStatus()).isEqualTo(200);
		assertThat(response.getContentAsString()).contains("username212345");
		verify(userDetailsService).loadUserByUsername("username");
	}

	@Test
	public void testUserArgumentResolver4() throws Exception {
		MockHttpServletResponse response = mockMvc.perform(get("/test4/username")
			.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andReturn()
			.getResponse();
		assertThat(response.getStatus()).isEqualTo(200);
		assertThat(response.getContentAsString()).isEmpty();
		verify(userDetailsService, never()).loadUserByUsername(any());
	}

	@Test
	public void testUserArgumentResolver5() throws Exception {
		final MockHttpServletResponse response1 = mockMvc.perform(get("/test5/me")
			.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andReturn()
			.getResponse();
		assertThat(response1.getStatus()).isEqualTo(200);
		assertThat(response1.getContentAsString()).isEmpty();
		verify(userDetailsService, never()).loadUserByUsername(any());

		SecurityContextHolder.getContext()
			.setAuthentication(new TestingAuthenticationToken(new UserEntity().setId("1234"), ""));
		SecurityContextHolder.getContext().getAuthentication();
		final MockHttpServletResponse response2 = mockMvc.perform(get("/test5/me")
			.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andReturn()
			.getResponse();
		assertThat(response2.getStatus()).isEqualTo(200);
		assertThat(response2.getContentAsString()).contains("\"id\":\"1234\"");
		verify(userDetailsService, never()).loadUserByUsername(any());

		when(userDetailsService.loadUserByUsername("1234")).thenReturn(new UserEntity().setId("12345"));
		SecurityContextHolder.getContext()
			.setAuthentication(new TestingAuthenticationToken("1234", ""));
		SecurityContextHolder.getContext().getAuthentication();
		final MockHttpServletResponse response3 = mockMvc.perform(get("/test5/me")
			.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andReturn()
			.getResponse();
		assertThat(response3.getStatus()).isEqualTo(200);
		assertThat(response3.getContentAsString()).contains("\"id\":\"12345\"");
		verify(userDetailsService).loadUserByUsername("1234");
	}
}
