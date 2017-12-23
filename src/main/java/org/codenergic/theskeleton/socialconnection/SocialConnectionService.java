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
package org.codenergic.theskeleton.socialconnection;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.persistence.NoResultException;

import org.codenergic.theskeleton.user.UserEntity;
import org.springframework.security.crypto.encrypt.TextEncryptor;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionData;
import org.springframework.social.connect.ConnectionFactory;
import org.springframework.social.connect.ConnectionFactoryLocator;
import org.springframework.social.connect.ConnectionKey;
import org.springframework.social.connect.ConnectionRepository;
import org.springframework.social.connect.NoSuchConnectionException;
import org.springframework.social.connect.NotConnectedException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

@Transactional(readOnly = true)
public class SocialConnectionService implements ConnectionRepository {
	private ServiceProviderConnectionMapper connectionMapper = new ServiceProviderConnectionMapper();
	private String userId;
	private SocialConnectionRepository connectionRepository;
	private ConnectionFactoryLocator connectionFactoryLocator;
	private TextEncryptor textEncryptor;

	public SocialConnectionService(String userId, SocialConnectionRepository connectionRepository, 
			ConnectionFactoryLocator connectionFactoryLocator, TextEncryptor textEncryptor) {
		this.userId = userId;
		this.connectionRepository = connectionRepository;
		this.connectionFactoryLocator = connectionFactoryLocator;
		this.textEncryptor = textEncryptor;
	}

	@Override
	public MultiValueMap<String, Connection<?>> findAllConnections() {
		List<SocialConnectionEntity> socialConnections = connectionRepository.findByUserIdOrderByRankAsc(userId);

		Set<String> registeredProviderIds = connectionFactoryLocator.registeredProviderIds();
		MultiValueMap<String, Connection<?>> connections = new LinkedMultiValueMap<String, Connection<?>>();
		registeredProviderIds.forEach(registeredProviderId -> connections.put(registeredProviderId, Collections.<Connection<?>> emptyList()));
		socialConnections.forEach(socialConnection -> {
			Connection<?> connection = connectionMapper.mapRow(socialConnection);
			String providerId = connection.getKey().getProviderId();
			if (connections.get(providerId).size() == 0) {
				connections.put(providerId, new LinkedList<Connection<?>>());
			}
			connections.add(providerId, connection);
		});

		return connections;
	}

	@Override
	public List<Connection<?>> findConnections(String providerId) {
		List<SocialConnectionEntity> socialConnections = connectionRepository
				.findByUserIdAndProviderOrderByRankAsc(userId, providerId);
		return socialConnections.stream()
				.map(connection -> connectionMapper.mapRow(connection))
				.collect(Collectors.toList());
	}

	@Override
	public <A> List<Connection<A>> findConnections(Class<A> apiType) {
		List<?> connections = findConnections(getProviderId(apiType));
		return (List<Connection<A>>) connections;
	}

	@Override
	public MultiValueMap<String, Connection<?>> findConnectionsToUsers(MultiValueMap<String, String> providerUserIds) {
		if (providerUserIds == null || providerUserIds.isEmpty()) {
			throw new IllegalArgumentException("Unable to execute find: no providerUsers provided");
		}
		
		// TODO

		return null;
	}

	@Override
	public Connection<?> getConnection(ConnectionKey connectionKey) {
		SocialConnectionEntity connection = connectionRepository
				.findByUserIdAndProviderAndProviderUserId(userId, connectionKey.getProviderId(),
						connectionKey.getProviderUserId());

		try {
			return connectionMapper.mapRow(connection);
		} catch (NoResultException e) {
			throw new NoSuchConnectionException(connectionKey);
		}
	}

	@Override
	public <A> Connection<A> getConnection(Class<A> apiType,
			String providerUserId) {
		String providerId = getProviderId(apiType);
		return (Connection<A>) getConnection(new ConnectionKey(providerId, providerUserId));
	}

	@Override
	public <A> Connection<A> getPrimaryConnection(Class<A> apiType) {
		String providerId = getProviderId(apiType);
		Connection<A> connection = (Connection<A>) findPrimaryConnection(providerId);
		
		if (connection == null) {
			throw new NotConnectedException(providerId);
		}
		
		return connection;
	}

	@Override
	public <A> Connection<A> findPrimaryConnection(Class<A> apiType) {
		String providerId = getProviderId(apiType);
		return (Connection<A>) findPrimaryConnection(providerId);
	}

	@Override
	@Transactional
	public void addConnection(Connection<?> connection) {
		ConnectionData data = connection.createData();
		int rank = connectionRepository.getRank(userId, data.getProviderId());

		SocialConnectionEntity userConnection = new SocialConnectionEntity()
			.setUser(new UserEntity().setId(userId))
			.setProvider(data.getProviderId())
			.setProviderUserId(data.getProviderUserId())
			.setRank(rank)
			.setDisplayName(data.getDisplayName())
			.setProfileUrl(data.getProfileUrl())
			.setImageUrl(data.getImageUrl())
			.setAccessToken(encrypt(data.getAccessToken()))
			.setSecret(encrypt(data.getSecret()))
			.setRefreshToken(encrypt(data.getRefreshToken()))
			.setExpireTime(data.getExpireTime());
		connectionRepository.save(userConnection);
	}

	@Override
	@Transactional
	public void updateConnection(Connection<?> connection) {
		ConnectionData data = connection.createData();
		SocialConnectionEntity userConnection = connectionRepository
				.findByUserIdAndProviderAndProviderUserId(userId, data.getProviderId(),
						data.getProviderUserId())
					.setDisplayName(data.getDisplayName())
					.setProfileUrl(data.getProfileUrl())
					.setImageUrl(data.getImageUrl())
					.setAccessToken(encrypt(data.getAccessToken()))
					.setSecret(encrypt(data.getSecret()))
					.setRefreshToken(encrypt(data.getRefreshToken()))
					.setExpireTime(data.getExpireTime());
		connectionRepository.save(userConnection);
	}

	@Override
	@Transactional
	public void removeConnections(String providerId) {
		List<SocialConnectionEntity> connections = connectionRepository
				.findByUserIdAndProvider(userId, providerId);
		connections.forEach(connection -> connectionRepository.delete(connection));
	}

	@Override
	public void removeConnection(ConnectionKey connectionKey) {
		SocialConnectionEntity connection = connectionRepository.findByUserIdAndProviderAndProviderUserId(
				userId, connectionKey.getProviderId(), connectionKey.getProviderUserId());
		connectionRepository.delete(connection);
	}

	private <A> String getProviderId(Class<A> apiType) {
		return connectionFactoryLocator.getConnectionFactory(apiType).getProviderId();
	}

	private String encrypt(String text) {
		return text != null ? textEncryptor.encrypt(text) : text;
	}

	private Connection<?> findPrimaryConnection(String providerId) {
		List<SocialConnectionEntity> socialConnections = connectionRepository.findByUserIdAndProviderAndRank(
				userId, providerId, 1);
		List<Connection<?>> connections = socialConnections.stream()
				.map(connection -> connectionMapper.mapRow(connection))
				.collect(Collectors.toList());
		if (connections.size() > 0) 
			return connections.get(0);
		else return null;
	}

	private final class ServiceProviderConnectionMapper {
		public Connection<?> mapRow(SocialConnectionEntity connection) {
			ConnectionData connectionData = mapConnectionData(connection);
			ConnectionFactory<?> connectionFactory = connectionFactoryLocator
					.getConnectionFactory(connectionData.getProviderId());
			return connectionFactory.createConnection(connectionData);
		}

		private ConnectionData mapConnectionData(SocialConnectionEntity connection) {
			return new ConnectionData(connection.getProvider().toString().toLowerCase(),
					connection.getProviderUserId(), connection.getDisplayName(),
					connection.getProfileUrl(), connection.getImageUrl(), decrypt(connection.getAccessToken()),
					decrypt(connection.getSecret()), decrypt(connection.getRefreshToken()),
					expireTime(connection.getExpireTime()));
		}

		private String decrypt(String encryptedText) {
			return encryptedText != null ? textEncryptor.decrypt(encryptedText) : encryptedText;
		}

		private Long expireTime(long expireTime) {
			return expireTime == 0 ? null : expireTime;
		}
	}
}
