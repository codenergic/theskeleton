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

import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import org.codenergic.theskeleton.core.data.AbstractAuditingEntity;
import org.codenergic.theskeleton.core.security.User;
import org.springframework.security.core.GrantedAuthority;

@Entity
@Table(name = "ts_user")
@SuppressWarnings("serial")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public class UserEntity extends AbstractAuditingEntity implements User {
	@NotNull
	@Column(name = "account_non_locked")
	private boolean accountNonLocked = false;
	@NotNull
	@Column(name = "credentials_non_expired")
	private boolean credentialsNonExpired = false;
	@NotNull
	@Column(length = 500, unique = true)
	private String email;
	@NotNull
	private boolean enabled = false;
	@Column(name = "expired_at")
	@Temporal(TemporalType.TIMESTAMP)
	private Date expiredAt;
	@NotNull
	@Column
	@Lob
	private String password;
	@Column(name = "phone_number")
	private String phoneNumber;
	@NotNull
	@Column(unique = true)
	private String username;
	@Transient
	private Set<GrantedAuthority> authorities = Collections.emptySet();
	@Column(name = "picture_url")
	private String pictureUrl;

	public UserEntity(UserEntity other) {
		this.setId(other.getId());
		this.accountNonLocked = other.accountNonLocked;
		this.credentialsNonExpired = other.credentialsNonExpired;
		this.email = other.email;
		this.enabled = other.enabled;
		this.expiredAt = other.expiredAt;
		this.password = other.password;
		this.phoneNumber = other.phoneNumber;
		this.username = other.username;
		this.authorities = other.authorities;
		this.pictureUrl = other.pictureUrl;
	}

	public UserEntity() {
	}

	@Override
	public Collection<GrantedAuthority> getAuthorities() {
		return authorities;
	}

	public UserEntity setAuthorities(Set<GrantedAuthority> authorities) {
		this.authorities = authorities;
		return this;
	}

	public String getEmail() {
		return email;
	}

	public UserEntity setEmail(String email) {
		this.email = email;
		return this;
	}

	public Date getExpiredAt() {
		return expiredAt;
	}

	public UserEntity setExpiredAt(Date expiredAt) {
		this.expiredAt = expiredAt;
		return this;
	}

	@Override
	public String getPassword() {
		return password;
	}

	public UserEntity setPassword(String password) {
		this.password = password;
		return this;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public UserEntity setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
		return this;
	}

	public String getPictureUrl() {
		return pictureUrl;
	}

	public UserEntity setPictureUrl(String pictureUrl) {
		this.pictureUrl = pictureUrl;
		return this;
	}

	@Override
	public String getUsername() {
		return username;
	}

	public UserEntity setUsername(String username) {
		this.username = username;
		return this;
	}

	@Override
	@Transient
	public boolean isAccountNonExpired() {
		return expiredAt == null || expiredAt.after(getCreatedDate());
	}

	@Override
	public boolean isAccountNonLocked() {
		return accountNonLocked;
	}

	public UserEntity setAccountNonLocked(boolean accountNonLocked) {
		this.accountNonLocked = accountNonLocked;
		return this;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return credentialsNonExpired;
	}

	public UserEntity setCredentialsNonExpired(boolean credentialsNonExpired) {
		this.credentialsNonExpired = credentialsNonExpired;
		return this;
	}

	@Override
	public boolean isEnabled() {
		return enabled;
	}

	public UserEntity setEnabled(boolean enabled) {
		this.enabled = enabled;
		return this;
	}

	@Override
	public UserEntity setId(String id) {
		super.setId(id);
		return this;
	}
}
