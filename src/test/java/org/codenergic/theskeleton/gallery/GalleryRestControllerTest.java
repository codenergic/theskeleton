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
package org.codenergic.theskeleton.gallery;

import org.codenergic.theskeleton.core.data.AuditInformation;
import org.codenergic.theskeleton.core.test.EnableRestDocs;
import org.codenergic.theskeleton.user.UserEntity;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.method.annotation.AuthenticationPrincipalArgumentResolver;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@ContextConfiguration
@WebAppConfiguration
@EnableRestDocs
public class GalleryRestControllerTest {
	private static final String USER_ID = "12345";
	private static final String USERNAME = "user12345";

	@MockBean
	private GalleryService galleryService;
	@Autowired
	private RestDocumentationContextProvider restDocumentation;
	private MockMvc mockMvc;

	@Before
	public void init() {
		mockMvc = MockMvcBuilders
			.standaloneSetup(new GalleryRestController(galleryService))
			.setCustomArgumentResolvers(new AuthenticationPrincipalArgumentResolver(), new PageableHandlerMethodArgumentResolver())
			.apply(documentationConfiguration(restDocumentation))
			.build();
		Authentication authentication = new UsernamePasswordAuthenticationToken(
			new UserEntity().setId(USER_ID).setUsername(USERNAME), "1234");
		SecurityContextHolder.getContext().setAuthentication(authentication);
	}

	@Test
	public void deleteImages() throws Exception {
		MockHttpServletResponse response = mockMvc.perform(delete("/api/galleries")
			.contentType(MediaType.APPLICATION_JSON).content("[\"12345\", \"123456\"]"))
			.andExpect(status().isOk())
			.andDo(document("gallery-delete"))
			.andReturn()
			.getResponse();
		assertThat(response.getStatus()).isEqualTo(200);
		verify(galleryService).deleteImages(USER_ID, "12345", "123456");
	}

	@Test
	public void testFindImageByUser() throws Exception {
		GalleryEntity gallery = new GalleryEntity().setImageUrl("http://img123");
		gallery.setCreatedBy(new AuditInformation().setUserId(USER_ID));
		when(galleryService.findImageByUser(eq(USER_ID), any())).thenReturn(new PageImpl<>(Collections.singletonList(gallery)));
		MockHttpServletResponse response = mockMvc.perform(get("/api/galleries")
			.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andDo(document("gallery-read"))
			.andReturn()
			.getResponse();
		assertThat(response.getStatus()).isEqualTo(200);
		assertThat(response.getContentType()).startsWith(MediaType.APPLICATION_JSON_VALUE);
		verify(galleryService).findImageByUser(eq(USER_ID), any());
	}

	@Test
	public void saveImage() throws Exception {
		GalleryEntity gallery = new GalleryEntity().setImageUrl("http://img123");
		gallery.setCreatedBy(new AuditInformation().setUserId(USER_ID));
		when(galleryService.saveImage(eq(USER_ID), any())).thenReturn(gallery);
		MockHttpServletResponse response = mockMvc.perform(post("/api/galleries")
			.contentType(MediaType.APPLICATION_JSON).content(new byte[0]).contentType(MediaType.IMAGE_PNG))
			.andExpect(status().isOk())
			.andDo(document("gallery-save"))
			.andReturn()
			.getResponse();
		assertThat(response.getStatus()).isEqualTo(200);
		verify(galleryService).saveImage(eq(USER_ID), any());
	}
}
