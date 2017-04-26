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
package org.codenergic.theskeleton.role;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/roles")
public class RoleRestService {
	@Autowired
	private RoleService roleService;

	@RequestMapping(path = "/{code}", method = RequestMethod.GET)
	public RoleRestData findRoleByCode(@PathVariable("code") final String code) {
		RoleEntity role = roleService.findRoleByCode(code);
		return role == null ? null : RoleRestData.builder(role).build();
	}

	@RequestMapping(method = RequestMethod.GET)
	public Page<RoleRestData> findRoles(final Pageable pageable) {
		return roleService.findRoles(pageable)
				.map(s -> RoleRestData.builder(s).build());
	}

	@RequestMapping(method = RequestMethod.POST)
	public RoleRestData saveRole(@RequestBody @Valid final RoleRestData role) {
		return RoleRestData.builder(roleService.saveRole(role.toEntity()))
				.build();
	}

	@RequestMapping(path = "/{code}", method = RequestMethod.PUT)
	public RoleRestData updateRole(@PathVariable("code") String code, @RequestBody @Valid final RoleRestData role) {
		return RoleRestData.builder(roleService.updateRole(code, role.toEntity()))
				.build();
	}
}
