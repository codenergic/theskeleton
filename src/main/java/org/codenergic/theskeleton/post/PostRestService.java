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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/post")
public class PostRestService {

	@Autowired
	private PostService postService;

	@PostMapping
	public PostRestData savePost(@RequestBody @Valid PostRestData postRestData) {
		return convertEntityToRestData(postService.savePost(postRestData.toEntity()));
	}

	@PutMapping("/{id}")
	public PostRestData updatePost(@PathVariable("id") String id, @RequestBody @Valid final PostRestData post) {
		return PostRestData.builder(postService.updatePost(id, post.toEntity()))
			.build();
	}

	@DeleteMapping("/{id}")
	public void deleteRole(@PathVariable("id") final String id) { postService.deletePost(id); }

	@GetMapping
	public Page<PostRestData> findPostByTitleContaining(
		@RequestParam(name = "title", defaultValue = "") String title, Pageable pageable) {
		return postService.findPostByTitleContaining(title, pageable)
			.map(this::convertEntityToRestData);
	}

	private PostRestData convertEntityToRestData(PostEntity post) {
		return post == null ? null : PostRestData.builder(post).build();
	}

}
