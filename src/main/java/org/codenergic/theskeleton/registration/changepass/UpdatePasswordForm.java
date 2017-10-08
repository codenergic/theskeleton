package org.codenergic.theskeleton.registration.changepass;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.Size;
import java.io.Serializable;

public class UpdatePasswordForm implements Serializable {

	@JsonProperty
	@Size(min = 8, max = 100, message = "Password must be at least 8 character")
	private String password = "";

	private String cpassword;

	private String token;

	public String getPassword() {
		return password;
	}

	public UpdatePasswordForm setPassword(String password) {
		this.password = password;
		return this;
	}

	public String getToken() {
		return token;
	}

	public UpdatePasswordForm setToken(String token) {
		this.token = token;
		return this;
	}

	public String getCpassword() {
		return cpassword;
	}

	public UpdatePasswordForm setCpassword(String cpassword) {
		this.cpassword = cpassword;
		return this;
	}

	@AssertTrue(message="Confirm password should match")
	public boolean isValid() {
		return this.password.equals(this.cpassword);
	}
}
