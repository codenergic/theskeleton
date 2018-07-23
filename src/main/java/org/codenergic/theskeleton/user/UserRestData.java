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
package org.codenergic.theskeleton.user;

import java.util.Set;

import javax.annotation.Nullable;
import javax.validation.constraints.Pattern;

import org.codenergic.theskeleton.core.data.RestData;
import org.codenergic.theskeleton.core.web.ValidationConstants;
import org.hibernate.validator.constraints.NotBlank;
import org.immutables.value.Value;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@Value.Immutable
@JsonDeserialize(builder = ImmutableUserRestData.Builder.class)
public interface UserRestData extends RestData {
	static ImmutableUserRestData.Builder builder() {
		return ImmutableUserRestData.builder();
	}

	@Nullable
	Set<String> getAuthorities();

	@NotBlank(groups = {New.class, Existing.class})
	@Nullable
	@Pattern(regexp = ValidationConstants.EMAIL_REGEX, message = "Not a valid email address", groups = {New.class, Existing.class})
	String getEmail();

	@Nullable
	String getId();

	@Nullable
	Boolean getIsNonLocked();

	@Nullable
	String getPassword();

	@Nullable
	String getPhoneNumber();

	@Nullable
	String getPictureUrl();

	@NotBlank(groups = {New.class, Existing.class})
	@Nullable
	String getUsername();

	interface New {
	}

	interface Existing {
	}
}
