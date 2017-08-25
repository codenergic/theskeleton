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
package org.codenergic.theskeleton.privilege;

import javax.validation.constraints.NotNull;

import org.codenergic.theskeleton.core.data.RestData;

import com.fasterxml.jackson.annotation.JsonProperty;

@SuppressWarnings("serial")
public class PrivilegeRestData implements RestData {
	@JsonProperty
	private String id;
	@JsonProperty
	@NotNull
	private String name;
	@JsonProperty
	private String description;

	private PrivilegeRestData() {
		this.name = "";
	}

	private PrivilegeRestData(Builder builder) {
		this.id = builder.id;
		this.name = builder.name;
		this.description = builder.description;
	}

	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}

	/**
	 * Creates builder to build {@link PrivilegeRestData}.
	 *
	 * @return created builder
	 */
	public static Builder builder() {
		return new Builder();
	}

	public static Builder builder(PrivilegeEntity privilege) {
		return new Builder()
				.withId(privilege.getId())
				.withName(privilege.getName())
				.withDescription(privilege.getDescription());
	}

	public PrivilegeEntity toEntity() {
		PrivilegeEntity privilege = new PrivilegeEntity();
		privilege.setId(id);
		privilege.setName(name);
		privilege.setDescription(description);
		return privilege;
	}

	/**
	 * Builder to build {@link PrivilegeRestData}.
	 */
	public static final class Builder {
		private String id;
		private String name;
		private String description;

		private Builder() {
		}

		public Builder withId(String id) {
			this.id = id;
			return this;
		}

		public Builder withName(String name) {
			this.name = name;
			return this;
		}

		public Builder withDescription(String description) {
			this.description = description;
			return this;
		}

		public PrivilegeRestData build() {
			return new PrivilegeRestData(this);
		}
	}

}
