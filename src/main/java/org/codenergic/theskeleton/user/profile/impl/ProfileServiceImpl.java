package org.codenergic.theskeleton.user.profile.impl;

import io.minio.MinioClient;
import org.apache.commons.lang3.StringUtils;
import org.codenergic.theskeleton.user.UserEntity;
import org.codenergic.theskeleton.user.UserOAuth2ClientApprovalEntity;
import org.codenergic.theskeleton.user.UserOAuth2ClientApprovalRepository;
import org.codenergic.theskeleton.user.UserRepository;
import org.codenergic.theskeleton.user.profile.ProfileService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStream;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class ProfileServiceImpl implements ProfileService {
	private static final String PICTURE_BUCKET_NAME = "profile-pictures";
	private final UserRepository userRepository;
	private final MinioClient minioClient;
	private final PasswordEncoder passwordEncoder;
	private final UserOAuth2ClientApprovalRepository clientApprovalRepository;

	public ProfileServiceImpl(UserOAuth2ClientApprovalRepository approvalRepository, UserRepository userRepository, MinioClient minioClient, PasswordEncoder passwordEncoder) {
		this.clientApprovalRepository = approvalRepository;
		this.userRepository = userRepository;
		this.minioClient = minioClient;
		this.passwordEncoder = passwordEncoder;
	}

	@Override
	public List<UserOAuth2ClientApprovalEntity> findOAuth2ClientApprovalByUsername(String username) {
		return clientApprovalRepository.findByUserUsername(username);
	}

	@Override
	@Transactional(readOnly = true)
	public Optional<UserEntity> findProfileByUsername(String username) {
		return userRepository.findByUsername(username);
	}

	@Override
	public void removeOAuth2ClientApprovalByUsername(String username, String clientId) {
		clientApprovalRepository.deleteByUserUsernameAndClientId(username, clientId);
	}

	@Override
	public UserEntity updateProfile(String username, final UserEntity newUser) {
		return findProfileByUsername(username)
			.map(user -> user
				.setUsername(newUser.getUsername())
				.setEmail(newUser.getEmail())
				.setPhoneNumber(newUser.getPhoneNumber()))
			.orElseThrow(() -> new UsernameNotFoundException(username));
	}

	@Override
	public UserEntity updateProfilePassword(String username, String rawPassword) {
		return findProfileByUsername(username)
			.map(user -> user.setPassword(passwordEncoder.encode(rawPassword)))
			.orElseThrow(() -> new UsernameNotFoundException(username));
	}

	@Override
	public UserEntity updateProfilePicture(String username, InputStream image, String contentType) throws Exception {
		UserEntity user = findProfileByUsername(username)
			.orElseThrow(() -> new UsernameNotFoundException(username));
		String imageObjectName = StringUtils.join(user.getId(), "/", Long.toHexString(Instant.now().toEpochMilli()),
			"-", UUID.randomUUID().toString());
		minioClient.putObject(PICTURE_BUCKET_NAME, imageObjectName, image, contentType);
		user.setPictureUrl(minioClient.getObjectUrl(PICTURE_BUCKET_NAME, imageObjectName));
		return user;
	}

}
