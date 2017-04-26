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

import javax.validation.Valid;

import org.codenergic.theskeleton.role.RoleEntity;
import org.springframework.beans.factory.annotation.Autowired;
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
	@Autowired
	private UserService userService;

	@PutMapping("/{username}/roles")
	public UserEntity addRoleToUser(@PathVariable("username") String username, @RequestBody Map<String, String> body) {
		return userService.addRoleToUser(username, body.get("role"));
	}

	@PutMapping("/{username}/enable")
	public UserEntity enableOrDisableUser(@PathVariable("username") String username, @RequestBody Map<String, Boolean> body) {
		return userService.enableOrDisableUser(username, body.getOrDefault("enabled", true));
	}

	@PutMapping("/{username}/exp")
	public UserEntity extendsUserExpiration(@PathVariable("username") String username, @RequestBody Map<String, Integer> body) {
		return userService.extendsUserExpiration(username, body.getOrDefault("amount", 180));
	}

	@GetMapping("/{username}/roles")
	public Set<RoleEntity> findRolesByUserUsername(@PathVariable("username") String username) {
		return userService.findRolesByUserUsername(username);
	}

	@GetMapping(path = "/{email}", params = { "email" })
	public UserEntity findUserByEmail(@PathVariable("email") String email) {
		return userService.findUserByEmail(email);
	}

	@GetMapping(path = "/{username}")
	public UserEntity findUserByUsername(String username) {
		return userService.findUserByUsername(username);
	}

	@GetMapping(params = { "role" })
	public Set<UserEntity> findUsersByRoleCode(@RequestParam("role") String code) {
		return userService.findUsersByRoleCode(code);
	}

	@GetMapping(params = { "username" })
	public Page<UserEntity> findUsersByUsernameStartingWith(@RequestParam("role") String username, Pageable pageable) {
		return userService.findUsersByUsernameStartingWith(username, pageable);
	}

	@PutMapping("/{username}/lock")
	public UserEntity lockOrUnlockUser(@PathVariable("username") String username, @RequestBody Map<String, Boolean> body) {
		return userService.lockOrUnlockUser(username, body.getOrDefault("unlocked", true));
	}

	@DeleteMapping("/{username}/roles")
	public UserEntity removeRoleFromUser(@PathVariable("username") String username, @RequestBody Map<String, String> body) {
		return userService.removeRoleFromUser(username, body.get("role"));
	}

	@PostMapping
	public UserEntity saveUser(@RequestBody @Valid UserRestData userData) {
		return userService.saveUser(userData.toEntity());
	}

	@PutMapping("/{username}")
	public UserEntity updateUser(@PathVariable("username") String username, @RequestBody @Valid UserRestData userData) {
		return userService.updateUser(username, userData.toEntity());
	}

	@PutMapping("/{username}/password")
	public UserEntity updateUserPassword(@PathVariable("username") String username, @RequestBody Map<String, String> body) {
		return userService.updateUserPassword(username, body.get("password"));
	}
}
