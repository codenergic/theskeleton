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

import org.codenergic.theskeleton.user.UserEntity;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class PostServiceTest {
	static final PostEntity DUMMY_POST = new PostEntity()
		.setId("123")
		.setTitle("It's a disastah")
		.setContent("some text are <b>bold</b>,<i>italic</i> or <u>underline</u>")
		.setResponse(false)
		.setPoster(new UserEntity());
	static final PostEntity DUMMY_POST2 = new PostEntity()
		.setId("12345")
		.setTitle("Minas Tirith")
		.setContent("Pippin looked out from the shelter of Gandalf\"s cloak. He wondered if he was awake")
		.setSlug("testing");

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
		postService.deletePost("123");
		verify(postRepository).delete("123");
	}

	@Test
	public void testFindPostById() {
		when(postRepository.findOne("123")).thenReturn(DUMMY_POST2);
		assertThat(postService.findPostById("123")).isEqualTo(DUMMY_POST2);
		verify(postRepository).findOne("123");
	}

	@Test
	public void testFindPostByPoster() {
		Page<PostEntity> dbResult = new PageImpl<>(Collections.singletonList(DUMMY_POST));
		when(postRepository.findByPosterUsername(eq("user"), any())).thenReturn(dbResult);
		assertThat(postService.findPostByPoster("user", null)).isEqualTo(dbResult);
		verify(postRepository).findByPosterUsername(eq("user"), any());
	}

	@Test
	public void testFindPostByTitleContaining() {
		Page<PostEntity> dbResult = new PageImpl<>(Collections.singletonList(DUMMY_POST));
		when(postRepository.findByTitleContaining(eq("disastah"), any()))
			.thenReturn(dbResult);
		Page<PostEntity> result = postService.findPostByTitleContaining("disastah", null);
		assertThat(result.getNumberOfElements()).isEqualTo(1);
		assertThat(result).isEqualTo(dbResult);
		verify(postRepository).findByTitleContaining(eq("disastah"), any());
	}

	@Test
	public void testPublishAndUnPublishPost() {
		when(postRepository.findOne("1234")).thenReturn(null);
		assertThatThrownBy(() -> postService.publishPost("1234")).isInstanceOf(IllegalArgumentException.class);
		verify(postRepository).findOne("1234");
		when(postRepository.findOne("123")).thenReturn(new PostEntity().setPostStatus(PostEntity.Status.DRAFT));
		assertThat(postService.publishPost("123").getPostStatus()).isEqualTo(PostEntity.Status.PUBLISHED);
		verify(postRepository).findOne("123");
		when(postRepository.findOne("321")).thenReturn(new PostEntity().setPostStatus(PostEntity.Status.PUBLISHED));
		assertThat(postService.unPublishPost("321").getPostStatus()).isEqualTo(PostEntity.Status.DRAFT);
		verify(postRepository).findOne("321");
	}

	@Test
	public void testReplyPost() {
		when(postRepository.findOne("123")).thenReturn(DUMMY_POST);
		when(postRepository.save(any(PostEntity.class))).then(invocation -> invocation.getArgument(0));
		PostEntity reply = postService.replyPost("123", DUMMY_POST2);
		assertThat(reply.isResponse()).isTrue();
		assertThat(reply.getResponseTo()).isEqualTo(DUMMY_POST);
		assertThat(reply.getSlug()).isNotEmpty();
		verify(postRepository).findOne("123");
		verify(postRepository).save(any(PostEntity.class));
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
