package org.codenergic.theskeleton.registration;

import org.codenergic.theskeleton.user.UserEntity;

public interface RegistrationService {
	boolean isEmailExists(String email);

	boolean isUsernameExists(String username);

	UserEntity registerUser(RegistrationForm form);

	RegistrationEntity sendConfirmationNotification(UserEntity user);

	boolean activateUser(String activationToken);
}
