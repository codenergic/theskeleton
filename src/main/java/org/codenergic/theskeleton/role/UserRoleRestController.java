/*
 * Copyright 2018 original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.codenergic.theskeleton.role;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.codenergic.theskeleton.core.security.User;
import org.codenergic.theskeleton.user.UserMapper;
import org.codenergic.theskeleton.user.UserRestData;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users/{username}/roles")
public class UserRoleRestController {
	private final RoleService roleService;
	private final RoleMapper roleMapper = RoleMapper.newInstance();
	private final UserMapper userMapper = UserMapper.newInstance();

	public UserRoleRestController(RoleService roleService) {
		this.roleService = roleService;
	}

	@PutMapping
	public UserRestData addRoleToUser(@User.Inject User user, @RequestBody Map<String, String> body) {
		return userMapper.toUserData(roleService.addRoleToUser(user.getUsername(), body.get("role")));
	}

	@GetMapping
	public Set<RoleRestData> findRolesByUserUsername(@User.Inject User user) {
		return roleService.findRolesByUserUsername(user.getUsername()).stream()
			.map(roleMapper::toRoleData)
			.collect(Collectors.toSet());
	}

	@DeleteMapping
	public UserRestData removeRoleFromUser(@User.Inject User user, @RequestBody Map<String, String> body) {
		return userMapper.toUserData(roleService.removeRoleFromUser(user.getUsername(), body.get("role")));
	}
}
