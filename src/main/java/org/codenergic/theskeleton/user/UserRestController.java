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

import java.util.Map;

import org.codenergic.theskeleton.tokenstore.TokenStoreService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
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
	private UserService userService;
	private UserAdminService userAdminService;
	private TokenStoreService tokenStoreService;

	public UserRestController(UserService userService, UserAdminService userAdminService, TokenStoreService tokenStoreService) {
		this.userService = userService;
		this.userAdminService = userAdminService;
		this.tokenStoreService = tokenStoreService;
	}

	private UserRestData convertEntityToRestData(UserEntity user) {
		return UserRestData.builder(user).build();
	}

	@DeleteMapping("/{username}")
	public void deleteUser(@PathVariable("username") String username) {
		userService.findUserByUsername(username).ifPresent(tokenStoreService::deleteTokenByUser);
		userAdminService.deleteUser(username);
	}

	@PutMapping("/{username}/enable")
	public UserRestData enableOrDisableUser(@PathVariable("username") String username, @RequestBody Map<String, Boolean> body) {
		return convertEntityToRestData(userAdminService.enableOrDisableUser(username, body.getOrDefault("enabled", true)));
	}

	@PutMapping("/{username}/exp")
	public UserRestData extendsUserExpiration(@PathVariable("username") String username, @RequestBody Map<String, Integer> body) {
		return convertEntityToRestData(userAdminService.extendsUserExpiration(username, body.getOrDefault("amount", 180)));
	}

	@GetMapping(path = "/{email}", params = { "email" })
	public ResponseEntity<UserRestData> findUserByEmail(@PathVariable("email") String email) {
		return userService.findUserByEmail(email)
			.map(this::convertEntityToRestData)
			.map(ResponseEntity::ok)
			.orElse(ResponseEntity.notFound().build());
	}

	@GetMapping(path = "/{username}")
	public ResponseEntity<UserRestData> findUserByUsername(@PathVariable("username") String username) {
		return userService.findUserByUsername(username)
			.map(this::convertEntityToRestData)
			.map(ResponseEntity::ok)
			.orElse(ResponseEntity.notFound().build());
	}

	@GetMapping
	public Page<UserRestData> findUsersByUsernameStartingWith(
			@RequestParam(name = "username", defaultValue = "") String username, Pageable pageable) {
		return userAdminService.findUsersByUsernameStartingWith(username, pageable)
				.map(this::convertEntityToRestData);
	}

	@PutMapping("/{username}/lock")
	public UserRestData lockOrUnlockUser(@PathVariable("username") String username, @RequestBody Map<String, Boolean> body) {
		return convertEntityToRestData(userAdminService.lockOrUnlockUser(username, body.getOrDefault("unlocked", true)));
	}

	@PostMapping
	public UserRestData saveUser(@RequestBody @Validated(UserRestData.New.class) UserRestData userData) {
		return convertEntityToRestData(userAdminService.saveUser(userData.toUserEntity()));
	}

	@PutMapping("/{username}")
	public UserRestData updateUser(@PathVariable("username") String username, @RequestBody @Validated(UserRestData.Existing.class) UserRestData userData) {
		return convertEntityToRestData(userAdminService.updateUser(username, userData.toUserEntity()));
	}

	@PutMapping("/{username}/password")
	public UserRestData updateUserPassword(@PathVariable("username") String username, @RequestBody Map<String, String> body) {
		return convertEntityToRestData(userAdminService.updateUserPassword(username, body.get("password")));
	}
}
