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

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.auto.value.AutoValue;

@SuppressWarnings("serial")
@AutoValue
@JsonDeserialize(builder = AutoValue_RoleRestData.Builder.class)
public abstract class RoleRestData implements RestData {
	public static Builder builder() {
		return new AutoValue_RoleRestData.Builder();
	}

	public static Builder builder(RoleEntity role) {
		return builder()
			.id(role.getId())
			.code(role.getCode())
			.description(role.getDescription());
	}

	@NotBlank(groups = {New.class, Existing.class})
	@Nullable
	abstract String getCode();

	@Nullable
	abstract String getDescription();

	@Nullable
	abstract String getId();

	RoleEntity toRoleEntity() {
		return new RoleEntity()
			.setId(getId())
			.setCode(getCode())
			.setDescription(getDescription());
	}

	@AutoValue.Builder
	public interface Builder extends RestData.Builder {
		RoleRestData build();

		Builder code(String code);

		Builder description(String description);

		Builder id(String id);
	}

	public interface New {
	}

	public interface Existing {
	}
}
