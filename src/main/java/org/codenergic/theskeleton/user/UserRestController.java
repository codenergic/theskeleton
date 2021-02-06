/*
 * Copyright 2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.codenergic.theskeleton.user;

import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.codenergic.theskeleton.core.security.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.oauth2.provider.approval.Approval;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
public class UserRestController {
	private final UserService userService;
	private final UserMapper userMapper = UserMapper.newInstance();
	private final UserOAuth2ClientApprovalMapper clientApprovalMapper = UserOAuth2ClientApprovalMapper.newInstance();

	public UserRestController(UserService userService) {
		this.userService = userService;
	}

	@DeleteMapping("/{username}")
	public void deleteUser(@User.Inject User user) {
		userService.deleteUser(user.getUsername());
	}

	@PutMapping("/{username}/enable")
	public UserRestData enableOrDisableUser(@PathVariable("username") String username, @RequestBody Map<String, Boolean> body) {
		return userMapper.toUserData(userService.enableOrDisableUser(username, body.getOrDefault("enabled", true)));
	}

	@PutMapping("/{username}/exp")
	public UserRestData extendsUserExpiration(@PathVariable("username") String username, @RequestBody Map<String, Integer> body) {
		return userMapper.toUserData(userService.extendsUserExpiration(username, body.getOrDefault("amount", 180)));
	}

	@GetMapping("/{username}/sessions")
	public List<SessionInformation> findUserActiveSessions(@User.Inject User user) {
		return userService.findUserActiveSessions(user.getUsername());
	}

	@GetMapping("/{username}/connected-apps")
	public List<UserOAuth2ClientApprovalRestData> findUserConnectedApps(@User.Inject User user) {
		return userService.findUserOAuth2ClientApprovalByUsername(user.getUsername())
			.stream()
			.collect(Collectors.groupingBy(e -> e.getClient().getId()))
			.values().stream()
			.filter(clients -> !clients.isEmpty())
			.map(clients -> {
				Map<String, Approval.ApprovalStatus> scopeAndStatus = clients.stream()
					.collect(Collectors.toMap(UserOAuth2ClientApprovalEntity::getScope, UserOAuth2ClientApprovalEntity::getApprovalStatus));
				return clientApprovalMapper.toUserOAuth2ClientApprovalData(clients.get(0), scopeAndStatus);
			})
			.collect(Collectors.toList());
	}

	@GetMapping(path = "/{email}", params = { "email" })
	public ResponseEntity<UserRestData> findUserByEmail(@PathVariable("email") String email) {
		return userService.findUserByEmail(email)
			.map(userMapper::toUserData)
			.map(ResponseEntity::ok)
			.orElse(ResponseEntity.notFound().build());
	}

	@GetMapping(path = "/{username}")
	public ResponseEntity<UserRestData> findUserByUsername(@User.Inject User user) {
		return userService.findUserByUsername(user.getUsername())
			.map(userMapper::toUserData)
			.map(ResponseEntity::ok)
			.orElse(ResponseEntity.notFound().build());
	}

	@GetMapping
	public Page<UserRestData> findUsersByUsernameStartingWith(
			@RequestParam(name = "username", defaultValue = "") String username, Pageable pageable) {
		return userService.findUsersByUsernameStartingWith(username, pageable)
				.map(userMapper::toUserData);
	}

	@PutMapping("/{username}/lock")
	public UserRestData lockOrUnlockUser(@PathVariable("username") String username, @RequestBody Map<String, Boolean> body) {
		return userMapper.toUserData(userService.lockOrUnlockUser(username, body.getOrDefault("unlocked", true)));
	}

	@DeleteMapping("/{username}/connected-apps")
	public void removeUserConnectedApps(@User.Inject User user, @RequestBody String clientId) {
		userService.removeUserOAuth2ClientApprovalByUsername(user.getUsername(), clientId);
	}

	@DeleteMapping("/{username}/sessions")
	public void revokeUserSession(@User.Inject User user, @RequestBody String sessionId) {
		userService.revokeUserSession(user.getUsername(), sessionId);
	}

	@PostMapping
	public UserRestData saveUser(@RequestBody @Validated(UserRestData.New.class) UserRestData userData) {
		return userMapper.toUserData(userService.saveUser(userMapper.toUser(userData)));
	}

	@PutMapping("/{username}")
	public UserRestData updateUser(@User.Inject User user, @RequestBody @Validated(UserRestData.Existing.class) UserRestData userData) {
		return userMapper.toUserData(userService.updateUser(user.getUsername(), userMapper.toUser(userData)));
	}

	@PutMapping("/{username}/password")
	public UserRestData updateUserPassword(@User.Inject User user, @RequestBody Map<String, String> body) {
		return userMapper.toUserData(userService.updateUserPassword(user.getUsername(), body.get("password")));
	}

	@PutMapping(path = "/{username}/picture", consumes = "image/*")
	public UserRestData updateUserPicture(@User.Inject User user, HttpServletRequest request) throws Exception {
		try (InputStream image = request.getInputStream()) {
			UserEntity u = userService.updateUserPicture(user.getUsername(), image, request.getContentType(), request.getContentLengthLong());
			return userMapper.toUserData(u);
		}
	}
}
