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
package org.codenergic.theskeleton.social;

import org.codenergic.theskeleton.user.UserEntity;
import org.springframework.security.crypto.encrypt.Encryptors;
import org.springframework.security.crypto.encrypt.TextEncryptor;
import org.springframework.social.connect.*;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import javax.persistence.NoResultException;
import java.util.List;
import java.util.stream.Collectors;

public class SocialConnectionService implements ConnectionRepository {
	private ServiceProviderConnectionMapper connectionMapper = new ServiceProviderConnectionMapper();
	private String userId;
	private SocialConnectionRepository connectionRepository;
	private ConnectionFactoryLocator connectionFactoryLocator;
	private TextEncryptor textEncryptor = Encryptors.noOpText();

	public SocialConnectionService(String userId, SocialConnectionRepository connectionRepository,
								   ConnectionFactoryLocator connectionFactoryLocator) {
		this.userId = userId;
		this.connectionRepository = connectionRepository;
		this.connectionFactoryLocator = connectionFactoryLocator;
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

	private String encrypt(String text) {
		return text == null ? null : textEncryptor.encrypt(text);
	}

	@Override
	public MultiValueMap<String, Connection<?>> findAllConnections() {
		List<SocialConnectionEntity> socialConnections = connectionRepository.findByUserIdOrderByRankAsc(userId);
		MultiValueMap<String, Connection<?>> connections = new LinkedMultiValueMap<>();
		socialConnections.forEach(socialConnection -> {
			Connection<?> connection = connectionMapper.mapRow(socialConnection);
			String providerId = connection.getKey().getProviderId();
			connections.add(providerId, connection);
		});
		return connections;
	}

	@Override
	public List<Connection<?>> findConnections(String providerId) {
		return connectionRepository.findByUserIdAndProviderOrderByRankAsc(userId, providerId)
			.stream()
			.map(connection -> connectionMapper.mapRow(connection))
			.collect(Collectors.toList());
	}

	@SuppressWarnings("unchecked")
	@Override
	public <A> List<Connection<A>> findConnections(Class<A> apiType) {
		List<?> connections = findConnections(getProviderId(apiType));
		return (List<Connection<A>>) connections;
	}

	@Override
	public MultiValueMap<String, Connection<?>> findConnectionsToUsers(MultiValueMap<String, String> providerUserIds) {
		throw new UnsupportedOperationException();
	}

	@SuppressWarnings("unchecked")
	@Override
	public <A> Connection<A> findPrimaryConnection(Class<A> apiType) {
		String providerId = getProviderId(apiType);
		return (Connection<A>) findPrimaryConnection(providerId);
	}

	private Connection<?> findPrimaryConnection(String providerId) {
		List<SocialConnectionEntity> socialConnections = connectionRepository.findByUserIdAndProviderAndRank(userId, providerId, 1);
		List<Connection<?>> connections = socialConnections.stream()
			.map(connection -> connectionMapper.mapRow(connection))
			.collect(Collectors.toList());
		return connections.isEmpty() ? null : connections.get(0);
	}

	@Override
	public Connection<?> getConnection(ConnectionKey connectionKey) {
		SocialConnectionEntity connection = connectionRepository
			.findByUserIdAndProviderAndProviderUserId(userId, connectionKey.getProviderId(), connectionKey.getProviderUserId());
		try {
			return connectionMapper.mapRow(connection);
		} catch (NoResultException e) {
			throw new NoSuchConnectionException(connectionKey);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public <A> Connection<A> getConnection(Class<A> apiType,
			String providerUserId) {
		String providerId = getProviderId(apiType);
		return (Connection<A>) getConnection(new ConnectionKey(providerId, providerUserId));
	}

	@SuppressWarnings("unchecked")
	@Override
	public <A> Connection<A> getPrimaryConnection(Class<A> apiType) {
		String providerId = getProviderId(apiType);
		Connection<A> connection = (Connection<A>) findPrimaryConnection(providerId);
		if (connection == null)
			throw new NotConnectedException(providerId);
		return connection;
	}

	private <A> String getProviderId(Class<A> apiType) {
		return connectionFactoryLocator.getConnectionFactory(apiType).getProviderId();
	}

	@Override
	public void removeConnection(ConnectionKey connectionKey) {
		SocialConnectionEntity connection = connectionRepository.findByUserIdAndProviderAndProviderUserId(
				userId, connectionKey.getProviderId(), connectionKey.getProviderUserId());
		connectionRepository.delete(connection);
	}

	@Override
	@Transactional
	public void removeConnections(String providerId) {
		List<SocialConnectionEntity> connections = connectionRepository.findByUserIdAndProvider(userId, providerId);
		connectionRepository.delete(connections);
	}

	@Override
	@Transactional
	public void updateConnection(Connection<?> connection) {
		ConnectionData data = connection.createData();
		SocialConnectionEntity userConnection = connectionRepository
			.findByUserIdAndProviderAndProviderUserId(userId, data.getProviderId(), data.getProviderUserId())
			.setDisplayName(data.getDisplayName())
			.setProfileUrl(data.getProfileUrl())
			.setImageUrl(data.getImageUrl())
			.setAccessToken(encrypt(data.getAccessToken()))
			.setSecret(encrypt(data.getSecret()))
			.setRefreshToken(encrypt(data.getRefreshToken()))
			.setExpireTime(data.getExpireTime());
		connectionRepository.save(userConnection);
	}

	private final class ServiceProviderConnectionMapper {
		private String decrypt(String encryptedText) {
			return encryptedText == null ? null : textEncryptor.decrypt(encryptedText);
		}

		private Long expireTime(long expireTime) {
			return expireTime == 0 ? null : expireTime;
		}

		private ConnectionData mapConnectionData(SocialConnectionEntity connection) {
			return new ConnectionData(connection.getProvider(),
					connection.getProviderUserId(), connection.getDisplayName(),
					connection.getProfileUrl(), connection.getImageUrl(), decrypt(connection.getAccessToken()),
					decrypt(connection.getSecret()), decrypt(connection.getRefreshToken()),
					expireTime(connection.getExpireTime()));
		}

		Connection<?> mapRow(SocialConnectionEntity connection) {
			ConnectionData connectionData = mapConnectionData(connection);
			ConnectionFactory<?> connectionFactory = connectionFactoryLocator
					.getConnectionFactory(connectionData.getProviderId());
			return connectionFactory.createConnection(connectionData);
		}
	}
}
