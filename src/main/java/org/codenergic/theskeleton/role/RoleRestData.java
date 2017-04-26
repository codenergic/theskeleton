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

import javax.validation.constraints.NotNull;

import org.codenergic.theskeleton.core.data.RestData;

import com.fasterxml.jackson.annotation.JsonProperty;

@SuppressWarnings("serial")
public class RoleRestData implements RestData {
	@JsonProperty
	private String id;
	@JsonProperty
	@NotNull
	private String code;
	@JsonProperty
	private String description;

	private RoleRestData() {
	}

	private RoleRestData(Builder builder) {
		this.id = builder.id;
		this.code = builder.code;
		this.description = builder.description;
	}

	public String getId() {
		return id;
	}

	public String getCode() {
		return code;
	}

	public String getDescription() {
		return description;
	}

	/**
	 * Creates builder to build {@link RoleRestData}.
	 *
	 * @return created builder
	 */
	public static Builder builder() {
		return new Builder();
	}

	public static Builder builder(RoleEntity role) {
		return new Builder()
				.withId(role.getId())
				.withCode(role.getCode())
				.withDescription(role.getDescription());
	}

	public RoleEntity toEntity() {
		RoleEntity roleEntity = new RoleEntity();
		roleEntity.setId(id);
		roleEntity.setCode(code);
		roleEntity.setDescription(description);
		return roleEntity;
	}

	/**
	 * Builder to build {@link RoleRestData}.
	 */
	public static final class Builder {
		private String id;
		private String code;
		private String description;

		private Builder() {
		}

		public Builder withId(String id) {
			this.id = id;
			return this;
		}

		public Builder withCode(String code) {
			this.code = code;
			return this;
		}

		public Builder withDescription(String description) {
			this.description = description;
			return this;
		}

		public RoleRestData build() {
			return new RoleRestData(this);
		}
	}

}
