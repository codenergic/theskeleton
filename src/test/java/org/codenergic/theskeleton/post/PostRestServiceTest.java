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
import org.springframework.test.web.servlet.ResultActions;

import java.io.IOException;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
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
	public void testFindPostByTitleContaining() throws Exception {
		final Page<PostEntity> post = new PageImpl<>(Arrays.asList(PostServiceTest.DUMMY_POST));
		when(postService.findPostByTitleContaining(contains("disastah"), any())).thenReturn(post);
		ResultActions resultActions = mockMvc.perform(get("/api/posts?title=disastah")
			.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andDo(document("post-read-all"));
		MockHttpServletResponse response = resultActions
			.andReturn()
			.getResponse();
		assertThat(response.getContentAsByteArray())
			.isEqualTo(objectMapper.writeValueAsBytes(post.map(a -> PostRestData.builder().fromPostEntity(a).build())));
		verify(postService).findPostByTitleContaining(eq("disastah"), any());
	}

	@Test
	public void testSavePost() throws Exception {
		when(postService.savePost(any())).thenReturn(PostServiceTest.DUMMY_POST);
		ResultActions resultActions = mockMvc.perform(post("/api/posts")
			.content("{\"title\": \"It's a disastah\", \"content\": \"Seriously a disastah\"}")
			.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andDo(document("post-create"));
		MockHttpServletResponse response = resultActions
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
		ResultActions resultActions = mockMvc.perform(put("/api/posts/123")
			.contentType(MediaType.APPLICATION_JSON)
			.content(jsonInput))
			.andExpect(status().isOk())
			.andDo(document("post-update"));
		MockHttpServletResponse response = resultActions
			.andReturn()
			.getResponse();
		verify(postService).updatePost(eq("123"), any());
		assertThat(response.getContentAsByteArray())
			.isEqualTo(objectMapper.writeValueAsBytes(
				PostRestData.builder().fromPostEntity(PostServiceTest.DUMMY_POST2).build()));
	}
}
