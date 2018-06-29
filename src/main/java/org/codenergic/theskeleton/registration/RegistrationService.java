package org.codenergic.theskeleton.registration;

import java.util.Optional;

import org.codenergic.theskeleton.user.UserEntity;

public interface RegistrationService {
	boolean isEmailExists(String email);

	boolean isUsernameExists(String username);

	UserEntity registerUser(RegistrationForm form);

	Optional<UserEntity> findUserByEmail(String email);

	boolean activateUser(String activationToken);

	boolean changePassword(String activationToken, String password);
}
