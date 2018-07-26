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

import org.codenergic.theskeleton.core.data.RestData;
import org.immutables.value.Value;
import org.springframework.security.oauth2.provider.approval.Approval;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.common.collect.ImmutableMap;

@Value.Immutable
@JsonDeserialize(builder = ImmutableUserOAuth2ClientApprovalRestData.Builder.class)
public interface UserOAuth2ClientApprovalRestData extends RestData {
	Approval.ApprovalStatus getApprovalStatus();

	String getClientId();

	String getClientName();

	String getScope();

	ImmutableMap<String, Approval.ApprovalStatus> getScopeAndStatus();

	String getUsername();
}
