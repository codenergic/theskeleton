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

import com.fasterxml.jackson.annotation.JsonProperty;

public class PostRestData {
	@JsonProperty
	private String id;
	@JsonProperty
	private String title;
	@JsonProperty
	private String content;

	public PostRestData() {}

	private PostRestData(Builder builder) {
		setId(builder.id);
		setTitle(builder.title);
		setContent(builder.content);
	}

	public static Builder newBuilder() {
		return new Builder();
	}
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}
	public PostEntity toEntity() {
		PostEntity postEntity = new PostEntity();
		postEntity.setId(id);
		postEntity.setTitle(title);
		postEntity.setContent(content);
		return postEntity;
	}

	public static Builder builder(PostEntity post) {
		return newBuilder().title(post.getTitle()).content(post.getContent()).id(post.getId());
	}


	public static final class Builder {
		private String id;
		private String title;
		private String content;

		public Builder() {
		}

		public Builder id(String val) {
			id = val;
			return this;
		}

		public Builder title(String val) {
			title = val;
			return this;
		}

		public Builder content(String val) {
			content = val;
			return this;
		}

		public PostRestData build() {
			return new PostRestData(this);
		}
	}
}
