package org.codenergic.theskeleton.user;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.apache.commons.io.IOUtils;
import org.codenergic.theskeleton.client.OAuth2ClientEntity;
import org.codenergic.theskeleton.core.test.EnableRestDocs;
import org.codenergic.theskeleton.core.test.EnableSecurityConfig;
import org.codenergic.theskeleton.tokenstore.TokenStoreService;
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
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.oauth2.provider.approval.Approval;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.google.common.collect.ImmutableMap;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@EnableSpringDataWebSupport
@WebMvcTest(controllers = { UserRestController.class })
@EnableRestDocs
@EnableSecurityConfig
public class UserRestControllerTest {
	@Autowired
	private MockMvc mockMvc;
	@Autowired
	private ObjectMapper objectMapper;
	@MockBean
	private UserService userService;
	@MockBean
	private TokenStoreService tokenStoreService;
	private UserMapper userMapper = UserMapper.newInstance();

	@Test
	@WithMockUser("user123")
	public void testUpdateUserPicture() throws Exception {
		final UserEntity user = new UserEntity()
			.setId("user123")
			.setUsername("user")
			.setEmail("user@server");
		when(userService.updateUserPicture(eq("user123"), any(), eq("image/png"), anyLong())).thenReturn(user);
		InputStream image = ClassLoader.getSystemResourceAsStream("static/logo.png");
		MockHttpServletRequestBuilder request = put("/api/users/user123/picture")
			.content(IOUtils.toByteArray(image))
			.contentType(MediaType.IMAGE_PNG);
		MockHttpServletResponse response = mockMvc.perform(request)
			.andDo(document("user-picture-update"))
			.andReturn()
			.getResponse();
		assertThat(response.getStatus()).isEqualTo(200);
		assertThat(response.getContentAsByteArray())
			.isEqualTo(objectMapper.writeValueAsBytes(userMapper.toUserData(user)));
		verify(userService).updateUserPicture(eq("user123"), any(), eq("image/png"), anyLong());
		image.close();
	}

	@Test
	public void testDeleteUser() throws Exception {
		mockMvc.perform(delete("/api/users/user123")
					.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andDo(document("user-delete"));
	}

	@Test
	public void testEnableOrDisableUser() throws Exception {
		final UserEntity user = new UserEntity()
				.setEnabled(true);
		when(userService.enableOrDisableUser("user123", true)).thenReturn(user);
		ResultActions resultActions = mockMvc.perform(put("/api/users/user123/enable")
					.content("{\"enabled\": true}")
					.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andDo(document("user-enable"));
		MockHttpServletResponse response = resultActions
				.andReturn()
				.getResponse();
		assertThat(response.getContentAsByteArray())
				.isEqualTo(objectMapper.writeValueAsBytes(userMapper.toUserData(user)));
		verify(userService).enableOrDisableUser("user123", true);
	}

	@Test
	public void testExtendsUserExpiration() throws Exception {
		final UserEntity user = new UserEntity()
				.setExpiredAt(new Date());
		when(userService.extendsUserExpiration("user123", 60)).thenReturn(user);
		ResultActions resultActions = mockMvc.perform(put("/api/users/user123/exp")
					.content("{\"amount\": 60}")
					.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andDo(document("user-extend-exp"));
		MockHttpServletResponse response = resultActions
				.andReturn()
				.getResponse();
		assertThat(response.getContentAsByteArray())
				.isEqualTo(objectMapper.writeValueAsBytes(userMapper.toUserData(user)));
		verify(userService).extendsUserExpiration("user123", 60);
	}

	@Test
	@WithMockUser("user123")
	public void testFindUserActiveSessions() throws Exception {
		final SessionInformation sessionInformation = new SessionInformation("1", "1", new Date());
		when(userService.findUserActiveSessions("user123")).thenReturn(Collections.singletonList(sessionInformation));
		MockHttpServletRequestBuilder request = get("/api/users/user123/sessions")
			.contentType(MediaType.APPLICATION_JSON);
		MockHttpServletResponse response = mockMvc.perform(request)
			.andDo(document("user-sessions-list"))
			.andReturn()
			.getResponse();
		assertThat(response.getStatus()).isEqualTo(200);
		List<SessionInformation> expectedValue = Collections.singletonList(sessionInformation);
		assertThat(response.getContentAsString()).isEqualTo(objectMapper.writeValueAsString(expectedValue));
		verify(userService).findUserActiveSessions("user123");
	}

	@Test
	public void testFindUserByEmail() throws Exception {
		final UserEntity user = new UserEntity()
				.setId("user123")
				.setEmail("user@server");
		when(userService.findUserByEmail("user@server")).thenReturn(Optional.of(user));
		ResultActions resultActions = mockMvc.perform(get("/api/users/user@server?email=true")
					.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andDo(document("user-read-email"));
		MockHttpServletResponse response = resultActions
				.andReturn()
				.getResponse();
		assertThat(response.getContentAsByteArray())
				.isEqualTo(objectMapper.writeValueAsBytes(userMapper.toUserData(user)));
		verify(userService).findUserByEmail("user@server");
	}

	@Test
	public void testFindUserByUsername() throws Exception {
		final UserEntity user = new UserEntity()
				.setId("user123")
				.setEmail("user@server");
		when(userService.findUserByUsername("user123")).thenReturn(Optional.of(user));
		ResultActions resultActions = mockMvc.perform(get("/api/users/user123")
					.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andDo(document("user-read"));
		MockHttpServletResponse response = resultActions
				.andReturn()
				.getResponse();
		assertThat(response.getContentAsByteArray())
				.isEqualTo(objectMapper.writeValueAsBytes(userMapper.toUserData(user)));
		verify(userService).findUserByUsername("user123");
	}

	@Test
	@WithMockUser("user123")
	public void testFindUserConnectedApps() throws Exception {
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
		when(userService.findUserOAuth2ClientApprovalByUsername("user123")).thenReturn(Arrays.asList(approval1, approval2));
		MockHttpServletRequestBuilder request = get("/api/users/user123/connected-apps")
			.contentType(MediaType.APPLICATION_JSON);
		MockHttpServletResponse response = mockMvc.perform(request)
			.andDo(document("user-connected-apps-view"))
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
		verify(userService).findUserOAuth2ClientApprovalByUsername("user123");
	}

	@Test
	public void testFindUsersByUsernameStartingWith() throws Exception {
		final UserEntity user = new UserEntity()
				.setId("user123")
				.setEmail("user@server");
		final Page<UserEntity> users = new PageImpl<>(Collections.singletonList(user));
		when(userService.findUsersByUsernameStartingWith(eq("user123"), any())).thenReturn(users);
		ResultActions resultActions = mockMvc.perform(get("/api/users?username=user123")
					.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andDo(document("user-read-username-startingwith"));
		MockHttpServletResponse response = resultActions
				.andReturn()
				.getResponse();
		assertThat(response.getContentAsByteArray())
				.isEqualTo(objectMapper.writeValueAsBytes(users.map(userMapper::toUserData)));
		verify(userService).findUsersByUsernameStartingWith(eq("user123"), any());
	}

	@Test
	public void testLockOrUnlockUser() throws Exception {
		final UserEntity user = new UserEntity()
				.setAccountNonLocked(true);
		when(userService.lockOrUnlockUser("user123", true)).thenReturn(user);
		ResultActions resultActions = mockMvc.perform(put("/api/users/user123/lock")
					.content("{\"enabled\": true}")
					.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andDo(document("user-lock"));
		MockHttpServletResponse response = resultActions
				.andReturn()
				.getResponse();
		assertThat(response.getContentAsByteArray())
				.isEqualTo(objectMapper.writeValueAsBytes(userMapper.toUserData(user)));
		verify(userService).lockOrUnlockUser("user123", true);
	}

	@Test
	@WithMockUser("user123")
	public void testRemoveUserConnectedApps() throws Exception {
		doAnswer(invocation -> null)
			.when(userService)
			.removeUserOAuth2ClientApprovalByUsername("user123", "123");
		MockHttpServletResponse response = mockMvc.perform(delete("/api/users/user123/connected-apps").content("123"))
			.andDo(document("user-connected-apps-remove"))
			.andReturn()
			.getResponse();
		assertThat(response.getStatus()).isEqualTo(200);
		assertThat(response.getContentAsByteArray()).hasSize(0);
		verify(userService).removeUserOAuth2ClientApprovalByUsername("user123", "123");
	}

	@Test
	@WithMockUser("user123")
	public void testRevokeUserSession() throws Exception {
		MockHttpServletRequestBuilder request = delete("/api/users/user123/sessions")
			.content("123")
			.contentType(MediaType.APPLICATION_JSON);
		MockHttpServletResponse response = mockMvc.perform(request)
			.andDo(document("user-sessions-revoke"))
			.andReturn()
			.getResponse();
		assertThat(response.getStatus()).isEqualTo(200);
		assertThat(response.getContentLength()).isEqualTo(0);
		verify(userService).revokeUserSession("user123", "123");
	}

	@Test
	public void testSaveInvalidUserEmail() throws Exception {
		MockHttpServletResponse response = mockMvc.perform(post("/api/users")
			.content("{\"username\": \"user123\", \"email\": \"user123\"}")
			.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isBadRequest())
			.andReturn()
			.getResponse();
		assertThat(response.getStatus()).isEqualTo(400);
	}

	@Test
	public void testSaveUser() throws Exception {
		final UserEntity user = new UserEntity()
				.setId("user123");
		when(userService.saveUser(any())).thenReturn(user);
		ResultActions resultActions = mockMvc.perform(post("/api/users")
					.content("{\"username\": \"user123\", \"email\": \"user@server.com\"}")
					.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andDo(document("user-create"));
		MockHttpServletResponse response = resultActions
				.andReturn()
				.getResponse();
		assertThat(response.getContentAsByteArray())
				.isEqualTo(objectMapper.writeValueAsBytes(userMapper.toUserData(user)));
		verify(userService).saveUser(any());
	}

	@Test
	public void testSerializeDeserializeUser() throws IOException {
		UserRestData user = ImmutableUserRestData.builder()
				.id("123")
				.username("user")
				.build();
		String json = objectMapper.writeValueAsString(user);
		UserRestData user2 = objectMapper.readValue(json, UserRestData.class);
		assertThat(user).isEqualTo(user2);
	}

	@Test
	public void testUpdateUser() throws Exception {
		final UserEntity user = new UserEntity()
				.setId("user123");
		when(userService.updateUser(eq("user123"), any())).thenReturn(user);
		ResultActions resultActions = mockMvc.perform(put("/api/users/user123")
					.content("{\"username\": \"user123\", \"email\": \"user@server.com\"}")
					.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andDo(document("user-update"));
		MockHttpServletResponse response = resultActions
				.andReturn()
				.getResponse();
		assertThat(response.getContentAsByteArray())
				.isEqualTo(objectMapper.writeValueAsBytes(userMapper.toUserData(user)));
		verify(userService).updateUser(eq("user123"), any());
	}

	@Test
	public void testUpdateUserPassword() throws Exception {
		final UserEntity user = new UserEntity()
				.setId("user123");
		when(userService.updateUserPassword(eq("user123"), any())).thenReturn(user);
		ResultActions resultActions = mockMvc.perform(put("/api/users/user123/password")
					.content("{\"username\": \"user123\"}")
					.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andDo(document("user-update-password"));
		MockHttpServletResponse response = resultActions
				.andReturn()
				.getResponse();
		assertThat(response.getContentAsByteArray())
				.isEqualTo(objectMapper.writeValueAsBytes(userMapper.toUserData(user)));
		verify(userService).updateUserPassword(eq("user123"), any());
	}

}
