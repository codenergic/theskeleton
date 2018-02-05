package org.codenergic.theskeleton.follower.impl;

import org.codenergic.theskeleton.follower.UserFollowerEntity;
import org.codenergic.theskeleton.follower.UserFollowerRepository;
import org.codenergic.theskeleton.follower.UserFollowerService;
import org.codenergic.theskeleton.user.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class UserFollowerServiceImpl implements UserFollowerService {
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
	public void unfollowUser(String currentUserId, String followedUserId) {
		UserFollowerEntity userFollower = userFollowerRepository.findByUserIdAndFollowerId(currentUserId, followedUserId);
		if (userFollower == null)
			throw new IllegalArgumentException("User follower cannot be found");
		userFollowerRepository.delete(userFollower);
	}
}
