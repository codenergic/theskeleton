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
package org.codenergic.theskeleton.privilege;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/privileges")
public class PrivilegeRestController {
	private final PrivilegeService privilegeService;

	public PrivilegeRestController(PrivilegeService privilegeService) {
		this.privilegeService = privilegeService;
	}

	@GetMapping("/{idOrName}")
	public ResponseEntity<PrivilegeRestData> findPrivilegeByIdOrName(@PathVariable("idOrName") final String idOrName) {
		return privilegeService.findPrivilegeByIdOrName(idOrName)
			.<PrivilegeRestData>map(privilege -> PrivilegeRestData.builder(privilege).build())
			.map(ResponseEntity::ok)
			.orElse(ResponseEntity.notFound().build());
	}

	@GetMapping
	public Page<PrivilegeRestData> findPrivileges(@RequestParam(name = "q", defaultValue = "") final String keywords,
			final Pageable pageable) {
		return privilegeService.findPrivileges(keywords, pageable)
				.map(s -> PrivilegeRestData.builder(s).build());
	}
}
