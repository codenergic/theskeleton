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

package org.codenergic.theskeleton.user.profile;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.codenergic.theskeleton.core.test.EnableRestDocs;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.social.connect.*;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.util.LinkedMultiValueMap;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@RunWith(SpringRunner.class)
@WebMvcTest(controllers = {ProfileSocialRestController.class})
@EnableRestDocs
public class ProfileSocialRestControllerTest {
	@MockBean
	private ConnectionRepository connectionRepository;
	@Autowired
	private MockMvc mockMvc;
	@Autowired
	private ObjectMapper objectMapper;

	private static final String PROVIDER_ID = "facebookTest";
	private final ConnectionData connectionData = new ConnectionData(PROVIDER_ID,
		"user123",
		"user123",
		"http://profileUrl",
		"http://imageUrl",
		"accessToken",
		"s3cr3t",
		"refreshToken",
		0L);
	private Connection<Object> connection = new Connection<Object>() {
		@Override
		public ConnectionKey getKey() {
			return new ConnectionKey(connectionData.getProviderId(), connectionData.getProviderUserId());
		}

		@Override
		public String getDisplayName() {
			return connectionData.getDisplayName();
		}

		@Override
		public String getProfileUrl() {
			return connectionData.getProfileUrl();
		}

		@Override
		public String getImageUrl() {
			return connectionData.getImageUrl();
		}

		@Override
		public void sync() {

		}

		@Override
		public boolean test() {
			return false;
		}

		@Override
		public boolean hasExpired() {
			return false;
		}

		@Override
		public void refresh() {

		}

		@Override
		public UserProfile fetchUserProfile() {
			return null;
		}

		@Override
		public void updateStatus(String message) {

		}

		@Override
		public Object getApi() {
			return null;
		}

		@Override
		public ConnectionData createData() {
			return connectionData;
		}
	};

	@Test
	@WithMockUser("user123")
	public void testSocialConnections() throws Exception {
		LinkedMultiValueMap<String, Connection<?>> connections = new LinkedMultiValueMap<>();
		connections.add(connection.getKey().getProviderId(), connection);
		when(connectionRepository.findAllConnections()).thenReturn(connections);
		MockHttpServletRequestBuilder request = get("/api/profile/socials")
			.contentType(MediaType.APPLICATION_JSON);
		MockHttpServletResponse response = mockMvc.perform(request)
			.andDo(document("user-profile-socials-list"))
			.andReturn()
			.getResponse();
		assertThat(response.getStatus()).isEqualTo(200);
		JsonNode jsonResponse = objectMapper.readTree(response.getContentAsByteArray());
		assertThat(jsonResponse.isObject()).isTrue();
		assertThat(jsonResponse.has(PROVIDER_ID)).isTrue();
		assertThat(jsonResponse.get(PROVIDER_ID).isObject()).isTrue();
		assertThat(jsonResponse.get(PROVIDER_ID).get("imageUrl").textValue()).isEqualTo(connection.getImageUrl());
		verify(connectionRepository).findAllConnections();
	}

	@Test
	@WithMockUser("user123")
	public void testRemoveSocialConnection() throws Exception {
		MockHttpServletRequestBuilder request = delete("/api/profile/socials")
			.content(PROVIDER_ID)
			.contentType(MediaType.APPLICATION_JSON);
		MockHttpServletResponse response = mockMvc.perform(request)
			.andDo(document("user-profile-socials-remove"))
			.andReturn()
			.getResponse();
		assertThat(response.getStatus()).isEqualTo(200);
		verify(connectionRepository).removeConnections(PROVIDER_ID);
	}
}
