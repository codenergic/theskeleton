package org.codenergic.theskeleton.user.profile;

import org.codenergic.theskeleton.user.UserEntity;
import org.codenergic.theskeleton.user.UserOAuth2ClientApprovalEntity;
import org.springframework.security.access.prepost.PreAuthorize;

import java.io.InputStream;
import java.util.List;

public interface ProfileService {
	@PreAuthorize("#username == principal.username")
	List<UserOAuth2ClientApprovalEntity> findOAuth2ClientApprovalByUsername(String username);

	@PreAuthorize("#username == principal.username")
	UserEntity findProfileByUsername(String username);

	@PreAuthorize("#username == principal.username")
	void removeOAuth2ClientApprovalByUsername(String username, String clientId);

	@PreAuthorize("#username == principal.username")
	UserEntity updateProfile(String username, UserEntity user);

	@PreAuthorize("#username == principal.username")
	UserEntity updateProfilePassword(String username, String rawPassword);

	@PreAuthorize("#username == principal.username")
	UserEntity updateProfilePicture(String username, InputStream image, String contentType) throws Exception;
}
