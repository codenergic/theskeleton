/*
 * Copyright 2018 original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.codenergic.theskeleton.user;

import java.util.Map;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import org.springframework.security.oauth2.provider.approval.Approval;

@Mapper
public interface UserOAuth2ClientApprovalMapper {
	static UserOAuth2ClientApprovalMapper newInstance() {
		return Mappers.getMapper(UserOAuth2ClientApprovalMapper.class);
	}

	@Mapping(target = "clientId", source = "userClientApproval.client.id")
	@Mapping(target = "clientName", source = "userClientApproval.client.name")
	@Mapping(target = "username", source = "userClientApproval.user.username")
	@Mapping(target = "scopeAndStatus", source = "scopeAndStatus")
	UserOAuth2ClientApprovalRestData toUserOAuth2ClientApprovalData(UserOAuth2ClientApprovalEntity userClientApproval,
																	Map<String, Approval.ApprovalStatus> scopeAndStatus);

}
