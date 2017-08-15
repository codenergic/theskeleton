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
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

public class PostServiceTest {

	@Mock
	private PostRepository postRepository;

	private PostService postService;

	public static final PostEntity DUMMY_POST = new PostEntity()
		.setId(UUID.randomUUID().toString())
		.setTitle("It's a disastah")
		.setContent("some text are <b>bold</b>,<i>italic</i> or <u>underline</u>");

	@Before
	public void init() {
		MockitoAnnotations.initMocks(this);
		this.postService = PostService.newInstance(postRepository);
	}

	@Test
	public void testSavePost() {
		postService.savePost(DUMMY_POST);
	}

	@Test
	public void testFindPostByTitleContaining() {
		Page<PostEntity> dbResult = new PageImpl<>(Arrays.asList(DUMMY_POST));
		when(postRepository.findByTitleContaining(eq("disastah"), any()))
			.thenReturn(dbResult);
		Page<PostEntity> result = postRepository.findByTitleContaining("disastah", null);
		assertThat(result.getNumberOfElements()).isEqualTo(1);
		assertThat(result).isEqualTo(dbResult);
		verify(postRepository).findByTitleContaining(eq("disastah"), any());
	}

}
