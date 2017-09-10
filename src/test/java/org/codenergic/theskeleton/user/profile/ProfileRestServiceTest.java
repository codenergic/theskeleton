package org.codenergic.theskeleton.user.profile;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.codenergic.theskeleton.user.UserEntity;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.minio.MinioClient;

@RunWith(SpringRunner.class)
@WebMvcTest(controllers = { ProfileRestService.class }, secure = true)
public class ProfileRestServiceTest {
	@Autowired
	private MockMvc mockMvc;
	@Autowired
	private ObjectMapper objectMapper;
	@Mock
	private MinioClient minioClient;
	@MockBean
	private ProfileService profileService;

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
	public void testFindUserByUsername() throws Exception {
		final UserEntity user = new UserEntity()
				.setId("user123")
				.setEmail("user@server");
		when(profileService.findProfileByUsername("user123")).thenReturn(user);
		MockHttpServletRequestBuilder request = get("/api/profile")
				.contentType(MediaType.APPLICATION_JSON)
				.with(user("user123"));
		MockHttpServletResponse response = mockMvc.perform(request)
				.andReturn()
				.getResponse();
		assertThat(response.getStatus()).isEqualTo(200);
		assertThat(response.getContentAsByteArray())
				.isEqualTo(objectMapper.writeValueAsBytes(ProfileRestData.builder().fromUserEntity(user).build()));
		verify(profileService).findProfileByUsername("user123");
	}

	@Test
	public void testUpdateProfile() throws Exception {
		final UserEntity user = new UserEntity()
				.setId("user125");
		when(profileService.updateProfile(eq("user123"), any())).thenReturn(user);
		MockHttpServletRequestBuilder request = put("/api/profile")
				.content("{\"username\": \"user1234\"}")
				.contentType(MediaType.APPLICATION_JSON)
				.with(user("user123"));
		MockHttpServletResponse response = mockMvc.perform(request)
				.andReturn()
				.getResponse();
		assertThat(response.getStatus()).isEqualTo(200);
		assertThat(response.getContentAsByteArray())
				.isEqualTo(objectMapper.writeValueAsBytes(ProfileRestData.builder().fromUserEntity(user).build()));
		verify(profileService).updateProfile(eq("user123"), any());
	}

	@Test
	public void testUpdateProfilePassword() throws Exception {
		final UserEntity user = new UserEntity()
				.setId("user123");
		when(profileService.updateProfilePassword(eq("user123"), any())).thenReturn(user);
		MockHttpServletRequestBuilder request = put("/api/profile/password")
				.content("{\"username\": \"user123\"}")
				.contentType(MediaType.APPLICATION_JSON)
				.with(user("user123"));
		MockHttpServletResponse response = mockMvc.perform(request)
				.andReturn()
				.getResponse();
		assertThat(response.getStatus()).isEqualTo(200);
		assertThat(response.getContentAsByteArray())
				.isEqualTo(objectMapper.writeValueAsBytes(ProfileRestData.builder().fromUserEntity(user).build()));
		verify(profileService).updateProfilePassword(eq("user123"), any());
	}

	@Test
	public void testUpdateProfilePicture() throws Exception {
		final UserEntity user = new UserEntity()
				.setId("user123");
		when(profileService.updateProfilePicture(eq("user123"), any(), eq("image/png"))).thenReturn(user);
		InputStream image = ClassLoader.getSystemResourceAsStream("static/logo.png");
		MockHttpServletRequestBuilder request = put("/api/profile/picture")
				.content(IOUtils.toByteArray(image))
				.contentType(MediaType.IMAGE_PNG)
				.with(user("user123"));
		MockHttpServletResponse response = mockMvc.perform(request)
				.andReturn()
				.getResponse();
		assertThat(response.getStatus()).isEqualTo(200);
		assertThat(response.getContentAsByteArray())
				.isEqualTo(objectMapper.writeValueAsBytes(ProfileRestData.builder().fromUserEntity(user).build()));
		verify(profileService).updateProfilePicture(eq("user123"), any(), eq("image/png"));
		image.close();
	}
}
