/*
 * Copyright 2017 the original author or authors.
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
package org.codenergic.theskeleton.user.profile;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.auto.value.AutoValue;
import org.codenergic.theskeleton.core.data.RestData;

@SuppressWarnings("serial")
@AutoValue
@JsonDeserialize(builder = AutoValue_ProfileSocialRestData.Builder.class)
abstract class ProfileSocialRestData implements RestData {
	static Builder builder() {
		return new AutoValue_ProfileSocialRestData.Builder();
	}

	abstract String getImageUrl();

	abstract String getProfileId();

	abstract String getProfileUrl();

	abstract String getProvider();

	@AutoValue.Builder
	interface Builder extends RestData.Builder {
		ProfileSocialRestData build();

		Builder imageUrl(String imageUrl);

		Builder profileId(String providerUserId);

		Builder profileUrl(String profileUrl);

		Builder provider(String providerName);
	}
}
