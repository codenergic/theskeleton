/*
 * Copyright 2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *  *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *  *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.codenergic.theskeleton.social;

import org.codenergic.theskeleton.user.UserEntity;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.social.connect.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class SocialConnectionServiceTest {
	private static final String PROVIDER_ID = "facebookTest";
	private static final String USER_ID = "userTest";
	private final SocialConnectionEntity dummySocialConnection = new SocialConnectionEntity()
		.setProvider(PROVIDER_ID)
		.setProviderUserId(USER_ID)
		.setDisplayName(USER_ID)
		.setProfileUrl("http://profileUrl")
		.setImageUrl("http://imageUrl")
		.setAccessToken("AccessToken")
		.setSecret("s3cr3t")
		.setRefreshToken("RefreshToken")
		.setExpireTime(0L)
		.setUser(new UserEntity().setId("123456"));
	private final ConnectionData dummyConnectionData = new ConnectionData(PROVIDER_ID,
		USER_ID,
		USER_ID,
		dummySocialConnection.getProfileUrl(),
		dummySocialConnection.getImageUrl(),
		dummySocialConnection.getAccessToken(),
		dummySocialConnection.getSecret(),
		dummySocialConnection.getRefreshToken(),
		dummySocialConnection.getExpireTime());
	@Mock
	private ConnectionFactory connectionFactory;
	@Mock
	private ConnectionFactoryLocator connectionFactoryLocator;
	@Mock
	private SocialConnectionRepository socialConnectionRepository;
	private SocialConnectionService socialConnectionService;

	@SuppressWarnings("unchecked")
	@Before
	public void init() {
		MockitoAnnotations.initMocks(this);
		when(connectionFactory.getProviderId()).thenReturn(PROVIDER_ID);
		when(connectionFactory.createConnection(any())).then(invocation -> {
			ConnectionData connectionData = invocation.getArgument(0);
			return new DummyConnection(connectionData);
		});
		when(connectionFactoryLocator.getConnectionFactory(Object.class)).thenReturn(connectionFactory);
		when(connectionFactoryLocator.getConnectionFactory(PROVIDER_ID)).thenReturn(connectionFactory);
		socialConnectionService = new SocialConnectionService(USER_ID, socialConnectionRepository, connectionFactoryLocator);
	}

	@Test
	public void testAddConnection() {
		when(socialConnectionRepository.getRank(USER_ID, PROVIDER_ID)).thenReturn(1);
		socialConnectionService.addConnection(new DummyConnection(dummyConnectionData));
		verify(socialConnectionRepository).getRank(USER_ID, PROVIDER_ID);
		verify(socialConnectionRepository).save(any(SocialConnectionEntity.class));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testFindAllConnections() {
		when(socialConnectionRepository.findByUserIdOrderByRankAsc(USER_ID))
			.thenReturn(Collections.singletonList(dummySocialConnection));
		MultiValueMap<String, Connection<?>> connections = socialConnectionService.findAllConnections();
		assertThat(connections).containsKey(PROVIDER_ID);
		assertThat(connections.get(PROVIDER_ID)).hasSize(1);
		assertThat(connections.get(PROVIDER_ID)).first().hasFieldOrPropertyWithValue("profileUrl", dummySocialConnection.getProfileUrl());
		verify(connectionFactory).createConnection(any());
		verify(socialConnectionRepository).findByUserIdOrderByRankAsc(USER_ID);
	}

	@Test
	public void testFindConnections() {
		when(socialConnectionRepository.findByUserIdAndProviderOrderByRankAsc(USER_ID, PROVIDER_ID))
			.thenReturn(Collections.singletonList(dummySocialConnection));
		List<Connection<Object>> connections = socialConnectionService.findConnections(Object.class);
		assertThat(connections).hasSize(1);
		assertThat(connections).first().hasFieldOrPropertyWithValue("displayName", dummySocialConnection.getDisplayName());
		verify(connectionFactory).getProviderId();
		verify(connectionFactory).createConnection(any());
		verify(connectionFactoryLocator).getConnectionFactory(Object.class);
		verify(socialConnectionRepository).findByUserIdAndProviderOrderByRankAsc(USER_ID, PROVIDER_ID);
	}

	@Test
	public void testFindConnectionsToUsers() {
		assertThatThrownBy(() -> socialConnectionService.findConnectionsToUsers(new LinkedMultiValueMap<>()))
			.isInstanceOf(UnsupportedOperationException.class);
	}

	@Test
	public void testFindPrimaryConnection() {
		when(socialConnectionRepository.findByUserIdAndProviderAndRank(USER_ID, PROVIDER_ID, 1))
			.thenReturn(Collections.singletonList(dummySocialConnection));
		Connection<Object> conn = socialConnectionService.findPrimaryConnection(Object.class);
		assertThat(conn.getKey().getProviderId()).isEqualTo(PROVIDER_ID);
		assertThat(conn.getKey().getProviderUserId()).isEqualTo(USER_ID);
		conn = socialConnectionService.getPrimaryConnection(Object.class);
		assertThat(conn.getKey().getProviderId()).isEqualTo(PROVIDER_ID);
		assertThat(conn.getKey().getProviderUserId()).isEqualTo(USER_ID);
		verify(connectionFactoryLocator, times(2)).getConnectionFactory(Object.class);
		verify(socialConnectionRepository, times(2)).findByUserIdAndProviderAndRank(USER_ID, PROVIDER_ID, 1);
	}

	@Test
	public void testGetConnection() {
		when(socialConnectionRepository.findByUserIdAndProviderAndProviderUserId(USER_ID, PROVIDER_ID, USER_ID))
			.thenReturn(Optional.of(dummySocialConnection));
		Connection<?> conn = socialConnectionService.getConnection(new ConnectionKey(PROVIDER_ID, USER_ID));
		assertThat(conn.getKey().getProviderId()).isEqualTo(PROVIDER_ID);
		assertThat(conn.getKey().getProviderUserId()).isEqualTo(USER_ID);
		assertThat(conn.getDisplayName()).isEqualTo(dummySocialConnection.getDisplayName());
		conn = socialConnectionService.getConnection(Object.class, USER_ID);
		assertThat(conn.getKey().getProviderId()).isEqualTo(PROVIDER_ID);
		assertThat(conn.getKey().getProviderUserId()).isEqualTo(USER_ID);
		assertThat(conn.getDisplayName()).isEqualTo(dummySocialConnection.getDisplayName());
		verify(connectionFactoryLocator).getConnectionFactory(Object.class);
		verify(socialConnectionRepository, times(2)).findByUserIdAndProviderAndProviderUserId(USER_ID, PROVIDER_ID, USER_ID);
	}

	@Test
	public void testRemoveConnections() {
		when(socialConnectionRepository.findByUserIdAndProviderAndProviderUserId(USER_ID, PROVIDER_ID, USER_ID))
			.thenReturn(Optional.of(dummySocialConnection));
		when(socialConnectionRepository.findByUserIdAndProvider(USER_ID, PROVIDER_ID))
			.thenReturn(Collections.singletonList(dummySocialConnection));
		socialConnectionService.removeConnection(new ConnectionKey(PROVIDER_ID, USER_ID));
		socialConnectionService.removeConnections(PROVIDER_ID);
		verify(socialConnectionRepository).findByUserIdAndProviderAndProviderUserId(USER_ID, PROVIDER_ID, USER_ID);
		verify(socialConnectionRepository).findByUserIdAndProvider(USER_ID, PROVIDER_ID);
		verify(socialConnectionRepository).delete(dummySocialConnection);
		verify(socialConnectionRepository).delete(Collections.singletonList(dummySocialConnection));
	}

	@Test
	public void testSocialUserEntity() {
		assertThat(new SocialUserEntity(new UserEntity().setId("123")).getUserId()).isEqualTo("123");
	}

	@Test
	public void testUpdateConnection() {
		when(socialConnectionRepository.findByUserIdAndProviderAndProviderUserId(USER_ID, PROVIDER_ID, USER_ID))
			.thenReturn(Optional.of(dummySocialConnection));
		socialConnectionService.updateConnection(new DummyConnection(dummyConnectionData));
		verify(socialConnectionRepository).findByUserIdAndProviderAndProviderUserId(USER_ID, PROVIDER_ID, USER_ID);
		verify(socialConnectionRepository).save(any(SocialConnectionEntity.class));
	}

	private static class DummyConnection implements Connection<Object> {
		private final ConnectionData connectionData;

		private DummyConnection(ConnectionData connectionData) {
			this.connectionData = connectionData;
		}

		@Override
		public ConnectionData createData() {
			return connectionData;
		}

		@Override
		public UserProfile fetchUserProfile() {
			return null;
		}

		@Override
		public Object getApi() {
			return null;
		}

		@Override
		public String getDisplayName() {
			return connectionData.getDisplayName();
		}

		@Override
		public String getImageUrl() {
			return connectionData.getImageUrl();
		}

		@Override
		public ConnectionKey getKey() {
			return new ConnectionKey(connectionData.getProviderId(), connectionData.getProviderUserId());
		}

		@Override
		public String getProfileUrl() {
			return connectionData.getProfileUrl();
		}

		@Override
		public boolean hasExpired() {
			return false;
		}

		@Override
		public void refresh() {

		}

		@Override
		public void sync() {
		}

		@Override
		public boolean test() {
			return false;
		}

		@Override
		public void updateStatus(String message) {

		}
	}
}
