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
package org.codenergic.theskeleton.post;

import java.util.Date;

import javax.annotation.Nullable;
import javax.validation.constraints.NotBlank;

import org.codenergic.theskeleton.core.data.RestData;
import org.immutables.value.Value;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@Value.Immutable
@JsonDeserialize(builder = ImmutablePostRestData.Builder.class)
interface PostRestData extends RestData {
	@NotBlank(groups = {New.class, Existing.class})
	@Nullable
	String getContent();

	@Nullable
	Date getCreatedDate();

	@Nullable
	String getId();

	@Nullable
	Date getLastUpdatedDate();

	@Nullable
	Boolean getResponse();

	@Nullable
	String getResponseTo();

	@Nullable
	String getStatus();

	@NotBlank(groups = {New.class, Existing.class})
	@Nullable
	String getTitle();

	interface New {
	}

	interface Existing {
	}
}
