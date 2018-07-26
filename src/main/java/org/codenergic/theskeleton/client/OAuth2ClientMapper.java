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

package org.codenergic.theskeleton.client;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
interface OAuth2ClientMapper {
	static OAuth2ClientMapper newInstance() {
		return Mappers.getMapper(OAuth2ClientMapper.class);
	}

	@Mapping(target = "autoApprove", source = "isAutoApprove")
	@Mapping(target = "scoped", source = "isScoped")
	@Mapping(target = "secretRequired", source = "isSecretRequired")
	@Mapping(target = "authorizedGrantTypes", source = "authorizedGrantTypes")
	OAuth2ClientEntity toOAuth2Client(OAuth2ClientRestData oAuth2ClientData);

	@Mapping(target = "registeredRedirectUris", source = "registeredRedirectUri")
	OAuth2ClientRestData toOAuth2ClientData(OAuth2ClientEntity oAuth2Client);
}
