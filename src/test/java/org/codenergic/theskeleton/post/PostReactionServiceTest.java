/*
 * Copyright 2018 original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.codenergic.theskeleton.post;

import org.assertj.core.api.Assertions;
import org.codenergic.theskeleton.post.impl.PostReactionServiceImpl;
import org.codenergic.theskeleton.user.UserEntity;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.codenergic.theskeleton.post.PostReactionType.DISLIKE;
import static org.codenergic.theskeleton.post.PostReactionType.LIKE;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class PostReactionServiceTest {
	private static final String POST_ID = "1234";
	private static final String USER_ID = "123";

	@Mock
	private PostReactionRepository postReactionRepository;
	private PostReactionService postReactionService;

	@Before
	public void init() {
		MockitoAnnotations.initMocks(this);
		postReactionService = new PostReactionServiceImpl(postReactionRepository);
	}

	@Test
	public void testDeletePostReaction() throws Exception {
		final PostReactionEntity postReaction = new PostReactionEntity();
		postReaction.setId("123456");
		when(postReactionRepository.findByUserIdAndPostIdAndReactionType(USER_ID, POST_ID, LIKE))
			.thenReturn(postReaction);
		postReactionService.deletePostReaction(USER_ID, POST_ID, LIKE);
		verify(postReactionRepository).findByUserIdAndPostIdAndReactionType(USER_ID, POST_ID, LIKE);
		verify(postReactionRepository).delete(postReaction);
	}

	@Test
	public void testFindUserByPostReaction() throws Exception {
		List<PostReactionEntity> reactions = Collections.singletonList(new PostReactionEntity().setUser(new UserEntity().setId(USER_ID)));
		when(postReactionRepository.findByPostIdAndReactionType(eq(POST_ID), eq(DISLIKE), any()))
			.thenReturn(new PageImpl<>(reactions));
		Page<UserEntity> users = postReactionService.findUserByPostReaction(POST_ID, DISLIKE, null);
		assertThat(users).hasSize(1);
		assertThat(users).first().hasFieldOrPropertyWithValue("id", USER_ID);
		verify(postReactionRepository).findByPostIdAndReactionType(eq(POST_ID), eq(DISLIKE), any());
	}

	@Test
	public void testGetNumberOfPostReactions() throws Exception {
		when(postReactionRepository.countByPostIdAndReactionType(POST_ID, LIKE)).thenReturn(10L);
		assertThat(postReactionService.getNumberOfPostReactions(POST_ID, LIKE)).isEqualTo(10L);
		verify(postReactionRepository).countByPostIdAndReactionType(POST_ID, LIKE);
	}

	@Test
	public void testReactToPost() throws Exception {
		when(postReactionRepository.save(any(PostReactionEntity.class))).then(invocation -> invocation.getArgument(0));
		PostReactionEntity postReaction = postReactionService.reactToPost(USER_ID, POST_ID, LIKE);
		assertThat(postReaction).isNotNull();
		assertThat(postReaction).hasFieldOrPropertyWithValue("user.id", USER_ID);
		assertThat(postReaction).hasFieldOrPropertyWithValue("post.id", POST_ID);
		assertThat(postReaction).hasFieldOrPropertyWithValue("reactionType", LIKE);
		verify(postReactionRepository).save(any(PostReactionEntity.class));
	}

}
