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

import org.codenergic.theskeleton.core.security.User;
import org.codenergic.theskeleton.user.UserEntity;
import org.codenergic.theskeleton.user.UserMapper;
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
	private final PostMapper postMapper = PostMapper.newInstance();
	private final UserMapper userMapper = UserMapper.newInstance();

	public PostRestController(PostService postService, PostReactionService postReactionService) {
		this.postService = postService;
		this.postReactionService = postReactionService;
	}

	@DeleteMapping("/{id}")
	public void deletePost(@PathVariable("id") final String id) {
		postService.deletePost(id);
	}

	@DeleteMapping("/{id}/reactions")
	public void deleteReaction(@PathVariable("id") String postId, @AuthenticationPrincipal User user) {
		postReactionService.deletePostReaction(user.getId(), postId);
	}

	@GetMapping
	public Page<PostRestData> findPostByContentContaining(@RequestParam(name = "q", defaultValue = "") String content,
			Pageable pageable) {
		return postService.findPostByContentContaining(content, pageable).map(postMapper::toPostData);
	}

	@GetMapping("/following")
	public Page<PostRestData> findPostByFollower(@AuthenticationPrincipal User user,
			@SortDefault(sort = "lastModifiedDate", direction = Sort.Direction.DESC) Pageable pageable) {
		return postService.findPostByFollowerId(user.getId(), pageable).map(postMapper::toPostData);
	}

	@GetMapping("/{id}")
	public ResponseEntity<PostRestData> findPostById(@PathVariable("id") String id) {
		return postService.findPostById(id)
			.map(postMapper::toPostData)
			.map(ResponseEntity::ok)
			.orElse(ResponseEntity.notFound().build());
	}

	@GetMapping("/{id}/reactions/{reaction}s")
	public Page<UserRestData> findPostReactions(@PathVariable("id") String postId, @PathVariable("reaction") String reaction, Pageable pageable) {
		PostReactionType reactionType = PostReactionType.valueOf(reaction.toUpperCase());
		return postReactionService.findUserByPostReaction(postId, reactionType, pageable)
			.map(userMapper::toUserData);
	}

	@GetMapping("/{id}/responses")
	public Page<PostRestData> findPostReplies(@PathVariable("id") String id, Pageable pageable) {
		return postService.findPostReplies(id, pageable).map(postMapper::toPostData);
	}

	@PutMapping("/{id}/reactions")
	public void reactToPost(@PathVariable("id") String postId, @RequestBody String reaction, @AuthenticationPrincipal User user) {
		PostReactionType reactionType = PostReactionType.valueOf(reaction.toUpperCase());
		postReactionService.reactToPost(user.getId(), postId, reactionType);
	}

	@PostMapping("/{id}/responses")
	public PostRestData replyPost(@PathVariable("id") String id, @RequestBody PostRestData reply) {
		PostEntity postReply = postService.replyPost(id, postMapper.toPost(reply));
		return postMapper.toPostData(postReply);
	}

	@PostMapping
	public PostRestData savePost(@AuthenticationPrincipal UserEntity currentUser, @RequestBody @Validated(PostRestData.New.class) PostRestData postRestData) {
		PostEntity post = postService.savePost(currentUser, postMapper.toPost(postRestData));
		return postMapper.toPostData(post);
	}

	@PutMapping("/{id}")
	public PostRestData updatePost(@PathVariable("id") String id,
			@RequestBody @Validated(PostRestData.Existing.class) final PostRestData postRestData) {
		PostEntity post = postService.updatePost(id, postMapper.toPost(postRestData));
		return postMapper.toPostData(post);
	}
}
