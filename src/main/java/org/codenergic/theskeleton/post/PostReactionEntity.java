/*
 * Copyright 2018 original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.codenergic.theskeleton.post;

import org.codenergic.theskeleton.core.data.AbstractAuditingEntity;
import org.codenergic.theskeleton.user.UserEntity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "ts_reaction", uniqueConstraints = {@UniqueConstraint(columnNames = {"post_id", "user_id", "reaction_type"})})
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public class PostReactionEntity extends AbstractAuditingEntity {
	@NotNull
	@ManyToOne
	@JoinColumn(name = "post_id", nullable = false)
	private PostEntity post;
	@NotNull
	@ManyToOne
	@JoinColumn(name = "user_id", nullable = false)
	private UserEntity user;
	@NotNull
	@Enumerated(EnumType.STRING)
	@Column(name = "reaction_type", nullable = false)
	private PostReactionType reactionType;

	public PostEntity getPost() {
		return post;
	}

	public PostReactionEntity setPost(PostEntity post) {
		this.post = post;
		return this;
	}

	public PostReactionType getReactionType() {
		return reactionType;
	}

	public PostReactionEntity setReactionType(PostReactionType reactionType) {
		this.reactionType = reactionType;
		return this;
	}

	public UserEntity getUser() {
		return user;
	}

	public PostReactionEntity setUser(UserEntity user) {
		this.user = user;
		return this;
	}
}
