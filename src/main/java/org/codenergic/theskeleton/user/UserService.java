package org.codenergic.theskeleton.user;

import java.util.Date;
import java.util.Set;

import org.codenergic.theskeleton.role.RoleEntity;
import org.codenergic.theskeleton.user.impl.UserServiceImpl;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;

public interface UserService {
	static UserService newInstance(PasswordEncoder passwordEncoder, UserRepository userRepository,
			UserRoleRepository userRoleRepository) {
		return new UserServiceImpl(passwordEncoder, userRepository, userRoleRepository);
	}

	UserEntity enableOrDisableUser(String username, boolean enabled);

	UserEntity extendsUserExpiration(String username, int amountInMinutes);

	Set<RoleEntity> findRolesByUserUsername(String username);

	UserEntity findUserByEmail(String email);

	UserEntity findUserByUsername(String username);

	Set<UserEntity> findUsersByRoleCode(String code);

	Page<UserEntity> findUsersByUsernameStartingWith(String username, Pageable pageable);

	UserEntity lockOrUnlockUser(String username, boolean unlocked);

	UserEntity saveUser(UserEntity userEntity);

	UserEntity updateUser(String username, UserEntity newUser);

	UserEntity updateUserExpirationDate(String username, Date date);

	UserEntity updateUserPassword(String username, String rawPassword);
}
