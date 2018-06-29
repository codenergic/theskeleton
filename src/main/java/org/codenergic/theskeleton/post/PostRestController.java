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

import org.codenergic.theskeleton.user.UserEntity;
import org.codenergic.theskeleton.user.UserRestData;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.SortDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
@RequestMapping("/api/posts")
public class PostRestController {
	private final PostService postService;
	private final PostReactionService postReactionService;

	public PostRestController(PostService postService, PostReactionService postReactionService) {
		this.postService = postService;
		this.postReactionService = postReactionService;
	}

	@DeleteMapping("/{id}/reactions")
	public void deleteReaction(@PathVariable("id") String postId, @AuthenticationPrincipal UserEntity user) {
		postReactionService.deletePostReaction(user.getId(), postId);
	}

	@DeleteMapping("/{id}")
	public void deleteRole(@PathVariable("id") final String id) {
		postService.deletePost(id);
	}

	@GetMapping("/following")
	public Page<PostRestData> findPostByFollower(@AuthenticationPrincipal UserEntity user,
			@SortDefault(sort = "lastModifiedDate", direction = Sort.Direction.DESC) Pageable pageable) {
		return postService.findPostByFollowerId(user.getId(), pageable).map(this::mapPostEntityToData);
	}

	@GetMapping("/{id}")
	public ResponseEntity<PostRestData> findPostById(@PathVariable("id") String id) {
		return postService.findPostById(id)
			.map(this::mapPostEntityToData)
			.map(ResponseEntity::ok)
			.orElse(ResponseEntity.notFound().build());
	}

	@GetMapping
	public Page<PostRestData> findPostByTitleContaining(@RequestParam(name = "title", defaultValue = "") String title,
			Pageable pageable) {
		return postService.findPostByTitleContaining(title, pageable).map(this::mapPostEntityToData);
	}

	@GetMapping("/{id}/reactions/{reaction}s")
	public Page<UserRestData> findPostReactions(@PathVariable("id") String postId, @PathVariable("reaction") String reaction, Pageable pageable) {
		PostReactionType reactionType = PostReactionType.valueOf(reaction.toUpperCase());
		return postReactionService.findUserByPostReaction(postId, reactionType, pageable).map(user ->
			UserRestData.builder().username(user.getUsername()).pictureUrl(user.getPictureUrl()).build());
	}

	@GetMapping("/{id}/responses")
	public Page<PostRestData> findPostReplies(@PathVariable("id") String id, Pageable pageable) {
		return postService.findPostReplies(id, pageable).map(this::mapPostEntityToData);
	}

	private PostRestData mapPostEntityToData(PostEntity post) {
		return PostRestData.builder(post).build();
	}

	@PutMapping("/{id}/publish")
	public PostRestData publishPost(@PathVariable("id") String id, @RequestBody boolean publish) {
		PostEntity post = publish ? postService.publishPost(id) : postService.unPublishPost(id);
		return mapPostEntityToData(post);
	}

	@PutMapping("/{id}/reactions")
	public void reactToPost(@PathVariable("id") String postId, @RequestBody String reaction, @AuthenticationPrincipal UserEntity user) {
		PostReactionType reactionType = PostReactionType.valueOf(reaction.toUpperCase());
		postReactionService.reactToPost(user.getId(), postId, reactionType);
	}

	@PostMapping("/{id}/responses")
	public PostRestData replyPost(@PathVariable("id") String id, @RequestBody PostRestData reply) {
		PostEntity postReply = postService.replyPost(id, reply.toPostEntity());
		return mapPostEntityToData(postReply);
	}

	@PostMapping
	public PostRestData savePost(@AuthenticationPrincipal UserEntity currentUser, @RequestBody @Validated(PostRestData.New.class) PostRestData postRestData) {
		PostEntity post = postService.savePost(currentUser, postRestData.toPostEntity());
		return mapPostEntityToData(post);
	}

	@PutMapping("/{id}")
	public PostRestData updatePost(@PathVariable("id") String id,
			@RequestBody @Validated(PostRestData.Existing.class) final PostRestData postRestData) {
		PostEntity post = postService.updatePost(id, postRestData.toPostEntity());
		return mapPostEntityToData(post);
	}
}
