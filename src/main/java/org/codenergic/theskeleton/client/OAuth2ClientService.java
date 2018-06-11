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

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.oauth2.provider.ClientDetailsService;

public interface OAuth2ClientService extends ClientDetailsService {
	@PreAuthorize("isAuthenticated() or hasAuthority('client_delete')")
	void deleteClient(String id);

	Optional<OAuth2ClientEntity> findClientById(String id);

	@PreAuthorize("isAuthenticated() and #userId == principal.id")
	Page<OAuth2ClientEntity> findClientByOwner(String userId, Pageable pageable);

	@PreAuthorize("hasAuthority('client_read_all')")
	Page<OAuth2ClientEntity> findClients(String keyword, Pageable pageable);

	@PreAuthorize("isAuthenticated()")
	@PostAuthorize("returnObject.createdBy.userId == principal.id or hasAuthority('client_generate_secret')")
	OAuth2ClientEntity generateSecret(String clientId);

	@PreAuthorize("isAuthenticated()")
	OAuth2ClientEntity saveClient(OAuth2ClientEntity client);

	@PreAuthorize("isAuthenticated()")
	@PostAuthorize("returnObject.createdBy.userId == principal.id or hasAuthority('client_update')")
	OAuth2ClientEntity updateClient(String clientId, OAuth2ClientEntity entity);
}
