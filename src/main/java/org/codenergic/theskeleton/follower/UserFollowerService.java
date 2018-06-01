package org.codenergic.theskeleton.follower;

import org.codenergic.theskeleton.user.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserFollowerService {
	Page<UserEntity> findUserFollowers(String userId, Pageable pageable);

	Page<UserEntity> findUserFollowings(String userId, Pageable pageable);

	UserFollowerEntity followUser(String currentUserId, String followingUserId);

	long getNumberOfFollowers(String userId);

	long getNumberOfFollowings(String userId);

	void unfollowUser(String currentUserId, String followedUserId);
}
