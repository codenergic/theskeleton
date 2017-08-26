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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.codenergic.theskeleton.core.data.AbstractAuditingEntity;

@SuppressWarnings("serial")
@Entity
@Table(name = "ts_post")
public class PostEntity extends AbstractAuditingEntity {
	@NotNull
	@Column(name = "title")
	private String title;
	@Lob
	@Column(name = "content")
	private String content;

	@Override
	public PostEntity setId(String id) {
		super.setId(id);
		return this;
	}

	public String getTitle() {
		return title;
	}

	public PostEntity setTitle(String title) {
		this.title = title;
		return this;
	}

	public String getContent() {
		return content;
	}

	public PostEntity setContent(String content) {
		this.content = content;
		return this;
	}
}
