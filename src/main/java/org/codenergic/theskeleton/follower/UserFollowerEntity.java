/*
 * Copyright 2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *  *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *  *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.codenergic.theskeleton.follower;

import org.codenergic.theskeleton.core.data.AbstractAuditingEntity;
import org.codenergic.theskeleton.user.UserEntity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "ts_user_follower", uniqueConstraints = {
	@UniqueConstraint(name = "uk_user_follower_user_id_follower_id", columnNames = {"user_id", "follower_id"})
})
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public class UserFollowerEntity extends AbstractAuditingEntity {
	@NotNull
	@ManyToOne
	@JoinColumn(name = "user_id", nullable = false)
	private UserEntity user;
	@NotNull
	@ManyToOne
	@JoinColumn(name = "follower_id", nullable = false)
	private UserEntity follower;

	public UserEntity getUser() {
		return user;
	}

	public UserFollowerEntity setUser(UserEntity user) {
		this.user = user;
		return this;
	}

	public UserEntity getFollower() {
		return follower;
	}

	public UserFollowerEntity setFollower(UserEntity follower) {
		this.follower = follower;
		return this;
	}
}
