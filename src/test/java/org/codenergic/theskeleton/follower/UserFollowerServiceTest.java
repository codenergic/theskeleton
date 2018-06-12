package org.codenergic.theskeleton.follower;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.codenergic.theskeleton.follower.impl.UserFollowerServiceImpl;
import org.codenergic.theskeleton.user.UserEntity;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class UserFollowerServiceTest {
	@Mock
	private UserFollowerRepository userFollowerRepository;
	private UserFollowerService userFollowerService;
	private List<UserFollowerEntity> userFollowerEntities;

	@Before
	public void init() throws Exception {
		MockitoAnnotations.initMocks(this);
		userFollowerService = new UserFollowerServiceImpl(userFollowerRepository);

		userFollowerEntities = new ArrayList<>();
		for (int i = 0; i < 10; i++) {
			userFollowerEntities.add(new UserFollowerEntity()
				.setUser(new UserEntity().setId(i + "123"))
				.setFollower(new UserEntity().setId((i + 80) + "123")));
		}
	}

	@Test
	public void testFindUserFollowers() {
		when(userFollowerRepository.findByUserId(eq("123"), any())).thenReturn(new PageImpl<>(userFollowerEntities));
		Page<UserEntity> users = userFollowerService.findUserFollowers("123", new PageRequest(0, 10));
		assertThat(users.getTotalElements()).isEqualTo(10);
		assertThat(users.getContent()).first().hasFieldOrPropertyWithValue("id", "80123");
		verify(userFollowerRepository).findByUserId(eq("123"), any());
	}

	@Test
	public void testFindUserFollowings() {
		when(userFollowerRepository.findByFollowerId(eq("123"), any())).thenReturn(new PageImpl<>(userFollowerEntities));
		Page<UserEntity> users = userFollowerService.findUserFollowings("123", new PageRequest(0, 10));
		assertThat(users.getTotalElements()).isEqualTo(10);
		assertThat(users.getContent()).first().hasFieldOrPropertyWithValue("id", "0123");
		verify(userFollowerRepository).findByFollowerId(eq("123"), any());
	}

	@Test
	public void testFollowUser() {
		when(userFollowerRepository.save(any(UserFollowerEntity.class))).then(invocation -> invocation.getArgument(0));
		UserFollowerEntity userFollower = userFollowerService.followUser("1234", "4567");
		assertThat(userFollower.getUser().getId()).isEqualTo("1234");
		assertThat(userFollower.getFollower().getId()).isEqualTo("4567");
		verify(userFollowerRepository).save(any(UserFollowerEntity.class));
	}

	@Test
	public void testGetNumberOfFollowers() {
		when(userFollowerRepository.countByUserId("123456")).thenReturn(10L);
		assertThat(userFollowerService.getNumberOfFollowers("123456")).isEqualTo(10L);
		verify(userFollowerRepository).countByUserId("123456");
	}

	@Test
	public void testGetNumberOfFollowings() {
		when(userFollowerRepository.countByFollowerId("123456")).thenReturn(10L);
		assertThat(userFollowerService.getNumberOfFollowings("123456")).isEqualTo(10L);
		verify(userFollowerRepository).countByFollowerId("123456");
	}

	@Test
	public void testUnfollowUser() {
		UserFollowerEntity userFollower = userFollowerEntities.get(1);
		when(userFollowerRepository.findByUserIdAndFollowerId(userFollower.getUser().getId(), userFollower.getFollower().getId()))
			.thenReturn(Optional.of(userFollower));
		assertThatThrownBy(() -> userFollowerService.unfollowUser("2", "1"))
			.isInstanceOf(IllegalArgumentException.class);
		userFollowerService.unfollowUser(userFollower.getUser().getId(), userFollower.getFollower().getId());
		verify(userFollowerRepository).findByUserIdAndFollowerId(userFollower.getUser().getId(), userFollower.getFollower().getId());
		verify(userFollowerRepository).delete(userFollower);
	}
}
