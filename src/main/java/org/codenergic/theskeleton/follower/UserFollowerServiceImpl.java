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

import org.codenergic.theskeleton.user.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
class UserFollowerServiceImpl implements UserFollowerService {
	private final UserFollowerRepository userFollowerRepository;

	public UserFollowerServiceImpl(UserFollowerRepository userFollowerRepository) {
		this.userFollowerRepository = userFollowerRepository;
	}

	@Override
	public Page<UserEntity> findUserFollowers(String userId, Pageable pageable) {
		return userFollowerRepository.findByUserId(userId, pageable).map(UserFollowerEntity::getFollower);
	}

	@Override
	public Page<UserEntity> findUserFollowings(String userId, Pageable pageable) {
		return userFollowerRepository.findByFollowerId(userId, pageable).map(UserFollowerEntity::getUser);
	}

	@Override
	@Transactional
	public UserFollowerEntity followUser(String currentUserId, String followingUserId) {
		UserFollowerEntity userFollowerEntity = new UserFollowerEntity()
			.setUser(new UserEntity().setId(currentUserId))
			.setFollower(new UserEntity().setId(followingUserId));
		return userFollowerRepository.save(userFollowerEntity);
	}

	@Override
	public long getNumberOfFollowers(String userId) {
		return userFollowerRepository.countByUserId(userId);
	}

	@Override
	public long getNumberOfFollowings(String userId) {
		return userFollowerRepository.countByFollowerId(userId);
	}

	@Override
	public void unfollowUser(String currentUserId, String followedUserId) {
		UserFollowerEntity userFollower = userFollowerRepository.findByUserIdAndFollowerId(currentUserId, followedUserId)
			.orElseThrow(() -> new IllegalArgumentException("User follower cannot be found"));
		userFollowerRepository.delete(userFollower);
	}
}
