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
package org.codenergic.theskeleton.client;

import org.codenergic.theskeleton.core.security.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users/{username}/clients")
public class UserOAuth2ClientRestController {
	private final OAuth2ClientService oAuth2ClientService;
	private final OAuth2ClientMapper oAuth2ClientMapper = OAuth2ClientMapper.newInstance();

	public UserOAuth2ClientRestController(OAuth2ClientService oAuth2ClientService) {
		this.oAuth2ClientService = oAuth2ClientService;
	}

	@GetMapping
	public Page<OAuth2ClientRestData> findClientByUser(@User.Inject User user, Pageable pageable) {
		return oAuth2ClientService.findClientByOwner(user.getId(), pageable)
			.map(oAuth2ClientMapper::toOAuth2ClientData);
	}
}
