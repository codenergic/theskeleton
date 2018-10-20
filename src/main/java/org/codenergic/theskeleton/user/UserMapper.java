/*
 * Copyright 2018 the original author or authors.
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

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface UserMapper {
	static UserMapper newInstance() {
		return Mappers.getMapper(UserMapper.class);
	}

	@Mapping(target = "authorities", ignore = true)
	@Mapping(target = "accountNonLocked", source = "isNonLocked")
	UserEntity toUser(UserRestData userData);

	@Mapping(target = "authorities", ignore = true)
	@Mapping(target = "nonLocked", source = "accountNonLocked")
	@Mapping(target = "password", ignore = true)
	UserRestData toUserData(UserEntity user);
}
