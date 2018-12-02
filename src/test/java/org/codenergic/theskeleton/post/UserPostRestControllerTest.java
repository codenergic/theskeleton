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
package org.codenergic.theskeleton.post;

import java.util.Collections;
import java.util.Date;

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
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@ContextConfiguration
@WebAppConfiguration
@EnableRestDocs
public class UserPostRestControllerTest {
	private static final String USER_ID = "id123";
	private static final String USERNAME = "username123";

	private MockMvc mockMvc;
	private ObjectMapper objectMapper = new ObjectMapper();
	@Autowired
	private RestDocumentationContextProvider restDocumentation;
	@MockBean
	private PostService postService;
	private final PostMapper postMapper = PostMapper.newInstance();

	@Before
	public void init() {
		mockMvc = MockMvcBuilders
			.standaloneSetup(new UserPostRestController(postService))
			.setCustomArgumentResolvers(new UserArgumentResolver(username -> new UserEntity().setUsername(username)),
				new AuthenticationPrincipalArgumentResolver(),
				new PageableHandlerMethodArgumentResolver())
			.apply(documentationConfiguration(restDocumentation))
			.build();
		Authentication authentication = new TestingAuthenticationToken(
			new UserEntity().setId(USER_ID).setUsername(USERNAME), "1234");
		SecurityContextHolder.getContext().setAuthentication(authentication);
	}

	@Test
	public void testFindUserPublishedPost() throws Exception {
		PostServiceTest.DUMMY_POST.setCreatedDate(new Date());
		PostServiceTest.DUMMY_POST.setLastModifiedDate(new Date());
		PageImpl<PostEntity> result = new PageImpl<>(Collections.singletonList(PostServiceTest.DUMMY_POST));
		when(postService.findPostByPosterId(eq(USER_ID), any())).thenReturn(result);
		MockHttpServletResponse response = mockMvc.perform(get("/api/users/me/posts")
			.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andDo(document("user-post-published"))
			.andReturn()
			.getResponse();
		assertThat(response.getStatus()).isEqualTo(200);
		Page<PostRestData> expectedResult = result.map(postMapper::toPostData);
		assertThat(response.getContentAsString()).isEqualTo(objectMapper.writeValueAsString(expectedResult));
		verify(postService).findPostByPosterId(eq(USER_ID), any());
	}
}
