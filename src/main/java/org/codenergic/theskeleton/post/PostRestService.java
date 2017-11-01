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

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/posts")
public class PostRestService {
	private PostService postService;

	public PostRestService(PostService postService) {
		this.postService = postService;
	}

	@DeleteMapping("/{id}")
	public void deleteRole(@PathVariable("id") final String id) {
		postService.deletePost(id);
	}

	@GetMapping("/{id}")
	public PostRestData findPostById(@PathVariable("id") String id) {
		PostEntity post = postService.findPostById(id);
		if (post == null)
			return null;
		return PostRestData.builder().fromPostEntity(post).build();
	}

	@GetMapping(params = { "username" })
	public Page<PostRestData> findPostByPoster(@RequestParam("username") String username, Pageable pageable) {
		return postService.findPostByPoster(username, pageable).map(p -> PostRestData.builder().fromPostEntity(p).build());
	}

	@GetMapping
	public Page<PostRestData> findPostByTitleContaining(@RequestParam(name = "title", defaultValue = "") String title, Pageable pageable) {
		return postService.findPostByTitleContaining(title, pageable)
			.map(p -> PostRestData.builder().fromPostEntity(p).build());
	}

	@PostMapping
	public PostRestData savePost(@RequestBody @Validated(PostRestData.New.class) PostRestData postRestData) {
		PostEntity post = postService.savePost(postRestData.toPostEntity());
		return PostRestData.builder().fromPostEntity(post).build();
	}

	@PutMapping("/{id}")
	public PostRestData updatePost(@PathVariable("id") String id, @RequestBody @Validated(PostRestData.Existing.class) final PostRestData postRestData) {
		PostEntity post = postService.updatePost(id, postRestData.toPostEntity());
		return PostRestData.builder().fromPostEntity(post).build();
	}
}
