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
package org.codenergic.theskeleton.post;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.codenergic.theskeleton.core.test.EnableRestDocs;
import org.codenergic.theskeleton.core.web.UserArgumentResolver;
import org.codenergic.theskeleton.user.UserEntity;
import org.codenergic.theskeleton.user.UserRestData;
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

import java.io.IOException;
import java.util.Collections;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@ContextConfiguration
@WebAppConfiguration
@EnableRestDocs
public class PostRestControllerTest {
	private static final String USER_ID = "user123";
	private static final String USERNAME = "username123";

	private MockMvc mockMvc;
	private ObjectMapper objectMapper = new ObjectMapper();
	@Autowired
	private RestDocumentationContextProvider restDocumentation;
	@MockBean
	private PostService postService;
	@MockBean
	private PostReactionService postReactionService;
	@MockBean
	private UserDetailsService userDetailsService;

	@Before
	public void init() {
		when(userDetailsService.loadUserByUsername(USERNAME)).thenReturn(new UserEntity().setId(USER_ID).setUsername(USERNAME));
		mockMvc = MockMvcBuilders
			.standaloneSetup(new PostRestController(postService, postReactionService))
			.setCustomArgumentResolvers(new UserArgumentResolver(userDetailsService), new AuthenticationPrincipalArgumentResolver(),
				new PageableHandlerMethodArgumentResolver())
			.apply(documentationConfiguration(restDocumentation))
			.build();
		Authentication authentication = new UsernamePasswordAuthenticationToken(
			new UserEntity().setId(USER_ID).setUsername(USERNAME), "1234");
		SecurityContextHolder.getContext().setAuthentication(authentication);
		PostServiceTest.DUMMY_POST.setCreatedDate(new Date());
		PostServiceTest.DUMMY_POST.setLastModifiedDate(new Date());
		PostServiceTest.DUMMY_POST2.setCreatedDate(new Date());
		PostServiceTest.DUMMY_POST2.setLastModifiedDate(new Date());
	}

	@Test
	public void testDeletePost() throws Exception {
		doAnswer(i -> null).when(postService).deletePost("123");
		MockHttpServletResponse response = mockMvc.perform(delete("/api/posts/123")
			.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andDo(document("post-delete"))
			.andReturn()
			.getResponse();
		assertThat(response.getStatus()).isEqualTo(200);
		verify(postService).deletePost("123");
	}

	@Test
	public void testDeleteReaction() throws Exception {
		MockHttpServletResponse response = mockMvc.perform(delete("/api/posts/1234/reactions")
			.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andDo(document("post-reactions-delete"))
			.andReturn()
			.getResponse();
		assertThat(response.getStatus()).isEqualTo(200);
		verify(postReactionService).deletePostReaction(USER_ID, "1234");

	}

	@Test
	public void testFindPostByFollower() throws Exception {
		final Page<PostEntity> post = new PageImpl<>(Collections.singletonList(PostServiceTest.DUMMY_POST));
		when(postService.findPostByFollowerId(eq(USER_ID), any())).thenReturn(post);
		MockHttpServletResponse response = mockMvc.perform(get("/api/posts/following")
			.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andDo(document("post-read-following"))
			.andReturn()
			.getResponse();
		assertThat(response.getContentAsByteArray())
			.isEqualTo(objectMapper.writeValueAsBytes(post.map(a -> PostRestData.builder(a).build())));
		verify(postService).findPostByFollowerId(eq(USER_ID), any());
	}

	@Test
	public void testFindPostById() throws Exception {
		when(postService.findPostById("123")).thenReturn(PostServiceTest.DUMMY_POST2);
		MockHttpServletResponse response = mockMvc.perform(get("/api/posts/123")
			.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andDo(document("post-read"))
			.andReturn()
			.getResponse();
		assertThat(response.getStatus()).isEqualTo(200);
		assertThat(response.getContentAsByteArray())
			.isEqualTo(objectMapper.writeValueAsBytes(PostRestData.builder(PostServiceTest.DUMMY_POST2).build()));
		verify(postService).findPostById("123");
	}

	@Test
	public void testFindPostByTitleContaining() throws Exception {
		final Page<PostEntity> post = new PageImpl<>(Collections.singletonList(PostServiceTest.DUMMY_POST));
		when(postService.findPostByTitleContaining(contains("disastah"), any())).thenReturn(post);
		MockHttpServletResponse response = mockMvc.perform(get("/api/posts?title=disastah")
			.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andDo(document("post-read-all"))
			.andReturn()
			.getResponse();
		assertThat(response.getContentAsByteArray())
			.isEqualTo(objectMapper.writeValueAsBytes(post.map(a -> PostRestData.builder(a).build())));
		verify(postService).findPostByTitleContaining(eq("disastah"), any());
	}

	@Test
	public void testFindPostReactions() throws Exception {
		when(postReactionService.findUserByPostReaction(eq("1234"), eq(PostReactionType.LIKE), any()))
			.thenReturn(new PageImpl<>(Collections
				.singletonList(new UserEntity().setUsername("user").setPictureUrl("1234"))));
		MockHttpServletResponse response = mockMvc.perform(get("/api/posts/1234/reactions/likes")
			.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andDo(document("post-reactions-read"))
			.andReturn()
			.getResponse();
		assertThat(response.getStatus()).isEqualTo(200);
		Page<UserRestData> expectedResponse = new PageImpl<>(Collections.singletonList(UserRestData.builder()
			.username("user")
			.pictureUrl("1234")
			.build()));
		assertThat(response.getContentAsString()).isEqualTo(objectMapper.writeValueAsString(expectedResponse));
		verify(postReactionService).findUserByPostReaction(eq("1234"), eq(PostReactionType.LIKE), any());
	}

	@Test
	public void testFindPostReplies() throws Exception {
		final Page<PostEntity> post = new PageImpl<>(Collections.singletonList(PostServiceTest.DUMMY_POST));
		when(postService.findPostReplies(contains("123"), any())).thenReturn(post);
		MockHttpServletResponse response = mockMvc.perform(get("/api/posts/123/responses")
			.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andDo(document("post-read-all-responses"))
			.andReturn()
			.getResponse();
		assertThat(response.getContentAsByteArray())
			.isEqualTo(objectMapper.writeValueAsBytes(post.map(a -> PostRestData.builder(a).build())));
		verify(postService).findPostReplies(eq("123"), any());
	}

	@Test
	public void testPublishAndUnPublishPost() throws Exception {
		when(postService.publishPost("123")).thenReturn(PostServiceTest.DUMMY_POST.setPostStatus(PostStatus.PUBLISHED));
		MockHttpServletResponse response = mockMvc.perform(put("/api/posts/123/publish")
			.content("true").contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andDo(document("post-publish"))
			.andReturn()
			.getResponse();
		PostRestData expectedResponse = PostRestData.builder(PostServiceTest.DUMMY_POST)
			.status(PostStatus.PUBLISHED.name())
			.build();
		assertThat(response.getStatus()).isEqualTo(200);
		assertThat(response.getContentAsByteArray()).isEqualTo(objectMapper.writeValueAsBytes(expectedResponse));
		verify(postService).publishPost("123");

		when(postService.unPublishPost("123")).thenReturn(PostServiceTest.DUMMY_POST.setPostStatus(PostStatus.DRAFT));
		response = mockMvc.perform(put("/api/posts/123/publish")
			.content("false").contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andDo(document("post-unpublish"))
			.andReturn()
			.getResponse();
		expectedResponse = PostRestData.builder(PostServiceTest.DUMMY_POST)
			.status(PostStatus.DRAFT.name())
			.build();
		assertThat(response.getStatus()).isEqualTo(200);
		assertThat(response.getContentAsByteArray()).isEqualTo(objectMapper.writeValueAsBytes(expectedResponse));
		verify(postService).unPublishPost("123");
	}

	@Test
	public void testReactToPost() throws Exception {
		MockHttpServletResponse response = mockMvc.perform(put("/api/posts/1234/reactions")
			.content("DISLIKE")
			.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andDo(document("post-react"))
			.andReturn()
			.getResponse();
		assertThat(response.getStatus()).isEqualTo(200);
		verify(postReactionService).reactToPost(anyString(), eq("1234"), eq(PostReactionType.DISLIKE));
	}

	@Test
	public void testReplyPost() throws Exception {
		when(postService.replyPost(eq("123"), any()))
			.thenReturn(PostServiceTest.DUMMY_POST2.setResponse(true).setResponseTo(PostServiceTest.DUMMY_POST));
		MockHttpServletResponse response = mockMvc.perform(post("/api/posts/123/responses")
			.content(objectMapper.writeValueAsBytes(PostRestData.builder(PostServiceTest.DUMMY_POST2).build()))
			.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andDo(document("post-reply"))
			.andReturn()
			.getResponse();
		PostRestData expectedResponse = PostRestData.builder(PostServiceTest.DUMMY_POST2).build();
		assertThat(response.getStatus()).isEqualTo(200);
		assertThat(response.getContentAsByteArray())
			.isEqualTo(objectMapper.writeValueAsBytes(expectedResponse));
		verify(postService).replyPost(eq("123"), any());
	}

	@Test
	public void testSavePost() throws Exception {
		when(postService.savePost(any(), any())).thenReturn(PostServiceTest.DUMMY_POST);
		MockHttpServletResponse response = mockMvc.perform(post("/api/posts")
			.content("{\"title\": \"It's a disastah\", \"content\": \"Seriously a disastah\"}")
			.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andDo(document("post-create"))
			.andReturn()
			.getResponse();
		assertThat(response.getStatus()).isEqualTo(200);
		assertThat(response.getContentAsByteArray())
			.isEqualTo(objectMapper.writeValueAsBytes(
				PostRestData.builder(PostServiceTest.DUMMY_POST).build()));
		verify(postService).savePost(any(), any());
	}

	@Test
	public void testSerializeDeserializePost() throws IOException {
		PostRestData post = PostRestData.builder()
			.id("123")
			.title("It's a disastah")
			.content("Seriously a disastah")
			.build();
		String json = objectMapper.writeValueAsString(post);
		PostRestData post2 = objectMapper.readValue(json, PostRestData.class);
		assertThat(post).isEqualTo(post2);
	}

	@Test
	public void testUpdatePost() throws Exception {
		byte[] jsonInput = objectMapper.writeValueAsBytes(
			PostRestData.builder(PostServiceTest.DUMMY_POST).build());
		when(postService.updatePost(eq("123"), any())).thenReturn(PostServiceTest.DUMMY_POST2);
		MockHttpServletResponse response = mockMvc.perform(put("/api/posts/123")
			.contentType(MediaType.APPLICATION_JSON)
			.content(jsonInput))
			.andExpect(status().isOk())
			.andDo(document("post-update"))
			.andReturn()
			.getResponse();
		verify(postService).updatePost(eq("123"), any());
		assertThat(response.getContentAsByteArray())
			.isEqualTo(objectMapper.writeValueAsBytes(
				PostRestData.builder(PostServiceTest.DUMMY_POST2).build()));
	}
}
