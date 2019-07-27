/*
 * Copyright 2019 the original author or authors.
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
package org.codenergic.theskeleton.tokenstore;

import java.time.Instant;
import java.util.Date;
import java.util.UUID;

import javax.annotation.Nullable;

import org.codenergic.theskeleton.core.data.RestData;
import org.codenergic.theskeleton.core.security.User;
import org.immutables.value.Value;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@Value.Immutable
@JsonDeserialize(builder = ImmutableTokenStoreRestData.Builder.class)
public interface TokenStoreRestData extends RestData {
	Date getExpiryDate();

	@Nullable
	String getSignedToken();

	TokenStoreType getTokenType();

	@Nullable
	User getUser();

	String getUserId();

	UUID getUuid();

	@JsonIgnore
	default boolean isExpired() {
		return Instant.now().isAfter(getExpiryDate().toInstant());
	}
}
