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

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.auto.value.AutoValue;
import com.google.common.collect.ImmutableMap;
import org.codenergic.theskeleton.core.data.RestData;
import org.springframework.security.oauth2.provider.approval.Approval;

import java.util.Map;

@AutoValue
@JsonDeserialize(builder = AutoValue_UserOAuth2ClientApprovalRestData.Builder.class)
public abstract class UserOAuth2ClientApprovalRestData implements RestData {
	public static Builder builder() {
		return new AutoValue_UserOAuth2ClientApprovalRestData.Builder();
	}

	public static Builder builder(UserOAuth2ClientApprovalEntity entity) {
		return builder()
			.clientId(entity.getClient().getClientId())
			.clientName(entity.getClient().getName())
			.username(entity.getUser().getUsername());
	}

	public abstract String getClientId();

	public abstract String getClientName();

	public abstract ImmutableMap<String, Approval.ApprovalStatus> getScopeAndStatus();

	public abstract String getUsername();

	@AutoValue.Builder
	public interface Builder extends RestData.Builder {
		default Builder addScopeAndStatus(String scope, Approval.ApprovalStatus approvalStatus) {
			scopeAndStatusBuilder().put(scope, approvalStatus);
			return this;
		}

		UserOAuth2ClientApprovalRestData build();

		Builder clientId(String clientId);

		Builder clientName(String clientName);

		Builder scopeAndStatus(Map<String, Approval.ApprovalStatus> scopeAndStatus);

		ImmutableMap.Builder<String, Approval.ApprovalStatus> scopeAndStatusBuilder();

		Builder username(String username);
	}
}
