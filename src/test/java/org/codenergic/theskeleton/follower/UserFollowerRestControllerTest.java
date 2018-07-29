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

package org.codenergic.theskeleton.follower;

import java.util.Collections;

import org.codenergic.theskeleton.core.test.EnableRestDocs;
import org.codenergic.theskeleton.core.web.UserArgumentResolver;
import org.codenergic.theskeleton.user.UserEntity;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.method.annotation.AuthenticationPrincipalArgumentResolver;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@ContextConfiguration
@WebAppConfiguration
@EnableRestDocs
public class UserFollowerRestControllerTest {
	private static final String USER_ID = "12345";
	private static final String USERNAME = "user12345";

	@Autowired
	private RestDocumentationContextProvider restDocumentation;
	@MockBean
	private UserDetailsService userDetailsService;
	@MockBean
	private UserFollowerService userFollowerService;
	private ObjectMapper objectMapper = new ObjectMapper();
	private MockMvc mockMvc;

	@Before
	public void init() {
		when(userDetailsService.loadUserByUsername(USERNAME)).thenReturn(new UserEntity().setId(USER_ID).setUsername(USERNAME));
		mockMvc = MockMvcBuilders
			.standaloneSetup(new UserFollowerRestController(userFollowerService))
			.setCustomArgumentResolvers(new UserArgumentResolver(userDetailsService), new AuthenticationPrincipalArgumentResolver(),
				new PageableHandlerMethodArgumentResolver())
			.apply(documentationConfiguration(restDocumentation))
			.build();
		Authentication authentication = new UsernamePasswordAuthenticationToken(
			new UserEntity().setId(USER_ID).setUsername(USERNAME), "1234");
		SecurityContextHolder.getContext().setAuthentication(authentication);
	}

	@Test
	public void testFindUserFollowers() throws Exception {
		when(userFollowerService.findUserFollowers(eq(USER_ID), any()))
			.thenReturn(new PageImpl<>(Collections.singletonList(new UserEntity()
				.setId(USERNAME).setUsername(USER_ID).setPictureUrl(USERNAME))));
		MockHttpServletResponse response = mockMvc.perform(get("/api/users/" + USERNAME + "/followers")
			.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andReturn()
			.getResponse();
		assertThat(response.getStatus()).isEqualTo(200);
		Page<UserFollowerRestData> result = new PageImpl<>(Collections
			.singletonList(UserFollowerRestData.builder()
				.followerUsername(USER_ID).followerPictureUrl(USERNAME).build()));
		assertThat(response.getContentAsString()).isEqualTo(objectMapper.writeValueAsString(result));
		verify(userFollowerService).findUserFollowers(eq(USER_ID), any());
	}

	@Test
	public void testFindUserFollowings() throws Exception {
		when(userFollowerService.findUserFollowings(eq(USER_ID), any()))
			.thenReturn(new PageImpl<>(Collections.singletonList(new UserEntity()
				.setId(USERNAME).setUsername(USER_ID).setPictureUrl(USERNAME))));
		MockHttpServletResponse response = mockMvc.perform(get("/api/users/" + USERNAME + "/followings")
			.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andReturn()
			.getResponse();
		assertThat(response.getStatus()).isEqualTo(200);
		Page<UserFollowerRestData> result = new PageImpl<>(Collections
			.singletonList(UserFollowerRestData.builder()
				.followingUsername(USER_ID).followingPictureUrl(USERNAME).build()));
		assertThat(response.getContentAsString()).isEqualTo(objectMapper.writeValueAsString(result));
		verify(userFollowerService).findUserFollowings(eq(USER_ID), any());
	}

	@Test
	public void testFollowUser() throws Exception {
		when(userFollowerService.followUser(USER_ID, USER_ID))
			.thenReturn(new UserFollowerEntity()
				.setUser(new UserEntity().setUsername(USER_ID).setPictureUrl(USERNAME))
				.setFollower(new UserEntity().setUsername(USERNAME).setPictureUrl(USER_ID)));
		MockHttpServletResponse response = mockMvc.perform(put("/api/users/" + USERNAME + "/followers")
			.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andReturn()
			.getResponse();
		assertThat(response.getStatus()).isEqualTo(200);
		UserFollowerRestData result = UserFollowerRestData.builder()
			.followingUsername(USER_ID).followingPictureUrl(USERNAME)
			.followerUsername(USERNAME).followerPictureUrl(USER_ID).build();
		assertThat(response.getContentAsString()).isEqualTo(objectMapper.writeValueAsString(result));
		verify(userFollowerService).followUser(USER_ID, USER_ID);
	}

	@Test
	public void testGetNumberOfFollowers() throws Exception {
		when(userFollowerService.getNumberOfFollowers(USER_ID)).thenReturn(10L);
		MockHttpServletResponse response = mockMvc.perform(get("/api/users/" + USERNAME + "/followers")
			.param("totals", "true")
			.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andReturn()
			.getResponse();
		assertThat(response.getStatus()).isEqualTo(200);
		assertThat(response.getContentAsString()).isEqualTo("10");
		verify(userFollowerService).getNumberOfFollowers(USER_ID);
	}

	@Test
	public void testGetNumberOfFollowings() throws Exception {
		when(userFollowerService.getNumberOfFollowings(USER_ID)).thenReturn(10L);
		MockHttpServletResponse response = mockMvc.perform(get("/api/users/" + USERNAME + "/followings")
			.param("totals", "true")
			.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andReturn()
			.getResponse();
		assertThat(response.getStatus()).isEqualTo(200);
		assertThat(response.getContentAsString()).isEqualTo("10");
		verify(userFollowerService).getNumberOfFollowings(USER_ID);
	}

	@Test
	public void testUnfollowUser() throws Exception {
		MockHttpServletResponse response = mockMvc.perform(delete("/api/users/" + USERNAME + "/followers")
			.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andReturn()
			.getResponse();
		verify(userFollowerService).unfollowUser(USER_ID, USER_ID);
	}
}
