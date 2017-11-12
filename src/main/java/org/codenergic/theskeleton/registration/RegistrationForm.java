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
package org.codenergic.theskeleton.registration;

import java.io.Serializable;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import org.codenergic.theskeleton.core.web.ValidationConstants;

import com.fasterxml.jackson.annotation.JsonProperty;

@SuppressWarnings("serial")
public class RegistrationForm implements Serializable {
	@JsonProperty
	@Pattern(regexp = ValidationConstants.USERNAME_REGEX, message = "Username must be lower case alphanumeric")
	@Size(min = 6, max = 100, message = "Username must be at least 6 character")
	private String username;
	@JsonProperty
	@Size(min = 8, max = 100, message = "Password must be at least 8 character")
	private String password;
	@JsonProperty
	@Pattern(regexp = ValidationConstants.EMAIL_REGEX, message = "Not a valid email address")
	private String email;

	public String getUsername() {
		return username;
	}

	public RegistrationForm setUsername(String username) {
		this.username = username;
		return this;
	}

	public String getPassword() {
		return password;
	}

	public RegistrationForm setPassword(String password) {
		this.password = password;
		return this;
	}

	public String getEmail() {
		return email;
	}

	public RegistrationForm setEmail(String email) {
		this.email = email;
		return this;
	}
}
