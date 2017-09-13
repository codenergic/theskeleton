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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/privileges")
public class PrivilegeRestService {
	@Autowired
	private PrivilegeService privilegeService;

	@GetMapping("/{idOrName}")
	public PrivilegeRestData findPrivilegeByIdOrName(@PathVariable("idOrName") final String idOrName) {
		PrivilegeEntity privilege = privilegeService.findPrivilegeByIdOrName(idOrName);
		return privilege == null ? null : PrivilegeRestData.builder().fromPrivilegeEntity(privilege).build();
	}

	@GetMapping
	public Page<PrivilegeRestData> findPrivileges(@RequestParam(name = "q", defaultValue = "") final String keywords,
			final Pageable pageable) {
		return privilegeService.findPrivileges(keywords, pageable)
				.map(s -> PrivilegeRestData.builder().fromPrivilegeEntity(s).build());
	}
}
