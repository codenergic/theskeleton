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
import static org.codenergic.theskeleton.client.OAuth2GrantType.AUTHORIZATION_CODE;
import static org.codenergic.theskeleton.client.OAuth2GrantType.IMPLICIT;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;

import org.codenergic.theskeleton.core.test.EnableRestDocs;
import org.codenergic.theskeleton.user.UserEntity;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import com.fasterxml.jackson.databind.ObjectMapper;

@RunWith(SpringRunner.class)
@EnableSpringDataWebSupport
@WebMvcTest(controllers = {Oauth2ClientRestController.class})
@EnableRestDocs
public class Oauth2ClientRestControllerTest {
	@Autowired
	private MockMvc mockMvc;
	@Autowired
	private ObjectMapper objectMapper;
	@MockBean
	private OAuth2ClientService oAuth2ClientService;

//	@Test
//	@WithMockUser("user123")
	public void testDeleteClient() throws Exception {
		mockMvc.perform(delete("/api/clients/client123")
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andDo(document("client-delete"));
	}

//	@Test
//	@WithMockUser("user123")
	public void testFindClientById() throws Exception {
		final OAuth2ClientEntity client = new OAuth2ClientEntity()
			.setId("client123")
			.setName("client")
			.setDescription("description")
			.setClientSecret("123456secret")
			.setSecretRequired(true)
			.setAutoApprove(false)
			.setAuthorizedGrantTypes(new HashSet<>(Arrays.asList(AUTHORIZATION_CODE, IMPLICIT)));
		when(oAuth2ClientService.findClientById("client123")).thenReturn(client);
		MockHttpServletRequestBuilder request = get("/api/clients/client123")
			.contentType(MediaType.APPLICATION_JSON);
		MockHttpServletResponse response = mockMvc.perform(request)
			.andExpect(status().isOk())
			.andDo(document("client-read"))
			.andReturn()
			.getResponse();
		assertThat(response.getContentAsByteArray())
			.isEqualTo(objectMapper.writeValueAsBytes(OAuth2ClientRestData.builder()
					.fromOAuth2ClientEntity(client).build()));
		verify(oAuth2ClientService).findClientById("client123");
	}

	@Test
//	@WithMockUser("user123")
	public void testFindClients() throws Exception {
		final UserEntity user = new UserEntity()
				.setId("user123")
				.setUsername("user123")
				.setPassword("123456");
		final OAuth2ClientEntity client = new OAuth2ClientEntity()
			.setId("client123")
			.setName("client")
			.setDescription("description")
			.setClientSecret("123456secret")
			.setSecretRequired(true)
			.setAutoApprove(false)
			.setAuthorizedGrantTypes(new HashSet<>(Arrays.asList(AUTHORIZATION_CODE, IMPLICIT)));
		Page<OAuth2ClientEntity> clients = new PageImpl<>(Arrays.asList(client));
		when(oAuth2ClientService.findClientByOwner(eq("admin"), any())).thenReturn(clients);
		MockHttpServletRequestBuilder request = get("/api/clients")
			.with(SecurityMockMvcRequestPostProcessors.user(user))
			.contentType(MediaType.APPLICATION_JSON);
		MockHttpServletResponse response = mockMvc.perform(request)
			.andExpect(status().isOk())
			.andDo(document("client-read-all"))
			.andReturn()
			.getResponse();
		assertThat(response.getContentAsByteArray())
			.isEqualTo(objectMapper.writeValueAsBytes(
				clients.map(c -> OAuth2ClientRestData.builder().fromOAuth2ClientEntity(c).build())));
		verify(oAuth2ClientService).findClientByOwner(eq("admin"), any());
	}

//	@Test
//	@WithMockUser("user123")
	public void testGenerateClientSecret() throws Exception {
		final OAuth2ClientEntity client = new OAuth2ClientEntity()
				.setId("client123");
		when(oAuth2ClientService.generateSecret(eq("client123"))).thenReturn(client);
		MockHttpServletRequestBuilder request = put("/api/clients/client123/generate-secret")
			.contentType(MediaType.APPLICATION_JSON);
		MockHttpServletResponse response = mockMvc.perform(request)
			.andExpect(status().isOk())
			.andDo(document("client-generate-secret"))
			.andReturn()
			.getResponse();
		assertThat(response.getContentAsByteArray())
		.isEqualTo(objectMapper.writeValueAsBytes(OAuth2ClientRestData.builder()
				.fromOAuth2ClientEntity(client).build()));
		verify(oAuth2ClientService).generateSecret(eq("client123"));
	}

//	@Test
//	@WithMockUser("user123")
	public void testSaveClient() throws Exception {
		final OAuth2ClientEntity client = new OAuth2ClientEntity()
				.setId("client123")
				.setName("client")
				.setDescription("description")
				.setSecretRequired(true)
				.setAutoApprove(false)
				.setAuthorizedGrantTypes(new HashSet<>(Arrays.asList(AUTHORIZATION_CODE, IMPLICIT)));
		when(oAuth2ClientService.saveClient(any())).thenReturn(client);
		MockHttpServletRequestBuilder request = post("/api/clients")
				.content("{\"name\": \"client\", \"description\": \"description\", "
						+ "\"isSecretRequired\": true, \"isAutoApprove\": false, "
						+ "\"authorizedGrantTypes\": [\"AUTHORIZATION_CODE\",\"IMPLICIT\"]}")
			.contentType(MediaType.APPLICATION_JSON);
		MockHttpServletResponse response = mockMvc.perform(request)
			.andExpect(status().isOk())
			.andDo(document("client-create"))
			.andReturn()
			.getResponse();
		assertThat(response.getContentAsByteArray())
			.isEqualTo(objectMapper.writeValueAsBytes(OAuth2ClientRestData.builder()
				.fromOAuth2ClientEntity(client).build()));
		verify(oAuth2ClientService).saveClient(any());
	}

//	@Test
	public void testSerializeDeserializeClient() throws IOException {
		OAuth2ClientRestData client = OAuth2ClientRestData.builder()
			.id("client123")
			.name("client")
			.description("description")
			.clientSecret("123456secret")
			.isSecretRequired(true)
			.isAutoApprove(false)
			.authorizedGrantTypes(new HashSet<>(Arrays.asList("AUTHORIZATION_CODE", "IMPLICIT")))
			.build();
		String json = objectMapper.writeValueAsString(client);
		OAuth2ClientRestData client2 = objectMapper.readValue(json, OAuth2ClientRestData.class);
		assertThat(client).isEqualTo(client2);
	}

//	@Test
//	@WithMockUser("user123")
	public void testUpdateClient() throws Exception {
		final OAuth2ClientEntity client = new OAuth2ClientEntity()
				.setId("client123");
		when(oAuth2ClientService.updateClient(eq("client123"), any())).thenReturn(client);
		MockHttpServletRequestBuilder request = put("/api/clients/client123")
			.content("{\"name\": \"client\", \"description\": \"description\", \"clientSecret\": \"s3cret\", "
					+ "\"isSecretRequired\": true, \"isAutoApprove\": false, "
					+ "\"authorizedGrantTypes\": [\"AUTHORIZATION_CODE\",\"IMPLICIT\"]}")
			.contentType(MediaType.APPLICATION_JSON);
		MockHttpServletResponse response = mockMvc.perform(request)
			.andExpect(status().isOk())
			.andDo(document("client-update"))
			.andReturn()
			.getResponse();
		assertThat(response.getContentAsByteArray())
		.isEqualTo(objectMapper.writeValueAsBytes(OAuth2ClientRestData.builder()
				.fromOAuth2ClientEntity(client).build()));
		verify(oAuth2ClientService).updateClient(eq("client123"), any());
	}
}
