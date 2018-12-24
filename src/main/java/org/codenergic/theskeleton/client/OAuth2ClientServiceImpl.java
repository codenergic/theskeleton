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
package org.codenergic.theskeleton.client;

import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.lang3.Validate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientRegistrationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
class OAuth2ClientServiceImpl implements OAuth2ClientService {
	private OAuth2ClientRepository clientRepository;
	private PasswordEncoder passwordEncoder;

	public OAuth2ClientServiceImpl(OAuth2ClientRepository clientRepository, PasswordEncoder passwordEncoder) {
		this.clientRepository = clientRepository;
		this.passwordEncoder = passwordEncoder;
	}

	@Override
	@Transactional
	public void deleteClient(String id) {
		OAuth2ClientEntity o = findClientById(id)
			.orElseThrow(() -> new ClientRegistrationException(id));
		clientRepository.delete(o);
	}

	@Override
	public Optional<OAuth2ClientEntity> findClientById(String id) {
		return clientRepository.findById(id);
	}

	@Override
	public Page<OAuth2ClientEntity> findClientByOwner(String userId, Pageable pageable) {
		return clientRepository.findByCreatedByUserId(userId, pageable);
	}

	@Override
	public Page<OAuth2ClientEntity> findClients(String keyword, Pageable pageable) {
		return clientRepository.findByNameOrDescriptionContaining(keyword, pageable);
	}

	@Override
	@Transactional
	public OAuth2ClientEntity generateSecret(String clientId) {
		OAuth2ClientEntity client = findClientById(clientId)
			.orElseThrow(() -> new ClientRegistrationException(clientId));
		return generateSecret(client);
	}

	private OAuth2ClientEntity generateSecret(OAuth2ClientEntity client) {
		Validate.notNull(client);
		Validate.notBlank(client.getId());
		return client.setClientSecret(passwordEncoder.encode(client.getClientId()));
	}

	@Override
	public ClientDetails loadClientByClientId(String clientId) {
		return findClientById(clientId)
			.orElseThrow(() -> new ClientRegistrationException(clientId));
	}

	@Override
	@Transactional
	public OAuth2ClientEntity saveClient(OAuth2ClientEntity client) {
		OAuth2ClientEntity savedClient = clientRepository.save(client.setId(null));
		return generateSecret(savedClient);
	}

	@Override
	@Transactional
	public OAuth2ClientEntity updateClient(String clientId, OAuth2ClientEntity newClient) {
		return findClientById(clientId)
			.orElseThrow(() -> new ClientRegistrationException(clientId))
			.setName(newClient.getName())
			.setDescription(newClient.getDescription())
			.setAuthorizedGrantTypes(newClient.getAuthorizedGrantTypes()
				.stream()
				.map(OAuth2GrantType::valueOf)
				.collect(Collectors.toSet()))
			.setAutoApprove(newClient.isAutoApprove())
			.setRegisteredRedirectUris(newClient.getRegisteredRedirectUri());
	}
}
