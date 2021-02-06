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

import java.util.Collections;
import java.util.Optional;

import org.codenergic.theskeleton.user.UserEntity;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class PostServiceTest {
	static final PostEntity DUMMY_POST = new PostEntity()
		.setId("123")
		.setContent("some text are <b>bold</b>,<i>italic</i> or <u>underline</u>")
		.setResponse(false)
		.setPoster(new UserEntity());
	static final PostEntity DUMMY_POST2 = new PostEntity()
		.setId("12345")
		.setContent("Pippin looked out from the shelter of Gandalf\"s cloak. He wondered if he was awake");

	@Mock
	private PostFollowingRepository postFollowingRepository;
	@Mock
	private PostRepository postRepository;
	private PostService postService;

	@Before
	public void init() {
		MockitoAnnotations.initMocks(this);
		this.postService = new PostServiceImpl(postRepository, postFollowingRepository) {};
	}

	@Test
	public void testDeletePost() {
		postService.deletePost("123");
		verify(postRepository).deleteById("123");
	}

	@Test
	public void testFindPostByContentContaining() {
		Page<PostEntity> dbResult = new PageImpl<>(Collections.singletonList(DUMMY_POST));
		when(postRepository.findByContentContaining(eq("disastah"), any()))
			.thenReturn(dbResult);
		Page<PostEntity> result = postService.findPostByContentContaining("disastah", null);
		assertThat(result.getNumberOfElements()).isEqualTo(1);
		assertThat(result).isEqualTo(dbResult);
		verify(postRepository).findByContentContaining(eq("disastah"), any());
	}

	@Test
	public void testFindPostByFollowerId() {
		Page<PostEntity> dbResult = new PageImpl<>(Collections.singletonList(DUMMY_POST));
		when(postFollowingRepository.findByFollowerId(eq("123"), any())).thenReturn(dbResult);
		assertThat(postService.findPostByFollowerId("123", null)).isEqualTo(dbResult);
		verify(postFollowingRepository).findByFollowerId(eq("123"), any());
	}

	@Test
	public void testFindPostById() {
		when(postRepository.findById("123")).thenReturn(Optional.of(DUMMY_POST2));
		assertThat(postService.findPostById("123")).isEqualTo(Optional.of(DUMMY_POST2));
		verify(postRepository).findById("123");
	}

	@Test
	public void testFindPostByPoster() {
		Page<PostEntity> dbResult = new PageImpl<>(Collections.singletonList(DUMMY_POST));
		when(postRepository.findByPosterId(eq("user"), any())).thenReturn(dbResult);
		assertThat(postService.findPostByPosterId("user", null)).isEqualTo(dbResult);
		verify(postRepository).findByPosterId(eq("user"), any());
	}

	@Test
	public void testFindPostReplies() {
		Page<PostEntity> dbResult = new PageImpl<>(Collections.singletonList(DUMMY_POST));
		when(postRepository.findByResponseToId(eq("123"), any())).thenReturn(dbResult);
		assertThat(postService.findPostReplies("123", null)).isEqualTo(dbResult);
		verify(postRepository).findByResponseToId(eq("123"), any());
	}

	@Test
	public void testReplyPost() {
		when(postRepository.findById("123")).thenReturn(Optional.of(DUMMY_POST));
		when(postRepository.save(any(PostEntity.class))).then(invocation -> invocation.getArgument(0));
		PostEntity reply = postService.replyPost("123", DUMMY_POST2);
		assertThat(reply.isResponse()).isTrue();
		assertThat(reply.getResponseTo()).isEqualTo(DUMMY_POST);
		verify(postRepository).findById("123");
		verify(postRepository).save(any(PostEntity.class));
	}

	@Test
	public void testSavePost() {
		when(postRepository.save(DUMMY_POST)).thenReturn(DUMMY_POST2);
		PostEntity savedPost = postService.savePost(new UserEntity(), DUMMY_POST);
		assertThat(savedPost).isEqualTo(DUMMY_POST2);
		verify(postRepository).save(DUMMY_POST);
	}

	@Test
	public void testUpdatePost() {
		when(postRepository.findById(eq("123"))).thenReturn(Optional.of(DUMMY_POST2));
		assertThat(postService.updatePost("123", DUMMY_POST2)).isEqualTo(DUMMY_POST2);
		verify(postRepository).findById(eq("123"));
	}
}
