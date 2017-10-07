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

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class PostServiceTest {
	static final PostEntity DUMMY_POST = new PostEntity()
		.setId("123")
		.setTitle("It's a disastah")
		.setContent("some text are <b>bold</b>,<i>italic</i> or <u>underline</u>");
	static final PostEntity DUMMY_POST2 = new PostEntity()
		.setId("12345")
		.setTitle("Minas Tirith")
		.setContent("Pippin looked out from the shelter of Gandalf\"s cloak. He wondered if he was awake");

	@Mock
	private PostRepository postRepository;
	private PostService postService;

	@Before
	public void init() {
		MockitoAnnotations.initMocks(this);
		this.postService = PostService.newInstance(postRepository);
	}

	@Test
	public void testDeletePost() {
		when(postRepository.findOne("123")).thenReturn(DUMMY_POST);
		postService.deletePost("123");
		verify(postRepository).delete(DUMMY_POST);
	}

	@Test
	public void testFindPostById() {
		when(postRepository.findOne("123")).thenReturn(DUMMY_POST2);
		assertThat(postService.findPostById("123")).isEqualTo(DUMMY_POST2);
		verify(postRepository).findOne("123");
	}

	@Test
	public void testFindPostByTitleContaining() {
		Page<PostEntity> dbResult = new PageImpl<>(Arrays.asList(DUMMY_POST));
		when(postRepository.findByTitleContaining(eq("disastah"), any()))
			.thenReturn(dbResult);
		Page<PostEntity> result = postService.findPostByTitleContaining("disastah", null);
		assertThat(result.getNumberOfElements()).isEqualTo(1);
		assertThat(result).isEqualTo(dbResult);
		verify(postRepository).findByTitleContaining(eq("disastah"), any());
	}

	@Test
	public void testSavePost() {
		when(postRepository.save(DUMMY_POST)).thenReturn(DUMMY_POST2);
		PostEntity savedPost = postService.savePost(DUMMY_POST);
		assertThat(savedPost).isEqualTo(DUMMY_POST2);
		verify(postRepository).save(DUMMY_POST);
	}

	@Test
	public void testUpdatePost() {
		when(postRepository.findOne(eq("123"))).thenReturn(DUMMY_POST2);
		assertThat(postService.updatePost("123", DUMMY_POST2)).isEqualTo(DUMMY_POST2);
		verify(postRepository).findOne(eq("123"));
	}

}
