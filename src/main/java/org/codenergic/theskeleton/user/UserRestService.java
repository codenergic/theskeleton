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
import java.util.Set;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.codenergic.theskeleton.privilege.role.RoleRestData;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
public class UserRestService {
	private UserService userService;
	private UserAdminService userAdminService;

	public UserRestService(UserService userService, UserAdminService userAdminService) {
		this.userService = userService;
		this.userAdminService = userAdminService;
	}

	@PutMapping("/{username}/roles")
	public UserRestData addRoleToUser(@PathVariable("username") String username, @RequestBody Map<String, String> body) {
		return convertEntityToRestData(userAdminService.addRoleToUser(username, body.get("role")));
	}

	@DeleteMapping("/{username}")
	public void deleteUser(@PathVariable("username") String username) {
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

	@GetMapping("/{username}/roles")
	public Set<RoleRestData> findRolesByUserUsername(@PathVariable("username") String username) {
		return userAdminService.findRolesByUserUsername(username).stream()
				.map(r -> RoleRestData.builder(r).build())
				.collect(Collectors.toSet());
	}

	@GetMapping(path = "/{email}", params = { "email" })
	public UserRestData findUserByEmail(@PathVariable("email") String email) {
		return convertEntityToRestData(userService.findUserByEmail(email));
	}

	@GetMapping(path = "/{username}")
	public UserRestData findUserByUsername(@PathVariable("username") String username) {
		return convertEntityToRestData(userService.findUserByUsername(username));
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

	@DeleteMapping("/{username}/roles")
	public UserRestData removeRoleFromUser(@PathVariable("username") String username, @RequestBody Map<String, String> body) {
		return convertEntityToRestData(userAdminService.removeRoleFromUser(username, body.get("role")));
	}

	@PostMapping
	public UserRestData saveUser(@RequestBody @Valid UserRestData userData) {
		return convertEntityToRestData(userAdminService.saveUser(userData.toUserEntity()));
	}

	@PutMapping("/{username}")
	public UserRestData updateUser(@PathVariable("username") String username, @RequestBody @Valid UserRestData userData) {
		return convertEntityToRestData(userAdminService.updateUser(username, userData.toUserEntity()));
	}

	@PutMapping("/{username}/password")
	public UserRestData updateUserPassword(@PathVariable("username") String username, @RequestBody Map<String, String> body) {
		return convertEntityToRestData(userAdminService.updateUserPassword(username, body.get("password")));
	}

	private UserRestData convertEntityToRestData(UserEntity user) {
		return UserRestData.builder().fromUserEntity(user).build();
	}
}
