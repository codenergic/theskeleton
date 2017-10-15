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
package org.codenergic.theskeleton.client;

import org.codenergic.theskeleton.client.impl.OAuth2ClientServiceImpl;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.provider.ClientDetailsService;

public interface OAuth2ClientService extends ClientDetailsService {
	static OAuth2ClientService newInstance(OAuth2ClientRepository clientRepository, PasswordEncoder passwordEncoder) {
		return new OAuth2ClientServiceImpl(clientRepository, passwordEncoder);
	}

	@PreAuthorize("hasAuthority('client_delete')")
	void deleteClient(String id);

	OAuth2ClientEntity findClientById(String id);

	@PreAuthorize("hasAuthority('client_read_all')")
	Page<OAuth2ClientEntity> findClientByOwner(String userId, Pageable pageable);

	@PreAuthorize("hasAuthority('client_read_all')")
	Page<OAuth2ClientEntity> findClients(String keyword, Pageable pageable);

	@PreAuthorize("hasAuthority('client_generate_secret')")
	OAuth2ClientEntity generateSecret(String clientId);

	@PreAuthorize("hasAuthority('client_write')")
	OAuth2ClientEntity saveClient(OAuth2ClientEntity client);

	@PreAuthorize("hasAuthority('client_update')")
	OAuth2ClientEntity updateClient(String clientId, OAuth2ClientEntity entity);
}
