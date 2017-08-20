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

	private void assertPostNotNull(PostEntity post) {
		Objects.requireNonNull(post, "Post not found");
	}

	@Override
	@Transactional
	public PostEntity savePost(PostEntity post) {
		return postRepository.save(post);
	}

	@Override
	@Transactional
	public PostEntity updatePost(String id, PostEntity post) {
		PostEntity p = postRepository.findOne(id);
		assertPostNotNull(p);
		p.setTitle(post.getTitle());
		p.setContent(post.getContent());
		return p;
	}

	@Override
	public void deletePost(String id) {
		PostEntity e = postRepository.findOne(id);
		assertPostNotNull(e);
		postRepository.delete(e);
	}

	@Override
	public Page<PostEntity> findPostByTitleContaining(String title, Pageable pageable) {
		return postRepository.findByTitleContaining(title, pageable);
	}
}
