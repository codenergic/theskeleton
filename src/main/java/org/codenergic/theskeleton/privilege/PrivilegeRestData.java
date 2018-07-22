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

import javax.annotation.Nullable;

import org.codenergic.theskeleton.core.data.RestData;
import org.immutables.value.Value;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@Value.Immutable
@JsonDeserialize(builder = ImmutablePrivilegeRestData.Builder.class)
public interface PrivilegeRestData extends RestData {
	static ImmutablePrivilegeRestData.Builder builder() {
		return ImmutablePrivilegeRestData.builder();
	}

	static ImmutablePrivilegeRestData.Builder builder(PrivilegeEntity privilege) {
		return builder()
			.id(privilege.getId())
			.name(privilege.getName())
			.description(privilege.getDescription());
	}

	@Nullable
	String getDescription();

	@Nullable
	String getId();

	@Nullable
	String getName();
}
