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
package org.codenergic.theskeleton.registration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.codenergic.theskeleton.core.test.EnableRestDocs;
import org.codenergic.theskeleton.core.test.InjectUserDetailsService;
import org.codenergic.theskeleton.tokenstore.TokenStoreService;
import org.codenergic.theskeleton.user.UserEntity;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import com.fasterxml.jackson.databind.ObjectMapper;

@RunWith(SpringRunner.class)
@EnableSpringDataWebSupport
@WebMvcTest(controllers = {RegistrationRestController.class})
@EnableRestDocs
@InjectUserDetailsService
public class RegistrationRestControllerTest {
	@Autowired
	private MockMvc mockMvc;
	@Autowired
	private ObjectMapper objectMapper;
	@MockBean
	private RegistrationService registrationService;
	@MockBean
	private TokenStoreService tokenStoreService;

	@Test
	public void testRegister() throws Exception {
		final RegistrationForm form = new RegistrationForm()
				.setUsername("test123")
				.setPassword("12345678")
				.setEmail("test@email.com");
		final UserEntity user = new UserEntity().setId("123");
		when(registrationService.registerUser(any())).thenReturn(user);
		MockHttpServletRequestBuilder request = post("/api/register")
				.content("{\"username\": \"test123\", \"password\": \"12345678\", "
						+ "\"email\": \"test@email.com\"}")
			.contentType(MediaType.APPLICATION_JSON);
		MockHttpServletResponse response = mockMvc.perform(request)
			.andExpect(status().isOk())
			.andDo(document("registration"))
			.andReturn()
			.getResponse();
		assertThat(response.getContentAsString())
			.isEqualTo(objectMapper.writeValueAsString(form));
		verify(registrationService).registerUser(any());
	}
}
