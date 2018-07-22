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

package org.codenergic.theskeleton.follower;

import org.codenergic.theskeleton.core.web.User;
import org.codenergic.theskeleton.user.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users/{username}")
public class UserFollowerRestController {
	private final UserFollowerService userFollowerService;

	public UserFollowerRestController(UserFollowerService userFollowerService) {
		this.userFollowerService = userFollowerService;
	}

	@GetMapping("/followers")
	public Page<UserFollowerRestData> findUserFollowers(@User UserEntity user, Pageable pageable) {
		return userFollowerService.findUserFollowers(user.getId(), pageable)
			.map(u -> UserFollowerRestData.builder()
				.followerUsername(u.getUsername())
				.followerPictureUrl(u.getPictureUrl())
				.build());
	}

	@GetMapping("/followings")
	public Page<UserFollowerRestData> findUserFollowings(@User UserEntity user, Pageable pageable) {
		return userFollowerService.findUserFollowings(user.getId(), pageable)
			.map(u -> UserFollowerRestData.builder()
				.followingUsername(u.getUsername())
				.followingPictureUrl(u.getPictureUrl())
				.build());
	}

	@PutMapping("/followers")
	public UserFollowerRestData followUser(@User UserEntity user, @AuthenticationPrincipal UserEntity currentUser) {
		UserFollowerEntity userFollower = userFollowerService.followUser(currentUser.getId(), user.getId());
		return UserFollowerRestData.builder()
			.followerUsername(userFollower.getFollower().getUsername())
			.followerPictureUrl(userFollower.getFollower().getPictureUrl())
			.followingUsername(userFollower.getUser().getUsername())
			.followingPictureUrl(userFollower.getUser().getPictureUrl())
			.build();
	}

	@GetMapping(path = "/followers", params = {"totals"})
	public long getNumberOfFollowers(@User UserEntity user) {
		return userFollowerService.getNumberOfFollowers(user.getId());
	}

	@GetMapping(path = "/followings", params = {"totals"})
	public long getNumberOfFollowings(@User UserEntity user) {
		return userFollowerService.getNumberOfFollowings(user.getId());
	}

	@DeleteMapping("/followers")
	public void unfollowUser(@User UserEntity user, @AuthenticationPrincipal UserEntity currentUser) {
		userFollowerService.unfollowUser(currentUser.getId(), user.getId());
	}
}
