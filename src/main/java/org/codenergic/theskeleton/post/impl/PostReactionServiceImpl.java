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
package org.codenergic.theskeleton.post.impl;

import org.codenergic.theskeleton.post.PostEntity;
import org.codenergic.theskeleton.post.PostReactionEntity;
import org.codenergic.theskeleton.post.PostReactionRepository;
import org.codenergic.theskeleton.post.PostReactionService;
import org.codenergic.theskeleton.post.PostReactionType;
import org.codenergic.theskeleton.user.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class PostReactionServiceImpl implements PostReactionService {
	private final PostReactionRepository postReactionRepository;

	public PostReactionServiceImpl(PostReactionRepository postReactionRepository) {
		this.postReactionRepository = postReactionRepository;
	}

	@Override
	@Transactional
	public void deletePostReaction(String userId, String postId) {
		PostReactionEntity postReaction = postReactionRepository.findByUserIdAndPostId(userId, postId)
			.orElseThrow(() -> new IllegalArgumentException("Cannot find reaction"));
		postReactionRepository.delete(postReaction);
	}

	@Override
	public Page<UserEntity> findUserByPostReaction(String postId, PostReactionType reactionType, Pageable pageable) {
		return postReactionRepository.findByPostIdAndReactionType(postId, reactionType, pageable).map(PostReactionEntity::getUser);
	}

	@Override
	public long getNumberOfPostReactions(String postId, PostReactionType reactionType) {
		return postReactionRepository.countByPostIdAndReactionType(postId, reactionType);
	}

	@Override
	@Transactional
	public PostReactionEntity reactToPost(String userId, String postId, PostReactionType reactionType) {
		PostReactionEntity postReaction = new PostReactionEntity()
			.setUser(new UserEntity().setId(userId))
			.setPost(new PostEntity().setId(postId))
			.setReactionType(reactionType);
		return postReactionRepository.save(postReaction);
	}
}
