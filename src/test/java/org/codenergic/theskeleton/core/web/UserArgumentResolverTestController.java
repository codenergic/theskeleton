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
package org.codenergic.theskeleton.core.web;

import org.codenergic.theskeleton.core.security.ImmutableUser;
import org.codenergic.theskeleton.core.security.User;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
class UserArgumentResolverTestController {
	@RequestMapping("/test1/{username}")
	public User test1(@User.Inject User user) {
		return ImmutableUser.copyOf(user).withId(user.getUsername().concat("12345"));
	}

	@RequestMapping("/test2/{id}")
	public User test2(@User.Inject("id") User user) {
		return ImmutableUser.copyOf(user).withId(user.getUsername().concat("12345"));
	}

	@RequestMapping("/test3/{id}")
	public User test3(@User.Inject(parameterName = "id") User user) {
		return ImmutableUser.copyOf(user).withId(user.getUsername().concat("12345"));
	}

	@RequestMapping("/test4/{id}")
	public User test4(@User.Inject(parameterName = "idx") User user) {
		return user == null ? null : ImmutableUser.copyOf(user).withId(user.getUsername().concat("12345"));
	}

	@RequestMapping("/test5/{username}")
	public User test5(@User.Inject User user) {
		return user;
	}
}
