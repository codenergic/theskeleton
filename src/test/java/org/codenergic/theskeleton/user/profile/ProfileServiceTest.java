package org.codenergic.theskeleton.user.profile;

import io.minio.MinioClient;
import org.codenergic.theskeleton.client.OAuth2ClientEntity;
import org.codenergic.theskeleton.user.UserEntity;
import org.codenergic.theskeleton.user.UserOAuth2ClientApprovalEntity;
import org.codenergic.theskeleton.user.UserOAuth2ClientApprovalRepository;
import org.codenergic.theskeleton.user.UserRepository;
import org.codenergic.theskeleton.user.profile.impl.ProfileServiceImpl;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.provider.approval.Approval;

import java.io.InputStream;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ProfileServiceTest {
	@Mock
	private UserOAuth2ClientApprovalRepository approvalRepository;
	@Mock
	private MinioClient minioClient;
	private PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
	private ProfileServiceImpl profileService;
	@Mock
	private UserRepository userRepository;

	@Before
	public void init() {
		MockitoAnnotations.initMocks(this);
		this.profileService = new ProfileServiceImpl(approvalRepository, userRepository, minioClient, passwordEncoder);
	}

	@Test
	public void testFindOAuth2ClientApprovalByUsername() throws Exception {
		final UserOAuth2ClientApprovalEntity result = new UserOAuth2ClientApprovalEntity()
			.setId("123")
			.setUser(new UserEntity().setUsername("user"))
			.setClient(new OAuth2ClientEntity().setId("123"))
			.setApprovalStatus(Approval.ApprovalStatus.APPROVED)
			.setScope("scope1");
		when(approvalRepository.findByUserUsername("user"))
			.thenReturn(Collections.singletonList(result));
		List<UserOAuth2ClientApprovalEntity> approvals = profileService.findOAuth2ClientApprovalByUsername("user");
		assertThat(approvals).isNotEmpty();
		assertThat(approvals).hasSize(1);
		assertThat(approvals.get(0)).isEqualTo(result);
		verify(approvalRepository).findByUserUsername("user");
	}

	@Test
	public void testRemoveOAuth2ClientApprovalByUsername() {
		profileService.removeOAuth2ClientApprovalByUsername("user", "client");
		verify(approvalRepository).deleteByUserUsernameAndClientId("user", "client");
	}

	@Test
	public void testUpdateProfile() {
		when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(new UserEntity()));
		profileService.updateProfile("username", new UserEntity());
		verify(userRepository).findByUsername("username");
	}

	@Test
	public void testUpdateProfilePassword() {
		final String rawPassword = "p@$$w0rd!";
		when(userRepository.findByUsername("user")).thenReturn(Optional.of(new UserEntity()));
		final UserEntity result = profileService.updateProfilePassword("user", rawPassword);
		assertThat(passwordEncoder.matches(rawPassword, result.getPassword())).isTrue();
		verify(userRepository).findByUsername("user");
	}

	@Test
	public void testUpdateProfilePicture() throws Exception {
		final InputStream inputStream = ClassLoader.getSystemResourceAsStream("static/logo.png");
		when(userRepository.findByUsername("user")).thenReturn(Optional.of(new UserEntity()));
		profileService.updateProfilePicture("user", inputStream, "image/png");
		verify(userRepository).findByUsername("user");
		verify(minioClient).putObject(anyString(), anyString(), any(InputStream.class), eq("image/png"));
		verify(minioClient).getObjectUrl(anyString(), anyString());
		inputStream.close();
	}

	@Test
	public void testUpdateUser() {
		final UserEntity input = new UserEntity()
				.setUsername("user")
				.setEnabled(false);
		when(userRepository.findByUsername("user")).thenReturn(Optional.of(input));
		UserEntity updatedUser = profileService.updateProfile("user", new UserEntity().setUsername("updated"));
		assertThat(updatedUser.getUsername()).isEqualTo(input.getUsername());
		verify(userRepository).findByUsername("user");
	}
}
