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
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.io.IOException;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@EnableSpringDataWebSupport
@WebMvcTest(controllers = {PostRestService.class}, secure = false)
@EnableRestDocs
public class PostRestServiceTest {
	@Autowired
	private MockMvc mockMvc;
	@Autowired
	private ObjectMapper objectMapper;
	@MockBean
	private PostService postService;

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
			.isEqualTo(objectMapper.writeValueAsBytes(PostRestData.builder()
				.fromPostEntity(PostServiceTest.DUMMY_POST2).build()));
		verify(postService).findPostById("123");
	}

	@Test
	public void testFindPostByPoster() throws Exception {
		final Page<PostEntity> post = new PageImpl<>(Collections.singletonList(PostServiceTest.DUMMY_POST));
		when(postService.findPostByPoster(contains("user"), any())).thenReturn(post);
		MockHttpServletResponse response = mockMvc.perform(get("/api/posts?username=user")
			.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andDo(document("post-read-all-by-user"))
			.andReturn()
			.getResponse();
		assertThat(response.getContentAsByteArray())
			.isEqualTo(objectMapper.writeValueAsBytes(post.map(a -> PostRestData.builder().fromPostEntity(a).build())));
		verify(postService).findPostByPoster(eq("user"), any());
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
			.isEqualTo(objectMapper.writeValueAsBytes(post.map(a -> PostRestData.builder().fromPostEntity(a).build())));
		verify(postService).findPostByTitleContaining(eq("disastah"), any());
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
			.isEqualTo(objectMapper.writeValueAsBytes(post.map(a -> PostRestData.builder().fromPostEntity(a).build())));
		verify(postService).findPostReplies(eq("123"), any());
	}

	@Test
	public void testPublishAndUnPublishPost() throws Exception {
		when(postService.publishPost("123")).thenReturn(PostServiceTest.DUMMY_POST.setPostStatus(PostEntity.Status.PUBLISHED));
		MockHttpServletResponse response = mockMvc.perform(put("/api/posts/123/publish")
			.content("true").contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andDo(document("post-publish"))
			.andReturn()
			.getResponse();
		PostRestData expectedResponse = PostRestData.builder()
			.fromPostEntity(PostServiceTest.DUMMY_POST)
			.status(PostEntity.Status.PUBLISHED.name())
			.build();
		assertThat(response.getStatus()).isEqualTo(200);
		assertThat(response.getContentAsByteArray()).isEqualTo(objectMapper.writeValueAsBytes(expectedResponse));
		verify(postService).publishPost("123");

		when(postService.unPublishPost("123")).thenReturn(PostServiceTest.DUMMY_POST.setPostStatus(PostEntity.Status.DRAFT));
		response = mockMvc.perform(put("/api/posts/123/publish")
			.content("false").contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andDo(document("post-unpublish"))
			.andReturn()
			.getResponse();
		expectedResponse = PostRestData.builder()
			.fromPostEntity(PostServiceTest.DUMMY_POST)
			.status(PostEntity.Status.DRAFT.name())
			.build();
		assertThat(response.getStatus()).isEqualTo(200);
		assertThat(response.getContentAsByteArray()).isEqualTo(objectMapper.writeValueAsBytes(expectedResponse));
		verify(postService).unPublishPost("123");
	}

	@Test
	public void testReplyPost() throws Exception {
		when(postService.replyPost(eq("123"), any()))
			.thenReturn(PostServiceTest.DUMMY_POST2.setResponse(true).setResponseTo(PostServiceTest.DUMMY_POST));
		MockHttpServletResponse response = mockMvc.perform(post("/api/posts/123/responses")
			.content(objectMapper.writeValueAsBytes(PostRestData.builder().fromPostEntity(PostServiceTest.DUMMY_POST2).build()))
			.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andDo(document("post-reply"))
			.andReturn()
			.getResponse();
		PostRestData expectedResponse = PostRestData.builder()
			.fromPostEntity(PostServiceTest.DUMMY_POST2)
			.build();
		assertThat(response.getStatus()).isEqualTo(200);
		assertThat(response.getContentAsByteArray())
			.isEqualTo(objectMapper.writeValueAsBytes(expectedResponse));
		verify(postService).replyPost(eq("123"), any());
	}

	@Test
	public void testSavePost() throws Exception {
		when(postService.savePost(any())).thenReturn(PostServiceTest.DUMMY_POST);
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
				PostRestData.builder().fromPostEntity(PostServiceTest.DUMMY_POST).build()));
		verify(postService).savePost(any());
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
			PostRestData.builder().fromPostEntity(PostServiceTest.DUMMY_POST).build());
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
				PostRestData.builder().fromPostEntity(PostServiceTest.DUMMY_POST2).build()));
	}
}
