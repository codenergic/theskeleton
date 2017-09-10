package org.codenergic.theskeleton.user.profile.impl;

import java.io.InputStream;
import java.time.Instant;
import java.util.UUID;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.codenergic.theskeleton.user.UserEntity;
import org.codenergic.theskeleton.user.UserRepository;
import org.codenergic.theskeleton.user.profile.ProfileService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.minio.MinioClient;

@Service
@Transactional
public class ProfileServiceImpl implements ProfileService {
	private static final String PICTURE_BUCKET_NAME = "profile.pictures";
	private final Logger logger = LoggerFactory.getLogger(getClass());
	private UserRepository userRepository;
	private MinioClient minioClient;
	private PasswordEncoder passwordEncoder;

	public ProfileServiceImpl(UserRepository userRepository, MinioClient minioClient, PasswordEncoder passwordEncoder) {
		this.userRepository = userRepository;
		this.minioClient = minioClient;
		this.passwordEncoder = passwordEncoder;
	}

	@Autowired
	public void createBucketIfNotExists(ScheduledExecutorService executorService) {
		executorService.schedule((Runnable) () -> {
			try {
				logger.info("Checking bucket: {}", PICTURE_BUCKET_NAME);
				if (minioClient.bucketExists(PICTURE_BUCKET_NAME))
					return;
				logger.info("Bucket doesn't exist, creating one");
				minioClient.makeBucket(PICTURE_BUCKET_NAME);
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
			} finally {
				logger.info("Bucket successfully created");
			}
		}, 5, TimeUnit.SECONDS);
	}

	@Override
	@Transactional(readOnly = true)
	public UserEntity findProfileByUsername(String username) {
		return userRepository.findByUsername(username);
	}

	@Override
	public UserEntity updateProfile(String username, final UserEntity newUser) {
		return findProfileByUsername(username)
				.setUsername(newUser.getUsername())
				.setEmail(newUser.getEmail())
				.setPhoneNumber(newUser.getPhoneNumber());
	}

	@Override
	public UserEntity updateProfilePassword(String username, String rawPassword) {
		return findProfileByUsername(username)
				.setPassword(passwordEncoder.encode(rawPassword));
	}

	@Override
	public UserEntity updateProfilePicture(String username, InputStream image, String contentType) throws Exception {
		UserEntity user = findProfileByUsername(username);
		String imageObjectName = StringUtils.join(user.getId(), "-", Long.toHexString(Instant.now().toEpochMilli()),
				"-", UUID.randomUUID().toString());
		minioClient.putObject(PICTURE_BUCKET_NAME, imageObjectName, image, contentType);
		user.setPictureUrl(minioClient.getObjectUrl(PICTURE_BUCKET_NAME, imageObjectName));
		return user;
	}

}
