package org.codenergic.theskeleton.user.profile;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.google.common.collect.ImmutableMap;
import io.minio.MinioClient;
import org.apache.commons.io.IOUtils;
import org.codenergic.theskeleton.client.OAuth2ClientEntity;
import org.codenergic.theskeleton.core.test.EnableRestDocs;
import org.codenergic.theskeleton.user.UserEntity;
import org.codenergic.theskeleton.user.UserOAuth2ClientApprovalEntity;
import org.codenergic.theskeleton.user.UserOAuth2ClientApprovalRestData;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.oauth2.provider.approval.Approval;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.security.Principal;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@RunWith(SpringRunner.class)
@WebMvcTest(controllers = {ProfileRestService.class})
@EnableRestDocs
public class ProfileRestServiceTest {
	@Autowired
	private MockMvc mockMvc;
	@Autowired
	private ObjectMapper objectMapper;
	@Mock
	private MinioClient minioClient;
	@MockBean
	private ProfileService profileService;
	@MockBean
	private SessionRegistry sessionRegistry;

	@Test
	@WithMockUser("user123")
	public void testFindProfileActiveSessions() throws Exception {
		final UserEntity user = new UserEntity().setUsername("user123");
		when(sessionRegistry.getAllPrincipals()).thenReturn(Collections.singletonList(user));
		final SessionInformation sessionInformation = new SessionInformation("1", "1", new Date());
		when(sessionRegistry.getAllSessions(user, true))
			.thenReturn(Collections.singletonList(sessionInformation));
		MockHttpServletRequestBuilder request = get("/api/profile/sessions")
			.contentType(MediaType.APPLICATION_JSON);
		MockHttpServletResponse response = mockMvc.perform(request)
			.andDo(document("user-profile-sessions-list"))
			.andReturn()
			.getResponse();
		assertThat(response.getStatus()).isEqualTo(200);
		List<SessionInformation> expectedValue = Collections
			.singletonList(new SessionInformation("user123", "1", sessionInformation.getLastRequest()));
		assertThat(response.getContentAsByteArray()).isEqualTo(objectMapper.writeValueAsBytes(expectedValue));
		verify(sessionRegistry).getAllPrincipals();
		verify(sessionRegistry).getAllSessions(user, true);
	}

	@Test
	@WithMockUser("user123")
	public void testFindProfileConnectedApps() throws Exception {
		final UserEntity user = new UserEntity().setUsername("user123");
		final OAuth2ClientEntity client = new OAuth2ClientEntity().setId("client123").setName("client123");
		final UserOAuth2ClientApprovalEntity approval1 = new UserOAuth2ClientApprovalEntity()
			.setUser(user)
			.setClient(client)
			.setScope("scope1")
			.setApprovalStatus(Approval.ApprovalStatus.APPROVED);
		final UserOAuth2ClientApprovalEntity approval2 = new UserOAuth2ClientApprovalEntity()
			.setUser(user)
			.setClient(client)
			.setScope("scope2")
			.setApprovalStatus(Approval.ApprovalStatus.DENIED);
		when(profileService.findOAuth2ClientApprovalByUsername("user123")).thenReturn(Arrays.asList(approval1, approval2));
		MockHttpServletRequestBuilder request = get("/api/profile/connected-apps")
			.contentType(MediaType.APPLICATION_JSON);
		MockHttpServletResponse response = mockMvc.perform(request)
			.andDo(document("user-profile-connected-apps-view"))
			.andReturn()
			.getResponse();
		assertThat(response.getStatus()).isEqualTo(200);
		CollectionType collectionType = objectMapper.getTypeFactory().constructCollectionType(List.class, UserOAuth2ClientApprovalRestData.class);
		List<UserOAuth2ClientApprovalRestData> returnedData = objectMapper
			.readValue(response.getContentAsByteArray(), collectionType);
		assertThat(returnedData).hasSize(1);
		UserOAuth2ClientApprovalRestData restData = returnedData.get(0);
		assertThat(restData.getClientId()).isEqualTo(client.getId());
		assertThat(restData.getClientName()).isEqualTo(client.getName());
		assertThat(restData.getUsername()).isEqualTo(user.getUsername());
		ImmutableMap<String, Approval.ApprovalStatus> scopeAndStatus = restData.getScopeAndStatus();
		assertThat(scopeAndStatus).hasSize(2);
		assertThat(scopeAndStatus).containsEntry(approval1.getScope(), approval1.getApprovalStatus());
		assertThat(scopeAndStatus).containsEntry(approval2.getScope(), approval2.getApprovalStatus());
		verify(profileService).findOAuth2ClientApprovalByUsername("user123");
	}

	@Test
	@WithMockUser("user123")
	public void testFindUserByUsername() throws Exception {
		final UserEntity user = new UserEntity()
			.setId("user123")
			.setEmail("user@server");
		when(profileService.findProfileByUsername("user123")).thenReturn(user);
		MockHttpServletRequestBuilder request = get("/api/profile")
			.contentType(MediaType.APPLICATION_JSON);
		MockHttpServletResponse response = mockMvc.perform(request)
			.andDo(document("user-profile-view"))
			.andReturn()
			.getResponse();
		assertThat(response.getStatus()).isEqualTo(200);
		assertThat(response.getContentAsByteArray())
			.isEqualTo(objectMapper.writeValueAsBytes(ProfileRestData.builder().fromUserEntity(user).build()));
		verify(profileService).findProfileByUsername("user123");
	}

	@Test
	@WithMockUser("user123")
	public void testRemoveProfileConnectedApps() throws Exception {
		doAnswer(invocation -> null)
			.when(profileService)
			.removeOAuth2ClientApprovalByUsername("user123", "123");
		MockHttpServletResponse response = mockMvc.perform(delete("/api/profile/connected-apps/123"))
			.andDo(document("user-profile-connected-apps-remove"))
			.andReturn()
			.getResponse();
		assertThat(response.getStatus()).isEqualTo(200);
		assertThat(response.getContentAsByteArray()).hasSize(0);
		verify(profileService).removeOAuth2ClientApprovalByUsername("user123", "123");
	}

	@Test
	@WithMockUser("user123")
	public void testRevokeProfileSession() throws Exception {
		MockHttpServletRequestBuilder request = delete("/api/profile/sessions")
			.content("123")
			.contentType(MediaType.APPLICATION_JSON);
		MockHttpServletResponse response = mockMvc.perform(request)
			.andDo(document("user-profile-sessions-revoke"))
			.andReturn()
			.getResponse();
		assertThat(response.getStatus()).isEqualTo(200);
		assertThat(response.getContentLength()).isEqualTo(0);
		verify(sessionRegistry).removeSessionInformation("123");
	}

	@Test
	public void testSerializeDeserializeUser() throws IOException {
		ProfileRestData user = ProfileRestData.builder()
			.username("user")
			.password("123123123")
			.phoneNumber("1231123123123")
			.build();
		String json = objectMapper.writeValueAsString(user);
		ProfileRestData user2 = objectMapper.readValue(json, ProfileRestData.class);
		assertThat(user).isEqualTo(user2);
	}

	@Test
	@WithMockUser("user123")
	public void testUpdateProfile() throws Exception {
		final UserEntity user = new UserEntity()
			.setId("user125")
			.setUsername("user")
			.setEmail("user@server");
		when(profileService.updateProfile(eq("user123"), any())).thenReturn(user);
		MockHttpServletRequestBuilder request = put("/api/profile")
			.content("{\"username\": \"user1234\"}")
			.contentType(MediaType.APPLICATION_JSON);
		MockHttpServletResponse response = mockMvc.perform(request)
			.andDo(document("user-profile-update"))
			.andReturn()
			.getResponse();
		assertThat(response.getStatus()).isEqualTo(200);
		assertThat(response.getContentAsByteArray())
			.isEqualTo(objectMapper.writeValueAsBytes(ProfileRestData.builder().fromUserEntity(user).build()));
		verify(profileService).updateProfile(eq("user123"), any());
	}

	@Test
	@WithMockUser("user123")
	public void testUpdateProfilePassword() throws Exception {
		final UserEntity user = new UserEntity()
			.setId("user123")
			.setUsername("user")
			.setEmail("user@server");
		when(profileService.updateProfilePassword(eq("user123"), any())).thenReturn(user);
		MockHttpServletRequestBuilder request = put("/api/profile/password")
			.content("{\"username\": \"user123\"}")
			.contentType(MediaType.APPLICATION_JSON);
		MockHttpServletResponse response = mockMvc.perform(request)
			.andDo(document("user-profile-password-update"))
			.andReturn()
			.getResponse();
		assertThat(response.getStatus()).isEqualTo(200);
		assertThat(response.getContentAsByteArray())
			.isEqualTo(objectMapper.writeValueAsBytes(ProfileRestData.builder().fromUserEntity(user).build()));
		verify(profileService).updateProfilePassword(eq("user123"), any());
	}

	@Test
	@WithMockUser("user123")
	public void testUpdateProfilePicture() throws Exception {
		final UserEntity user = new UserEntity()
			.setId("user123")
			.setUsername("user")
			.setEmail("user@server");
		when(profileService.updateProfilePicture(eq("user123"), any(), eq("image/png"))).thenReturn(user);
		InputStream image = ClassLoader.getSystemResourceAsStream("static/logo.png");
		MockHttpServletRequestBuilder request = put("/api/profile/picture")
			.content(IOUtils.toByteArray(image))
			.contentType(MediaType.IMAGE_PNG);
		MockHttpServletResponse response = mockMvc.perform(request)
			.andDo(document("user-profile-picture-update"))
			.andReturn()
			.getResponse();
		assertThat(response.getStatus()).isEqualTo(200);
		assertThat(response.getContentAsByteArray())
			.isEqualTo(objectMapper.writeValueAsBytes(ProfileRestData.builder().fromUserEntity(user).build()));
		verify(profileService).updateProfilePicture(eq("user123"), any(), eq("image/png"));
		image.close();
	}
}
