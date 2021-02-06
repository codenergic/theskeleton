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

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.request.WebRequest;

@Controller
@RequestMapping("/registration")
public class RegistrationController {
	private static final String REGISTRATION = "registration";
	private static final String REGISTRATION_CONFIRMATION = "registration_confirmation";
	private static final String REGISTRATION_ACTIVATION = "registration_activation";

	private final RegistrationService registrationService;

	public RegistrationController(RegistrationService registrationService) {
		this.registrationService = registrationService;
	}

	@GetMapping
	public String registrationView(RegistrationForm registrationForm, WebRequest request) {
		return REGISTRATION;
	}

	@PostMapping
	public String register(@Valid RegistrationForm registrationForm, BindingResult bindingResult, WebRequest request) {
		if (bindingResult.hasErrors())
			return registrationView(registrationForm, request);
		try {
			registrationService.registerUser(registrationForm);
		} catch (RegistrationException e) {
			bindingResult.rejectValue("username", "error.registrationForm", e.getMessage());
			return registrationView(registrationForm, request);
		}
		return REGISTRATION_CONFIRMATION;
	}

	@GetMapping(path = "/activate")
	public String activateUser(Model model, @RequestParam(name = "at") String activationToken) {
		try {
			registrationService.activateUser(activationToken);
		} catch (RegistrationException e) {
			model.addAttribute("message", e.getMessage());
		}
		return REGISTRATION_ACTIVATION;
	}
}
