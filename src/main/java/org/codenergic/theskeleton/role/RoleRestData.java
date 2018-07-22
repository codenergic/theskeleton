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

import javax.annotation.Nullable;

import org.codenergic.theskeleton.core.data.RestData;
import org.hibernate.validator.constraints.NotBlank;
import org.immutables.value.Value;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@Value.Immutable
//@RestData.Immutable
@JsonDeserialize(builder = ImmutableRoleRestData.Builder.class)
public interface RoleRestData extends RestData {
	static ImmutableRoleRestData.Builder builder() {
		return ImmutableRoleRestData.builder();
	}

	static ImmutableRoleRestData.Builder builder(RoleEntity role) {
		return builder()
			.id(role.getId())
			.code(role.getCode())
			.description(role.getDescription());
	}

	@NotBlank(groups = {New.class, Existing.class})
	@Nullable
	String getCode();

	@Nullable
	String getDescription();

	@Nullable
	String getId();

	default RoleEntity toRoleEntity() {
		return new RoleEntity()
			.setId(getId())
			.setCode(getCode())
			.setDescription(getDescription());
	}

	interface New {
	}

	interface Existing {
	}
}
