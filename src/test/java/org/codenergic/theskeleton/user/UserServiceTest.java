package org.codenergic.theskeleton.user;

import java.io.InputStream;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.TimeZone;
import java.util.UUID;

import org.codenergic.theskeleton.client.OAuth2ClientEntity;
import org.codenergic.theskeleton.core.security.ImmutableUser;
import org.codenergic.theskeleton.core.security.User;
import org.codenergic.theskeleton.privilege.RolePrivilegeRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.provider.approval.Approval;

import io.minio.MinioClient;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class UserServiceTest {
	@Mock
	private MinioClient minioClient;
	private PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
	@Mock
	private UserOAuth2ClientApprovalRepository clientApprovalRepository;
	@Mock
	private UserRepository userRepository;
	@Mock
	private RolePrivilegeRepository rolePrivilegeRepository;
	@Mock
	private SessionRegistry sessionRegistry;
	@Mock
	private UserAuthorityService<? extends GrantedAuthority> userAuthorityService;
	private UserService userService;

	@Before
	public void init() {
		MockitoAnnotations.initMocks(this);
		this.userService = new UserServiceImpl(minioClient, passwordEncoder, userRepository, userAuthorityService, clientApprovalRepository, sessionRegistry);
	}

	@Test
	public void testDeleteUser() {
		UserEntity user = new UserEntity()
				.setId(UUID.randomUUID().toString())
				.setUsername("user")
				.setPassword(passwordEncoder.encode("user"));
		when(userRepository.findByUsername("user")).thenReturn(Optional.of(user));
		userService.deleteUser("user");
		verify(userRepository).findByUsername("user");
		verify(userRepository).delete(user);
	}

	@Test
	@SuppressWarnings("serial")
	public void testEnableOrDisableUser() {
		UserEntity input = new UserEntity() {{ setId(UUID.randomUUID().toString()); }}
				.setUsername("user")
				.setEnabled(false);
		when(userRepository.findByUsername("user")).thenReturn(Optional.of(input));
		UserEntity result = userService.enableOrDisableUser("user", true);
		assertThat(result.getId()).isEqualTo(input.getId());
		assertThat(result.isEnabled()).isTrue();
		verify(userRepository).findByUsername("user");
	}

	@Test
	public void testExtendsUserExpiration() {
		Date expiredAt = Calendar.getInstance(TimeZone.getDefault()).getTime();
		UserEntity input = new UserEntity()
				.setId(UUID.randomUUID().toString())
				.setUsername("user")
				.setExpiredAt(expiredAt);
		when(userRepository.findByUsername("user")).thenReturn(Optional.of(input));
		UserEntity result = userService.extendsUserExpiration("user", 60);
		assertThat(result.getId()).isEqualTo(input.getId());
		assertThat(result.getExpiredAt()).isAfter(expiredAt);
		assertThat((result.getExpiredAt().getTime() - expiredAt.getTime()) / 1000).isEqualTo(3600);
		verify(userRepository).findByUsername("user");
	}

	@Test
	public void testFindUserActiveSessions() {
		User user = ImmutableUser.builder().id("12345").username("username12345").build();
		when(sessionRegistry.getAllPrincipals()).thenReturn(Collections.singletonList(user));
		when(sessionRegistry.getAllSessions(user, true))
			.thenReturn(Collections.singletonList(new SessionInformation(user, "12345", new Date())));
		List<SessionInformation> activeSessions = userService.findUserActiveSessions("username12345");
		assertThat(activeSessions).hasSize(1);
		assertThat(activeSessions).first().hasFieldOrPropertyWithValue("principal", user.getUsername());
		verify(sessionRegistry).getAllPrincipals();
		verify(sessionRegistry).getAllSessions(user, true);
	}

	@Test
	public void testFindUserByEmail() {
		UserEntity dbResult = new UserEntity().setUsername("user");
		when(userRepository.findByEmail("user@localhost")).thenReturn(Optional.of(dbResult));
		UserEntity result = userService.findUserByEmail("user@localhost").orElseThrow(RuntimeException::new);
		assertThat(result).isEqualTo(dbResult);
		verify(userRepository).findByEmail("user@localhost");
	}

	@Test
	public void testFindUserOAuth2ClientApprovalByUsername() {
		final UserOAuth2ClientApprovalEntity result = new UserOAuth2ClientApprovalEntity()
			.setId("123")
			.setUser(new UserEntity().setUsername("user"))
			.setClient(new OAuth2ClientEntity().setId("123"))
			.setApprovalStatus(Approval.ApprovalStatus.APPROVED)
			.setScope("scope1");
		when(clientApprovalRepository.findByUserUsername("user"))
			.thenReturn(Collections.singletonList(result));
		List<UserOAuth2ClientApprovalEntity> approvals = userService.findUserOAuth2ClientApprovalByUsername("user");
		assertThat(approvals).isNotEmpty();
		assertThat(approvals).hasSize(1);
		assertThat(approvals.get(0)).isEqualTo(result);
		verify(clientApprovalRepository).findByUserUsername("user");
	}

	@Test
	public void testFindUsersByUsernameStartingWith() {
		Page<UserEntity> dbResult = new PageImpl<>(Collections.singletonList(new UserEntity().setUsername("user")));
		when(userRepository.findByUsernameStartingWith(eq("user"), any()))
				.thenReturn(dbResult);
		Page<UserEntity> result = userService.findUsersByUsernameStartingWith("user", null);
		assertThat(result.getNumberOfElements()).isEqualTo(1);
		assertThat(result).isEqualTo(dbResult);
		verify(userRepository).findByUsernameStartingWith(eq("user"), any());
	}

	@Test
	public void testLoadUserByUsername() {
		assertThatThrownBy(() -> userService.loadUserByUsername("123"))
			.isInstanceOf(UsernameNotFoundException.class);
		verify(userRepository).findByEmail("123");
		verify(userRepository).findById("123");

		when(userRepository.findByUsername("1234")).thenReturn(Optional.of(new UserEntity()));
		UserDetails user = userService.loadUserByUsername("1234");
		assertThat(user).isInstanceOf(UserEntity.class);
		verify(userRepository).findByUsername("1234");
		verify(userAuthorityService).getAuthorities(user);
	}

	@Test
	public void testLockOrUnlockUser() {
		UserEntity dbResult = new UserEntity().setAccountNonLocked(false);
		when(userRepository.findByUsername("user")).thenReturn(Optional.of(dbResult));
		UserEntity result = userService.lockOrUnlockUser("user", true);
		assertThat(result.isAccountNonLocked()).isEqualTo(true);
		verify(userRepository).findByUsername("user");
	}

	@Test
	public void testRemoveOAuth2ClientApprovalByUsername() {
		userService.removeUserOAuth2ClientApprovalByUsername("user", "client");
		verify(clientApprovalRepository).deleteByUserUsernameAndClientId("user", "client");
	}

	@Test
	public void testRevokeProfileSession() {
		userService.revokeUserSession("", "12345");
		verify(sessionRegistry).removeSessionInformation("12345");
	}

	@Test
	public void testSaveUser() {
		userService.saveUser(new UserEntity());
	}

	@Test
	public void testUpdateUser() {
		UserEntity input = new UserEntity()
				.setUsername("user")
				.setEnabled(false);
		when(userRepository.findByUsername("user")).thenReturn(Optional.of(input));
		UserEntity updatedUser = userService.updateUser("user", new UserEntity().setUsername("updated"));
		assertThat(updatedUser.getUsername()).isEqualTo(input.getUsername());
		verify(userRepository).findByUsername("user");
	}

	@Test
	public void testUpdateUserPassword() {
		String rawPassword = "p@$$w0rd!";
		when(userRepository.findByUsername("user")).thenReturn(Optional.of(new UserEntity()));
		UserEntity result = userService.updateUserPassword("user", rawPassword);
		assertThat(passwordEncoder.matches(rawPassword, result.getPassword())).isTrue();
		verify(userRepository).findByUsername("user");
	}

	@Test
	public void testUpdateUserPicture() throws Exception {
		final InputStream inputStream = ClassLoader.getSystemResourceAsStream("static/logo.png");
		when(userRepository.findByUsername("user")).thenReturn(Optional.of(new UserEntity()));
		userService.updateUserPicture("user", inputStream, "image/png");
		verify(userRepository).findByUsername("user");
		verify(minioClient).putObject(anyString(), anyString(), any(InputStream.class), eq("image/png"));
		verify(minioClient).getObjectUrl(anyString(), anyString());
		inputStream.close();
	}
}
