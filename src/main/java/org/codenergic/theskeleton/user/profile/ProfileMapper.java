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
package org.codenergic.theskeleton.user.profile;

import org.codenergic.theskeleton.user.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionData;

@Mapper
interface ProfileMapper {
	static ProfileMapper newInstance() {
		return Mappers.getMapper(ProfileMapper.class);
	}

	@Mapping(target = "password", ignore = true)
	ProfileRestData toProfileData(UserEntity user);

	@Mapping(target = "provider", source = "provider")
	@Mapping(target = "imageUrl", source = "connection.imageUrl")
	@Mapping(target = "profileUrl", source = "connection.profileUrl")
	@Mapping(target = "profileId", source = "connectionData.providerUserId")
	ProfileSocialRestData toProfileSocialData(String provider, Connection<?> connection, ConnectionData connectionData);

	UserEntity toUser(ProfileRestData profileData);
}
