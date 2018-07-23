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
package org.codenergic.theskeleton.privilege;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.codenergic.theskeleton.role.RoleMapper;
import org.codenergic.theskeleton.role.RoleRestData;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/roles/{code}/privileges")
public class RolePrivilegeRestController {
	private final PrivilegeService privilegeService;
	private final PrivilegeMapper privilegeMapper = PrivilegeMapper.newInstance();
	private final RoleMapper roleMapper = RoleMapper.newInstance();

	public RolePrivilegeRestController(PrivilegeService privilegeService) {
		this.privilegeService = privilegeService;
	}

	@PutMapping
	public RoleRestData addPrivilegeToRole(@PathVariable("code") String code, @RequestBody Map<String, String> body) {
		return roleMapper.toRoleData(privilegeService.addPrivilegeToRole(code, body.get("privilege")));
	}

	@GetMapping
	public Set<PrivilegeRestData> findPrivilegesByRoleCode(@PathVariable("code") String code) {
		return privilegeService.findPrivilegesByRoleCode(code).stream()
			.map(privilegeMapper::toPrivilegeData)
			.collect(Collectors.toSet());
	}

	@DeleteMapping
	public RoleRestData removePrivilegeFromRole(@PathVariable("code") String code, @RequestBody Map<String, String> body) {
		return roleMapper.toRoleData(privilegeService.removePrivilegeFromRole(code, body.get("privilege")));
	}
}
