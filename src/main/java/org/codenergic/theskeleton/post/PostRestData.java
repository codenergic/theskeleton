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

import org.codenergic.theskeleton.core.data.RestData;
import org.hibernate.validator.constraints.NotBlank;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.google.auto.value.AutoValue;

@SuppressWarnings("serial")
@AutoValue
@JsonDeserialize(builder = AutoValue_PostRestData.Builder.class)
abstract class PostRestData implements RestData {
	@NotBlank(groups = {New.class, Existing.class})
	@Nullable
	abstract String getContent();

	@Nullable
	abstract String getId();

	@NotBlank(groups = {New.class, Existing.class})
	@Nullable
	abstract String getTitle();

	static Builder builder() {
		return new AutoValue_PostRestData.Builder();
	}

	PostEntity toPostEntity() {
		return new PostEntity()
			.setContent(getContent())
			.setId(getId())
			.setTitle(getTitle());
	}

	@AutoValue.Builder
	@JsonPOJOBuilder(withPrefix = "")
	interface Builder {
		Builder content(String content);
		Builder id(String id);
		Builder title(String title);

		PostRestData build();

		default Builder fromPostEntity(PostEntity post) {
			return id(post.getId())
				.content(post.getContent())
				.title(post.getTitle());
		}
	}
	
	public interface New {}

	public interface Existing {}
}
