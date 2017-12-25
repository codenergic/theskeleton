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
package org.codenergic.theskeleton.core.social;

import org.codenergic.theskeleton.socialconnection.SocialConnectionEntity;
import org.codenergic.theskeleton.socialconnection.SocialConnectionRepository;
import org.codenergic.theskeleton.socialconnection.SocialConnectionService;
import org.springframework.social.connect.*;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class SocialUsersConnectionService implements UsersConnectionRepository {
	private ConnectionFactoryLocator connectionFactoryLocator;
	private SocialConnectionRepository connectionRepository;
	private ConnectionSignUp connectionSignUp;

	public SocialUsersConnectionService(ConnectionFactoryLocator locator, SocialConnectionRepository repository) {
		this.connectionFactoryLocator = locator;
		this.connectionRepository = repository;
	}

	@Override
	@Transactional
	public List<String> findUserIdsWithConnection(Connection<?> connection) {
		ConnectionKey key = connection.getKey();
		List<SocialConnectionEntity> localUsers = connectionRepository.findByProviderAndProviderUserId(key.getProviderId(), key.getProviderUserId());

		if (localUsers.isEmpty() && connectionSignUp != null) {
			String newUserId = connectionSignUp.execute(connection);
			if (newUserId != null) {
				createConnectionRepository(newUserId).addConnection(connection);
				return Collections.singletonList(newUserId);
			}
		}

		return localUsers.stream().map(u -> u.getUser().getId()).collect(Collectors.toList());
	}

	@Override
	public Set<String> findUserIdsConnectedTo(String providerId, Set<String> providerUserIds) {
		return connectionRepository.findByProviderAndProviderUserIdIn(providerId, providerUserIds)
			.stream().map(u -> u.getUser().getId()).collect(Collectors.toSet());
	}

	@Override
	@Transactional
	public SocialConnectionService createConnectionRepository(String userId) {
		if (userId == null)
			throw new IllegalArgumentException("UserId cannot be null");
		return new SocialConnectionService(userId, connectionRepository, connectionFactoryLocator);
	}

	public void setConnectionSignUp(ConnectionSignUp connectionSignUp) {
		this.connectionSignUp = connectionSignUp;
	}
}
