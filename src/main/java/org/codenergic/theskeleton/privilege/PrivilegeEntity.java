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

import org.codenergic.theskeleton.core.data.AbstractAuditingEntity;
import org.springframework.security.core.GrantedAuthority;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "ts_privilege")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@SuppressWarnings("serial")
public class PrivilegeEntity extends AbstractAuditingEntity implements GrantedAuthority {
	@NotNull
	@Column(length = 200, unique = true)
	private String name;
	@Column(length = 500)
	private String description;

	@Override
	public PrivilegeEntity setId(String id) {
		super.setId(id);
		return this;
	}

	public String getName() {
		return name;
	}
	public PrivilegeEntity setName(String name) {
		this.name = name;
		return this;
	}
	public String getDescription() {
		return description;
	}
	public PrivilegeEntity setDescription(String description) {
		this.description = description;
		return this;
	}

	@Override
	@Transient
	public String getAuthority() {
		return getName();
	}
}
