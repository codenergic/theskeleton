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

import java.util.Optional;

import org.codenergic.theskeleton.user.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
class PostServiceImpl implements PostService {
	private final PostFollowingRepository postFollowingRepository;
	private final PostRepository postRepository;

	public PostServiceImpl(PostRepository postRepository, PostFollowingRepository postFollowingRepository) {
		this.postRepository = postRepository;
		this.postFollowingRepository = postFollowingRepository;
	}

	@Override
	public void deletePost(String id) {
		postRepository.delete(id);
	}

	@Override
	public Page<PostEntity> findPostByContentContaining(String content, Pageable pageable) {
		return postRepository.findByContentContaining(content, pageable);
	}

	@Override
	public Page<PostEntity> findPostByFollowerId(String followerId, Pageable pageable) {
		return postFollowingRepository.findByFollowerId(followerId, pageable);
	}

	@Override
	public Optional<PostEntity> findPostById(String id) {
		return postRepository.findById(id);
	}

	private PostEntity findPostByIdOrThrow(String id) {
		return findPostById(id)
			.orElseThrow(() -> new IllegalArgumentException("Post not found"));
	}

	@Override
	public Page<PostEntity> findPostByPosterId(String userId, Pageable pageable) {
		return postRepository.findByPosterId(userId, pageable);
	}

	@Override
	public Page<PostEntity> findPostReplies(String postId, Pageable pageable) {
		return postRepository.findByResponseToId(postId, pageable);
	}

	@Override
	public PostEntity replyPost(String postId, PostEntity replyPost) {
		PostEntity post = findPostByIdOrThrow(postId);
		return postRepository.save(replyPost.setResponse(true)
			.setResponseTo(post));
	}

	@Override
	@Transactional
	public PostEntity savePost(UserEntity currentUser, PostEntity post) {
		post.setPoster(currentUser);
		return postRepository.save(post);
	}

	@Override
	@Transactional
	public PostEntity updatePost(String id, PostEntity post) {
		PostEntity p = findPostByIdOrThrow(id);
		return p.setContent(post.getContent());
	}
}
