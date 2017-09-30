package org.codenergic.theskeleton.user.profile;

import org.codenergic.theskeleton.user.UserEntity;
import org.codenergic.theskeleton.user.UserOAuth2ClientApprovalRestData;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

	@GetMapping("/connected-apps")
	public List<UserOAuth2ClientApprovalRestData> findProfileConnectedApps(Authentication authentication) {
		return profileService.findOAuth2ClientApprovalByUsername(authentication.getName())
			.stream()
			.collect(Collectors.groupingBy(e -> e.getClient().getId()))
			.values().stream()
			.map(clients -> {
				UserOAuth2ClientApprovalRestData.Builder builder = UserOAuth2ClientApprovalRestData.builder()
					.fromUserOAuth2ClientApprovalEntity(clients.get(0));
				clients.forEach(c -> builder.addScopeAndStatus(c.getScope(), c.getApprovalStatus()));
				return builder.build();
			})
			.collect(Collectors.toList());
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
