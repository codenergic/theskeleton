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

import org.codenergic.theskeleton.core.web.User;
import org.codenergic.theskeleton.user.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users/{username}/posts")
public class UserPostRestController {
	private final PostService postService;
	private final PostMapper postMapper = PostMapper.newInstance();

	public UserPostRestController(PostService postService) {
		this.postService = postService;
	}

	@GetMapping
	public Page<PostRestData> findUserPublishedPost(@User UserEntity user, Pageable pageable) {
		return postService.findPublishedPostByPoster(user.getId(), pageable)
			.map(postMapper::toPostData);
	}

	@GetMapping(params = {"status"})
	public Page<PostRestData> findUserPostByStatus(@User UserEntity user, @RequestParam("status") String postStatus, Pageable pageable) {
		return postService.findPostByPosterAndStatus(user.getId(), PostStatus.valueOf(postStatus.toUpperCase()), pageable)
			.map(postMapper::toPostData);
	}
}
