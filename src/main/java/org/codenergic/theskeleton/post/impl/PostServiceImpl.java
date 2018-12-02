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
package org.codenergic.theskeleton.post.impl;

import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.codenergic.theskeleton.post.PostEntity;
import org.codenergic.theskeleton.post.PostFollowingRepository;
import org.codenergic.theskeleton.post.PostRepository;
import org.codenergic.theskeleton.post.PostService;
import org.codenergic.theskeleton.post.PostStatus;
import org.codenergic.theskeleton.user.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class PostServiceImpl implements PostService {
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
	public Page<PostEntity> findPostByFollowerId(String followerId, Pageable pageable) {
		return postFollowingRepository.findByFollowerId(followerId, pageable);
	}

	@Override
	public Optional<PostEntity> findPostById(String id) {
		return postRepository.findById(id);
	}

	@Override
	public Page<PostEntity> findPostByPosterAndStatus(String userId, PostStatus postStatus, Pageable pageable) {
		return postRepository.findByPosterIdAndPostStatus(userId, postStatus, pageable);
	}

	@Override
	public Page<PostEntity> findPostByTitleContaining(String title, Pageable pageable) {
		return postRepository.findByTitleContaining(title, pageable);
	}

	@Override
	public Page<PostEntity> findPostReplies(String postId, Pageable pageable) {
		return postRepository.findByResponseToId(postId, pageable);
	}

	@Override
	public Page<PostEntity> findPublishedPostByPoster(String userId, Pageable pageable) {
		return postRepository.findByPosterIdAndPostStatus(userId, PostStatus.PUBLISHED, pageable);
	}

	@Override
	@Transactional
	public PostEntity publishPost(String id) {
		return updatePostStatus(id, PostStatus.PUBLISHED);
	}

	@Override
	public PostEntity replyPost(String postId, PostEntity replyPost) {
		PostEntity post = findPostByIdOrThrow(postId);
		return postRepository.save(replyPost.setResponse(true)
			.setPostStatus(PostStatus.PUBLISHED)
			.setTitle(StringUtils.substring(replyPost.getContent(), 0, 20))
			.setResponseTo(post));
	}

	@Override
	@Transactional
	public PostEntity savePost(UserEntity currentUser, PostEntity post) {
		post.setPostStatus(PostStatus.DRAFT);
		post.setPoster(currentUser);
		return postRepository.save(post);
	}

	@Override
	@Transactional
	public PostEntity unPublishPost(String id) {
		return updatePostStatus(id, PostStatus.DRAFT);
	}

	@Override
	@Transactional
	public PostEntity updatePost(String id, PostEntity post) {
		PostEntity p = findPostByIdOrThrow(id);
		return p.setTitle(post.getTitle())
			.setContent(post.getContent());
	}

	private PostEntity findPostByIdOrThrow(String id) {
		return findPostById(id)
			.orElseThrow(() -> new IllegalArgumentException("Post not found"));
	}

	private PostEntity updatePostStatus(String id, PostStatus status) {
		PostEntity p = findPostByIdOrThrow(id);
		return p.setPostStatus(status);
	}
}
