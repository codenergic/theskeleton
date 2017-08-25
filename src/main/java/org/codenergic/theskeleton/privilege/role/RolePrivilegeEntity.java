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
package org.codenergic.theskeleton.privilege.role;

import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;

import org.codenergic.theskeleton.core.data.AbstractAuditingEntity;
import org.codenergic.theskeleton.privilege.PrivilegeEntity;
import org.springframework.security.core.GrantedAuthority;

@Entity
@Table(name = "ts_role_privilege", uniqueConstraints = { @UniqueConstraint(columnNames = { "role_id","privilege_id" }) })
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@SuppressWarnings("serial")
public class RolePrivilegeEntity extends AbstractAuditingEntity implements GrantedAuthority {
	@NotNull
	@ManyToOne
	@JoinColumn(name = "role_id")
	private RoleEntity role;
	@NotNull
	@ManyToOne
	@JoinColumn(name = "privilege_id")
	private PrivilegeEntity privilege;

	public RolePrivilegeEntity() {}

	public RolePrivilegeEntity(RoleEntity role, PrivilegeEntity privilege) {
		this.role = role;
		this.privilege = privilege;
	}

	@Override
	public RolePrivilegeEntity setId(String id) {
		super.setId(id);
		return this;
	}

	public RoleEntity getRole() {
		return role;
	}
	public RolePrivilegeEntity setRole(RoleEntity role) {
		this.role = role;
		return this;
	}
	public PrivilegeEntity getPrivilege() {
		return privilege;
	}
	public RolePrivilegeEntity setPrivilege(PrivilegeEntity privilege) {
		this.privilege = privilege;
		return this;
	}

	@Override
	@Transient
	public String getAuthority() {
		return privilege.getAuthority();
	}
}
