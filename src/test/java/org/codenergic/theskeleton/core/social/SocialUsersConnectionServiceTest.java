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
package org.codenergic.theskeleton.core.social;

import org.codenergic.theskeleton.socialconnection.SocialConnectionEntity;
import org.codenergic.theskeleton.socialconnection.SocialConnectionRepository;
import org.codenergic.theskeleton.user.UserEntity;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionFactoryLocator;
import org.springframework.social.connect.ConnectionKey;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class SocialUsersConnectionServiceTest {
	private static final String PROVIDER_ID = "facebookTest";
	private static final String USER_ID = "userTest";

	@Mock
	private ConnectionFactoryLocator connectionFactoryLocator;
	@Mock
	private SocialConnectionRepository connectionRepository;
	@Mock
	private Connection connection;
	private SocialUsersConnectionService usersConnectionService;

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

	@Before
	public void init() {
		MockitoAnnotations.initMocks(this);
		usersConnectionService = new SocialUsersConnectionService(connectionFactoryLocator, connectionRepository);
	}

	@Test
	public void testFindUserIdsWithConnection() {
		when(connection.getKey()).thenReturn(new ConnectionKey(PROVIDER_ID, USER_ID));
		when(connectionRepository.findByProviderAndProviderUserId(PROVIDER_ID, USER_ID))
			.thenReturn(Collections.singletonList(dummySocialConnection));
		List<String> users = usersConnectionService.findUserIdsWithConnection(connection);
		assertThat(users).hasSize(1);
		assertThat(users).first().isEqualTo(dummySocialConnection.getUser().getId());
		verify(connection).getKey();
		verify(connectionRepository).findByProviderAndProviderUserId(PROVIDER_ID, USER_ID);
	}

	@Test
	public void testFindUserIdsConnectedTo() {
		Set<String> providerUserIds = Collections.singleton(USER_ID);
		when(connectionRepository.findByProviderAndProviderUserIdIn(PROVIDER_ID, providerUserIds))
			.thenReturn(Collections.singletonList(dummySocialConnection));
		Set<String> users = usersConnectionService.findUserIdsConnectedTo(PROVIDER_ID, providerUserIds);
		assertThat(users).hasSize(1);
		assertThat(users).first().isEqualTo(dummySocialConnection.getUser().getId());
		verify(connectionRepository).findByProviderAndProviderUserIdIn(PROVIDER_ID, providerUserIds);
	}

	@Test
	public void testCreateConnectionRepository() {
		usersConnectionService.createConnectionRepository(USER_ID);
	}
}
