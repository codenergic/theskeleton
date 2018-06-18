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

import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;

import org.codenergic.theskeleton.client.impl.OAuth2ClientServiceImpl;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.provider.ClientRegistrationException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class OAuth2ClientServiceTest {
	private OAuth2ClientService clientService;
	@Mock
	private OAuth2ClientRepository clientRepository;
	@Spy
	private PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);

	@Before
	public void init() {
		MockitoAnnotations.initMocks(this);
		this.clientService = new OAuth2ClientServiceImpl(clientRepository, passwordEncoder);
	}

	@Test
	public void testDeleteClient() {
		OAuth2ClientEntity result = new OAuth2ClientEntity().setName("123");
		when(clientRepository.findById("123")).thenReturn(Optional.of(result));
		clientService.deleteClient("123");
		verify(clientRepository).delete(result);
		verify(clientRepository).findById("123");

		when(clientRepository.findById("1234")).thenReturn(Optional.empty());
		assertThatThrownBy(() -> clientService.deleteClient("1234")).isInstanceOf(ClientRegistrationException.class);
		verify(clientRepository).findById("1234");
	}

	@Test
	public void testFindClientByOwner() {
		Page<OAuth2ClientEntity> result = new PageImpl<>(Collections.singletonList(new OAuth2ClientEntity()));
		when(clientRepository.findByCreatedByUserId(anyString(), any())).thenReturn(result);
		assertThat(clientService.findClientByOwner("123", null)).isEqualTo(result);
		verify(clientRepository).findByCreatedByUserId(anyString(), any());
	}

	@Test
	public void testFindClients() {
		Page<OAuth2ClientEntity> result = new PageImpl<>(Collections.singletonList(new OAuth2ClientEntity()));
		when(clientRepository.findByNameOrDescriptionContaining(anyString(), any())).thenReturn(result);
		assertThat(clientService.findClients("123", null)).isEqualTo(result);
		verify(clientRepository).findByNameOrDescriptionContaining(anyString(), any());
	}

	@Test
	public void testGenerateSecret() {
		when(clientRepository.findById("client")).thenReturn(Optional.of(new OAuth2ClientEntity().setId("client")));
		OAuth2ClientEntity result = clientService.generateSecret("client");
		assertThat(passwordEncoder.matches(result.getClientId(), result.getClientSecret())).isTrue();
		verify(clientRepository).findById("client");
	}

	@Test
	public void testLoadClientByClientId() {
		OAuth2ClientEntity result = new OAuth2ClientEntity().setName("123");
		when(clientRepository.findById("123")).thenReturn(Optional.of(result));
		assertThat(clientService.loadClientByClientId("123")).isEqualTo(result);
		verify(clientRepository).findById("123");
	}

	@Test
	public void testSaveClient() {
		when(clientRepository.save(any(OAuth2ClientEntity.class))).then(invocation -> {
			OAuth2ClientEntity client = invocation.getArgument(0);
			return client.setId("123");
		});
		clientService.saveClient(new OAuth2ClientEntity());
		verify(clientRepository).save(any(OAuth2ClientEntity.class));
	}

	@Test
	public void testUpdateClient() {
		when(clientRepository.findById("client")).thenReturn(Optional.of(new OAuth2ClientEntity().setId("client")));
		clientService.updateClient("client", new OAuth2ClientEntity().setAuthorizedGrantTypes(new HashSet<>()));
		verify(clientRepository).findById("client");
	}

}
