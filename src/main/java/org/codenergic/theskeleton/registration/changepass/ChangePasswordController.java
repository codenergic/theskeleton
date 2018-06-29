package org.codenergic.theskeleton.registration.changepass;

import javax.validation.Valid;

import org.codenergic.theskeleton.registration.RegistrationException;
import org.codenergic.theskeleton.registration.RegistrationService;
import org.codenergic.theskeleton.tokenstore.TokenStoreService;
import org.codenergic.theskeleton.tokenstore.TokenStoreType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("changepass")
public class ChangePasswordController {

	private static final String CHANGEPASS = "changepass";
	private static final String CHANGEPASS_CONFIRMATION = "changepass_confirmation";
	private static final String CHANGEPASS_UPDATE = "changepass_update";
	private static final String ERROR = "error";
	private static final String MESSAGE = "message";

	private RegistrationService registrationService;
	private TokenStoreService tokenStoreService;

	public ChangePasswordController(RegistrationService registrationService, TokenStoreService tokenStoreService) {
		this.registrationService = registrationService;
		this.tokenStoreService = tokenStoreService;
	}

	@GetMapping
	public String changepassView(ChangePasswordForm changePasswordForm) {
		return CHANGEPASS;
	}

	@PostMapping
	public String changepass(Model model, @Valid ChangePasswordForm changePasswordForm, BindingResult bindingResult) {
		if (bindingResult.hasErrors())
			return changepassView(changePasswordForm);

		return registrationService.findUserByEmail(changePasswordForm.getEmail())
			.map(user -> {
				tokenStoreService.sendTokenNotification(TokenStoreType.CHANGE_PASSWORD, user);
				model.addAttribute(MESSAGE, CHANGEPASS);
				return CHANGEPASS_CONFIRMATION;
			})
			.orElseGet(() -> {
				bindingResult.rejectValue("email", "error.changePasswordForm", "Can't find that email, sorry.");
				model.addAttribute(MESSAGE, CHANGEPASS);
				return changepassView(changePasswordForm);
			});
	}

	@GetMapping(path = "/update")
	public String updateView(@RequestParam(name = "rt") String resetToken, UpdatePasswordForm updatePasswordForm) {
		return tokenStoreService.findByTokenAndType(resetToken, TokenStoreType.CHANGE_PASSWORD)
			.map(t -> {
				updatePasswordForm.setToken(t.getToken());
				return CHANGEPASS_UPDATE;
			})
			.orElse("redirect:/changepass");
	}

	@PostMapping(path = "/update")
	public String update(Model model, @Valid UpdatePasswordForm updatePasswordForm, BindingResult bindingResult,
						 @RequestParam(name = "rt") String resetToken) {
		if (bindingResult.hasErrors())
			return updateView(resetToken, updatePasswordForm);
		try {
			registrationService.changePassword(resetToken, updatePasswordForm.getPassword());
		} catch (RegistrationException e) {
			model.addAttribute(MESSAGE, ERROR);
			model.addAttribute(ERROR, e.getMessage());
			return CHANGEPASS_CONFIRMATION;
		}
		model.addAttribute(MESSAGE, "update");
		return CHANGEPASS_CONFIRMATION;
	}
}
