package org.codenergic.theskeleton.user.profile;

import java.io.InputStream;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.codenergic.theskeleton.user.UserEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/profile")
public class ProfileRestService {
	private ProfileService profileService;

	public ProfileRestService(ProfileService profileService) {
		this.profileService = profileService;
	}

	private ProfileRestData convertToRestData(UserEntity user) {
		return ProfileRestData.builder().fromUserEntity(user).build();
	}

	@GetMapping
	public ProfileRestData getCurrentProfile(Authentication authentication) {
		UserEntity user = profileService.findProfileByUsername(authentication.getName());
		return convertToRestData(user);
	}

	@PutMapping
	public ProfileRestData updateProfile(Authentication authentication,
			@RequestBody @Valid ProfileRestData profileRestData) {
		UserEntity user = profileService.updateProfile(authentication.getName(), profileRestData.toUserEntity());
		return convertToRestData(user);
	}

	@PutMapping("/password")
	public ProfileRestData updateProfilePassword(Authentication authentication, @RequestBody Map<String, String> body) {
		UserEntity user = profileService.updateProfilePassword(authentication.getName(), body.get("password"));
		return convertToRestData(user);
	}

	@PutMapping(path = "/picture", consumes = "image/*")
	public ProfileRestData updateProfilePicture(Authentication authentication, HttpServletRequest request) throws Exception {
		try (InputStream image = request.getInputStream()) {
			UserEntity user = profileService.updateProfilePicture(authentication.getName(), image, request.getContentType());
			return convertToRestData(user);
		}
	}
}
