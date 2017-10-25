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

import org.apache.commons.lang3.StringUtils;
import org.codenergic.theskeleton.post.PostEntity;
import org.codenergic.theskeleton.post.PostRepository;
import org.codenergic.theskeleton.post.PostService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
@Transactional(readOnly = true)
public class PostServiceImpl implements PostService {

	private PostRepository postRepository;

	public PostServiceImpl(PostRepository postRepository) {
		this.postRepository = postRepository;
	}

	@Override
	public void deletePost(String id) {
		postRepository.delete(id);
	}

	@Override
	public PostEntity findPostById(String id) {
		return postRepository.findOne(id);
	}

	@Override
	public Page<PostEntity> findPostByPoster(String username, Pageable pageable) {
		return postRepository.findByPosterUsername(username, pageable);
	}

	@Override
	public Page<PostEntity> findPostByTitleContaining(String title, Pageable pageable) {
		return postRepository.findByTitleContaining(title, pageable);
	}

	@Override
	@Transactional
	public PostEntity savePost(PostEntity post) {
		post.setSlug(StringUtils.replace(post.getTitle().toLowerCase(), " ", "-"));
		post.setPostStatus(PostEntity.Status.POSTED);
		return postRepository.save(post);
	}

	@Override
	@Transactional
	public PostEntity updatePost(String id, PostEntity post) {
		PostEntity p = postRepository.findOne(id);
		Objects.requireNonNull(post, "Post not found");
		p.setTitle(post.getTitle());
		p.setContent(post.getContent());
		p.setSlug(StringUtils.replace(p.getTitle().toLowerCase(), " ", "-"));
		return p;
	}
}
