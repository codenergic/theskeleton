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
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/post")
public class PostRestService {
	@Autowired
	private PostService postService;

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

	@DeleteMapping("/{id}")
	public void deleteRole(@PathVariable("id") final String id) { postService.deletePost(id); }

	@GetMapping
	public Page<PostRestData> findPostByTitleContaining(
		@RequestParam(name = "title", defaultValue = "") String title, Pageable pageable) {
		return postService.findPostByTitleContaining(title, pageable)
			.map(p -> PostRestData.builder().fromPostEntity(p).build());
	}
}
