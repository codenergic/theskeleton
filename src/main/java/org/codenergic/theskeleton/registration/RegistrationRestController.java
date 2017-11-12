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

import javax.validation.Valid;

import org.codenergic.theskeleton.tokenstore.TokenStoreService;
import org.codenergic.theskeleton.tokenstore.TokenStoreType;
import org.codenergic.theskeleton.user.UserEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/register")
public class RegistrationRestController {
	private RegistrationService registrationService;
	private TokenStoreService tokenStoreService;

	public RegistrationRestController(RegistrationService registrationService,
			TokenStoreService tokenStoreService) {
		this.registrationService = registrationService;
		this.tokenStoreService = tokenStoreService;
	}

	@PostMapping
	public RegistrationForm register(@RequestBody @Valid RegistrationForm registrationForm) {
		UserEntity user = registrationService.registerUser(registrationForm);
		if (user != null && user.getId() != null)
			tokenStoreService.sendTokenNotification(TokenStoreType.USER_ACTIVATION, user);
		return registrationForm;
	}
}
