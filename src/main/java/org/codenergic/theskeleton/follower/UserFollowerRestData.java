/*
 * Copyright 2018 the original author or authors.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.codenergic.theskeleton.follower;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.google.auto.value.AutoValue;
import org.codenergic.theskeleton.core.data.RestData;

import javax.annotation.Nullable;

@AutoValue
@JsonDeserialize(builder = AutoValue_UserFollowerRestData.class)
public abstract class UserFollowerRestData implements RestData {
	public static Builder builder() {
		return new AutoValue_UserFollowerRestData.Builder();
	}

	@Nullable
	abstract String getFollowerPictureUrl();

	@Nullable
	abstract String getFollowerUsername();

	@Nullable
	abstract String getFollowingPictureUrl();

	@Nullable
	abstract String getFollowingUsername();

	@AutoValue.Builder
	@JsonPOJOBuilder(withPrefix = "")
	interface Builder {
		UserFollowerRestData build();

		Builder setFollowerPictureUrl(String newFollowerPictureUrl);

		Builder setFollowerUsername(String newFollowerUsername);

		Builder setFollowingPictureUrl(String newFollowingPictureUrl);

		Builder setFollowingUsername(String newFollowingUsername);
	}
}
