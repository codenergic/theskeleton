package org.codenergic.theskeleton.user;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import org.codenergic.theskeleton.role.RoleEntity;
import org.codenergic.theskeleton.role.RoleRestData;
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
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import com.fasterxml.jackson.databind.ObjectMapper;

@RunWith(SpringRunner.class)
@EnableSpringDataWebSupport
@WebMvcTest(controllers = { UserRestService.class }, secure = false)
public class UserRestServiceTest {
	@Autowired
	private MockMvc mockMvc;
	@Autowired
	private ObjectMapper objectMapper;
	@MockBean
	private UserService userService;

	@Test
	public void testAddRoleToUser() throws Exception {
		final UserEntity user = new UserEntity()
				.setId("123")
				.setUsername("user123")
				.setPassword("123456");
		when(userService.addRoleToUser("user123", "role123")).thenReturn(user);
		MockHttpServletRequestBuilder request = put("/api/users/user123/roles")
				.content("{\"role\": \"role123\"}")
				.contentType(MediaType.APPLICATION_JSON);
		MockHttpServletResponse response = mockMvc.perform(request)
				.andReturn()
				.getResponse();
		verify(userService).addRoleToUser("user123", "role123");
		assertThat(response.getStatus()).isEqualTo(200);
		assertThat(response.getContentAsByteArray())
				.isEqualTo(objectMapper.writeValueAsBytes(UserRestData.builder(user).build()));
	}

	@Test
	public void testEnableOrDisableUser() throws Exception {
		final UserEntity user = new UserEntity()
				.setEnabled(true);
		when(userService.enableOrDisableUser("user123", true)).thenReturn(user);
		MockHttpServletRequestBuilder request = put("/api/users/user123/enable")
				.content("{\"enabled\": true}")
				.contentType(MediaType.APPLICATION_JSON);
		MockHttpServletResponse response = mockMvc.perform(request)
				.andReturn()
				.getResponse();
		verify(userService).enableOrDisableUser("user123", true);
		assertThat(response.getStatus()).isEqualTo(200);
		assertThat(response.getContentAsByteArray())
				.isEqualTo(objectMapper.writeValueAsBytes(UserRestData.builder(user).build()));
	}

	@Test
	public void testExtendsUserExpiration() throws Exception {
		final UserEntity user = new UserEntity()
				.setExpiredAt(new Date());
		when(userService.extendsUserExpiration("user123", 60)).thenReturn(user);
		MockHttpServletRequestBuilder request = put("/api/users/user123/exp")
				.content("{\"amount\": 60}")
				.contentType(MediaType.APPLICATION_JSON);
		MockHttpServletResponse response = mockMvc.perform(request)
				.andReturn()
				.getResponse();
		verify(userService).extendsUserExpiration("user123", 60);
		assertThat(response.getStatus()).isEqualTo(200);
		assertThat(response.getContentAsByteArray())
				.isEqualTo(objectMapper.writeValueAsBytes(UserRestData.builder(user).build()));
	}

	@Test
	public void testFindRolesByUserUsername() throws Exception {
		final RoleEntity role = new RoleEntity()
				.setId("role123")
				.setCode("role123");
		final Set<RoleEntity> roles = new HashSet<>(Arrays.asList(role));
		final Set<RoleRestData> expected = roles.stream()
				.map(r -> RoleRestData.builder(r).build())
				.collect(Collectors.toSet());
		when(userService.findRolesByUserUsername("user123")).thenReturn(roles);
		MockHttpServletRequestBuilder request = get("/api/users/user123/roles")
				.contentType(MediaType.APPLICATION_JSON);
		MockHttpServletResponse response = mockMvc.perform(request)
				.andReturn()
				.getResponse();
		verify(userService).findRolesByUserUsername("user123");
		assertThat(response.getStatus()).isEqualTo(200);
		assertThat(response.getContentAsByteArray()).isEqualTo(objectMapper.writeValueAsBytes(expected));
	}

	@Test
	public void testFindUserByEmail() throws Exception {
		final UserEntity user = new UserEntity()
				.setId("user123")
				.setEmail("user@server");
		when(userService.findUserByEmail("user@server")).thenReturn(user);
		MockHttpServletRequestBuilder request = get("/api/users/user@server?email=true")
				.contentType(MediaType.APPLICATION_JSON);
		MockHttpServletResponse response = mockMvc.perform(request)
				.andReturn()
				.getResponse();
		verify(userService).findUserByEmail("user@server");
		assertThat(response.getStatus()).isEqualTo(200);
		assertThat(response.getContentAsByteArray())
				.isEqualTo(objectMapper.writeValueAsBytes(UserRestData.builder(user).build()));
	}

	@Test
	public void testFindUserByUsername() throws Exception {
		final UserEntity user = new UserEntity()
				.setId("user123")
				.setEmail("user@server");
		when(userService.findUserByUsername("user123")).thenReturn(user);
		MockHttpServletRequestBuilder request = get("/api/users/user123")
				.contentType(MediaType.APPLICATION_JSON);
		MockHttpServletResponse response = mockMvc.perform(request)
				.andReturn()
				.getResponse();
		verify(userService).findUserByUsername("user123");
		assertThat(response.getStatus()).isEqualTo(200);
		assertThat(response.getContentAsByteArray())
				.isEqualTo(objectMapper.writeValueAsBytes(UserRestData.builder(user).build()));
	}

	@Test
	public void testFindUsersByUsernameStartingWith() throws Exception {
		final UserEntity user = new UserEntity()
				.setId("user123")
				.setEmail("user@server");
		final Page<UserEntity> users = new PageImpl<>(Arrays.asList(user));
		when(userService.findUsersByUsernameStartingWith(eq("user123"), any())).thenReturn(users);
		MockHttpServletRequestBuilder request = get("/api/users?username=user123")
				.contentType(MediaType.APPLICATION_JSON);
		MockHttpServletResponse response = mockMvc.perform(request)
				.andReturn()
				.getResponse();
		verify(userService).findUsersByUsernameStartingWith(eq("user123"), any());
		assertThat(response.getStatus()).isEqualTo(200);
		assertThat(response.getContentAsByteArray())
				.isEqualTo(objectMapper.writeValueAsBytes(users.map(u -> UserRestData.builder(u).build())));
	}

	@Test
	public void testLockOrUnlockUser() throws Exception {
		final UserEntity user = new UserEntity()
				.setAccountNonLocked(true);
		when(userService.lockOrUnlockUser("user123", true)).thenReturn(user);
		MockHttpServletRequestBuilder request = put("/api/users/user123/lock")
				.content("{\"enabled\": true}")
				.contentType(MediaType.APPLICATION_JSON);
		MockHttpServletResponse response = mockMvc.perform(request)
				.andReturn()
				.getResponse();
		verify(userService).lockOrUnlockUser("user123", true);
		assertThat(response.getStatus()).isEqualTo(200);
		assertThat(response.getContentAsByteArray())
				.isEqualTo(objectMapper.writeValueAsBytes(UserRestData.builder(user).build()));
	}

	@Test
	public void testRemoveRoleFromUser() throws Exception {
		final UserEntity user = new UserEntity()
				.setId("user123");
		when(userService.removeRoleFromUser("user123", "role123")).thenReturn(user);
		MockHttpServletRequestBuilder request = delete("/api/users/user123/roles")
				.content("{\"role\": \"role123\"}")
				.contentType(MediaType.APPLICATION_JSON);
		MockHttpServletResponse response = mockMvc.perform(request)
				.andReturn()
				.getResponse();
		verify(userService).removeRoleFromUser("user123", "role123");
		assertThat(response.getStatus()).isEqualTo(200);
		assertThat(response.getContentAsByteArray())
				.isEqualTo(objectMapper.writeValueAsBytes(UserRestData.builder(user).build()));
	}

	@Test
	public void testSaveUser() throws Exception {
		final UserEntity user = new UserEntity()
				.setId("user123");
		when(userService.saveUser(any())).thenReturn(user);
		MockHttpServletRequestBuilder request = post("/api/users")
				.content("{\"username\": \"user123\"}")
				.contentType(MediaType.APPLICATION_JSON);
		MockHttpServletResponse response = mockMvc.perform(request)
				.andReturn()
				.getResponse();
		verify(userService).saveUser(any());
		assertThat(response.getStatus()).isEqualTo(200);
		assertThat(response.getContentAsByteArray())
				.isEqualTo(objectMapper.writeValueAsBytes(UserRestData.builder(user).build()));
	}

	@Test
	public void testUpdateUser() throws Exception {
		final UserEntity user = new UserEntity()
				.setId("user123");
		when(userService.updateUser(eq("user123"), any())).thenReturn(user);
		MockHttpServletRequestBuilder request = put("/api/users/user123")
				.content("{\"username\": \"user123\"}")
				.contentType(MediaType.APPLICATION_JSON);
		MockHttpServletResponse response = mockMvc.perform(request)
				.andReturn()
				.getResponse();
		verify(userService).updateUser(eq("user123"), any());
		assertThat(response.getStatus()).isEqualTo(200);
		assertThat(response.getContentAsByteArray())
				.isEqualTo(objectMapper.writeValueAsBytes(UserRestData.builder(user).build()));
	}

	@Test
	public void testUpdateUserPassword() throws Exception {
		final UserEntity user = new UserEntity()
				.setId("user123");
		when(userService.updateUserPassword(eq("user123"), any())).thenReturn(user);
		MockHttpServletRequestBuilder request = put("/api/users/user123/password")
				.content("{\"username\": \"user123\"}")
				.contentType(MediaType.APPLICATION_JSON);
		MockHttpServletResponse response = mockMvc.perform(request)
				.andReturn()
				.getResponse();
		verify(userService).updateUserPassword(eq("user123"), any());
		assertThat(response.getStatus()).isEqualTo(200);
		assertThat(response.getContentAsByteArray())
				.isEqualTo(objectMapper.writeValueAsBytes(UserRestData.builder(user).build()));
	}

}
