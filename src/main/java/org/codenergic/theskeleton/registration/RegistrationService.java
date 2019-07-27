package org.codenergic.theskeleton.registration;

import org.codenergic.theskeleton.user.UserEntity;

public interface RegistrationService {
	UserEntity registerUser(RegistrationForm form);

	void activateUser(String activationToken);

	void changePassword(String activationToken, String password);
}
