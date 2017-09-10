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

import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;

import org.codenergic.theskeleton.core.data.RestData;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.google.auto.value.AutoValue;

@SuppressWarnings("serial")
@AutoValue
@JsonDeserialize(builder = AutoValue_PostRestData.Builder.class)
abstract class PostRestData implements RestData {
	@NotNull(groups = {New.class, Existing.class})
	@Nullable
	abstract String getContent();

	@NotNull(groups = {Existing.class})
	@Nullable
	abstract String getId();

	@NotNull(groups = {New.class, Existing.class})
	@Nullable
	abstract String getTitle();

	PostEntity toPostEntity() {
		return new PostEntity()
				.setContent(getContent())
				.setId(getId())
				.setTitle(getTitle());
	}

	static Builder builder() {
		return new AutoValue_PostRestData.Builder();
	}

	@AutoValue.Builder
	@JsonPOJOBuilder(withPrefix = "")
	interface Builder {
		PostRestData build();

		default Builder fromPostEntity(PostEntity post) {
			return id(post.getId())
					.content(post.getContent())
					.title(post.getTitle());
		}

		Builder content(String content);

		Builder id(String id);

		Builder title(String title);
	}
 
	public interface New {}

	public interface Existing {}
}
