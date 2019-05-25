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
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.codenergic.theskeleton.core.data.AbstractAuditingEntity;
import org.codenergic.theskeleton.user.UserEntity;

@SuppressWarnings("serial")
@Entity
@Table(name = "ts_post")
public class PostEntity extends AbstractAuditingEntity {
	@Lob
	@Column(name = "content")
	private String content;
	@ManyToOne
	@JoinColumn(name = "poster_user_id")
	private UserEntity poster;
	@ManyToOne
	@JoinColumn(name = "response_to_post_id")
	private PostEntity responseTo;
	@Column(name = "is_response")
	private boolean isResponse = false;

	public String getContent() {
		return content;
	}

	public PostEntity setContent(String content) {
		this.content = content;
		return this;
	}

	public UserEntity getPoster() {
		return poster;
	}

	public PostEntity setPoster(UserEntity poster) {
		this.poster = poster;
		return this;
	}

	public PostEntity getResponseTo() {
		return responseTo;
	}

	public PostEntity setResponseTo(PostEntity responseTo) {
		this.responseTo = responseTo;
		return this;
	}

	public boolean isResponse() {
		return isResponse;
	}

	public PostEntity setResponse(boolean response) {
		isResponse = response;
		return this;
	}

	@Override
	public PostEntity setId(String id) {
		super.setId(id);
		return this;
	}
}
