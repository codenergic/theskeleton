package org.codenergic.theskeleton.registration.changepass;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.codenergic.theskeleton.core.web.ValidationConstants;

import javax.validation.constraints.Pattern;
import java.io.Serializable;

public class ChangePasswordForm implements Serializable {
	@JsonProperty
	@Pattern(regexp = ValidationConstants.EMAIL_REGEX, message = "Not a valid email address")
	private String email;

	public String getEmail() {
		return email;
	}

	public ChangePasswordForm setEmail(String email) {
		this.email = email;
		return this;
	}
}
