package org.codenergic.theskeleton.user.profile;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.codenergic.theskeleton.user.ImmutableUserOAuth2ClientApprovalRestData;
import org.codenergic.theskeleton.user.UserEntity;
import org.codenergic.theskeleton.user.UserOAuth2ClientApprovalRestData;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/profile")
public class ProfileRestController {
	private final ProfileService profileService;
	private final SessionRegistry sessionRegistry;

	public ProfileRestController(ProfileService profileService, SessionRegistry sessionRegistry) {
		this.profileService = profileService;
		this.sessionRegistry = sessionRegistry;
	}

	private ProfileRestData convertToRestData(UserEntity user) {
		return ProfileRestData.builder(user).build();
	}

	@GetMapping("/sessions")
	public List<SessionInformation> findProfileActiveSessions(Authentication authentication) {
		if (authentication == null || authentication.getPrincipal() == null)
			return Collections.emptyList();
		List<SessionInformation> sessions = new ArrayList<>();
		for (Object principal : sessionRegistry.getAllPrincipals()) {
			UserEntity user = (UserEntity) principal;
			if (!user.getUsername().equals(authentication.getName()))
				continue;
			sessions.addAll(sessionRegistry.getAllSessions(user, true));
		}
		return sessions.stream()
			.map(i -> new SessionInformation(authentication.getName(), i.getSessionId(), i.getLastRequest()))
			.collect(Collectors.toList());
	}

	@GetMapping("/connected-apps")
	public List<UserOAuth2ClientApprovalRestData> findProfileConnectedApps(Authentication authentication) {
		return profileService.findOAuth2ClientApprovalByUsername(authentication.getName())
			.stream()
			.collect(Collectors.groupingBy(e -> e.getClient().getId()))
			.values().stream()
			.map(clients -> {
				ImmutableUserOAuth2ClientApprovalRestData.Builder builder = UserOAuth2ClientApprovalRestData.builder(clients.get(0));
				clients.forEach(c -> builder.putScopeAndStatus(c.getScope(), c.getApprovalStatus()));
				return builder.build();
			})
			.collect(Collectors.toList());
	}

	@GetMapping
	public ResponseEntity<ProfileRestData> getCurrentProfile(Authentication authentication) {
		return profileService.findProfileByUsername(authentication.getName())
			.map(this::convertToRestData)
			.map(ResponseEntity::ok)
			.orElse(ResponseEntity.notFound().build());
	}

	@DeleteMapping("/connected-apps")
	public void removeProfileConnectedApps(Authentication authentication, @RequestBody String clientId) {
		profileService.removeOAuth2ClientApprovalByUsername(authentication.getName(), clientId);
	}

	@DeleteMapping("/sessions")
	public void revokeProfileSession(@RequestBody String sessionId) {
		sessionRegistry.removeSessionInformation(sessionId);
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
