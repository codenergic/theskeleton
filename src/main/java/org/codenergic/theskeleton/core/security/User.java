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
package org.codenergic.theskeleton.core.security;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Collection;

import javax.annotation.Nullable;

import org.codenergic.theskeleton.core.data.Identifiable;
import org.immutables.value.Value;
import org.springframework.core.annotation.AliasFor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Nullable
@Value.Immutable
public interface User extends UserDetails, Identifiable<String> {
	@Nullable
	@Value.Auxiliary
	Collection<? extends GrantedAuthority> getAuthorities();

	@Nullable
	String getEmail();

	@Nullable
	@Override
	String getId();

	@Nullable
	@Value.Auxiliary
	String getPassword();

	String getUsername();

	default boolean isAccountNonExpired() {
		return false;
	}

	default boolean isAccountNonLocked() {
		return false;
	}

	default boolean isCredentialsNonExpired() {
		return false;
	}

	default boolean isEnabled()  {
		return false;
	}

	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.PARAMETER)
	@interface Inject {
		@AliasFor("value")
		String parameterName() default "username";

		@AliasFor("parameterName")
		String value() default "username";
	}
}
