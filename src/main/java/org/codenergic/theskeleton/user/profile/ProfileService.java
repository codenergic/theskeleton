package org.codenergic.theskeleton.user.profile;

import java.io.InputStream;

import org.codenergic.theskeleton.user.UserEntity;
import org.springframework.security.access.prepost.PreAuthorize;

public interface ProfileService {
	@PreAuthorize("#username == principal.username")
	UserEntity findProfileByUsername(String username);

	@PreAuthorize("#username == principal.username")
	UserEntity updateProfile(String username, UserEntity user);

	@PreAuthorize("#username == principal.username")
	UserEntity updateProfilePassword(String username, String rawPassword);

	@PreAuthorize("#username == principal.username")
	UserEntity updateProfilePicture(String username, InputStream image, String contentType) throws Exception;
}
