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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.HashSet;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

public class OAuth2ClientServiceTest {
	private OAuth2ClientService clientService;
	@Mock
	private OAuth2ClientRepository clientRepository;
	@Spy
	private PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);

	@Before
	public void init() {
		MockitoAnnotations.initMocks(this);
		this.clientService = OAuth2ClientService.newInstance(clientRepository, passwordEncoder);
	}

	@Test
	public void testGenerateSecret() {
		when(clientRepository.findOne("client")).thenReturn(new OAuth2ClientEntity().setId("client"));
		OAuth2ClientEntity result = clientService.generateSecret("client");
		assertThat(passwordEncoder.matches(result.getClientId(), result.getClientSecret())).isTrue();
		verify(clientRepository).findOne("client");
	}

	@Test
	public void testSaveClient() {
		clientService.saveClient(new OAuth2ClientEntity());
	}

	@Test
	public void testUpdateClient() {
		when(clientRepository.findOne("client")).thenReturn(new OAuth2ClientEntity().setId("client"));
		clientService.updateClient("client", new OAuth2ClientEntity().setAuthorizedGrantTypes(new HashSet<>()));
		verify(clientRepository).findOne("client");
	}

}
