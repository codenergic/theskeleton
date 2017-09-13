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

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.codenergic.theskeleton.core.data.AbstractAuditingEntity;

@Entity
@Table(name = "ts_role")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@SuppressWarnings("serial")
public class RoleEntity extends AbstractAuditingEntity {
	@NotNull
	@Column(length = 200, unique = true)
	private String code;
	@Column(length = 500)
	private String description;
	@OneToMany(mappedBy = "role")
	private Set<RolePrivilegeEntity> privileges = new HashSet<>();

	@Override
	public RoleEntity setId(String id) {
		super.setId(id);
		return this;
	}

	public String getCode() {
		return code;
	}

	public RoleEntity setCode(String code) {
		this.code = code;
		return this;
	}

	public String getDescription() {
		return description;
	}

	public RoleEntity setDescription(String description) {
		this.description = description;
		return this;
	}

	public Collection<RolePrivilegeEntity> getPrivileges() {
		return privileges;
	}

	public RoleEntity setPrivileges(Set<RolePrivilegeEntity> privileges) {
		this.privileges = privileges;
		return this;
	}
}
