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

import java.util.Calendar;
import java.util.Optional;
import java.util.TimeZone;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;

import org.codenergic.theskeleton.client.OAuth2ClientEntity;
import org.codenergic.theskeleton.core.data.AbstractAuditingEntity;
import org.springframework.security.oauth2.provider.approval.Approval;
import org.springframework.security.oauth2.provider.approval.Approval.ApprovalStatus;

@Entity
@SuppressWarnings("serial")
@Table(name = "ts_user_oauth2_client_approval", uniqueConstraints = {
		@UniqueConstraint(columnNames = { "user_id", "oauth2_client_id", "scope" }) })
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public class UserOAuth2ClientApprovalEntity extends AbstractAuditingEntity {
	@NotNull
	@ManyToOne
	@JoinColumn(name = "user_id")
	private UserEntity user;
	@NotNull
	@ManyToOne
	@JoinColumn(name = "oauth2_client_id")
	private OAuth2ClientEntity client;
	@NotNull
	@Column(length = 200)
	private String scope;
	@NotNull
	@Column(name = "approval_status")
	@Enumerated(EnumType.ORDINAL)
	private ApprovalStatus approvalStatus = ApprovalStatus.APPROVED;

	@Override
	public UserOAuth2ClientApprovalEntity setId(String id) {
		super.setId(id);
		return this;
	}

	public UserEntity getUser() {
		return user;
	}

	public UserOAuth2ClientApprovalEntity setUser(UserEntity user) {
		this.user = user;
		return this;
	}

	public OAuth2ClientEntity getClient() {
		return client;
	}

	public UserOAuth2ClientApprovalEntity setClient(OAuth2ClientEntity client) {
		this.client = client;
		return this;
	}

	public String getScope() {
		return scope;
	}

	public UserOAuth2ClientApprovalEntity setScope(String scope) {
		this.scope = scope;
		return this;
	}

	public ApprovalStatus getApprovalStatus() {
		return approvalStatus;
	}

	public UserOAuth2ClientApprovalEntity setApprovalStatus(ApprovalStatus approvalStatus) {
		this.approvalStatus = approvalStatus;
		return this;
	}

	public Approval toApproval() {
		Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
		calendar.setTime(Optional.ofNullable(getLastModifiedDate()).orElse(getCreatedDate()));
		calendar.add(Calendar.DAY_OF_MONTH, 30);
		return new Approval(user.getId(), client.getId(), scope, calendar.getTime(), approvalStatus);
	}
}
