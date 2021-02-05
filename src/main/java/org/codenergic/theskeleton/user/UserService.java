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
package org.codenergic.theskeleton.user;

import java.io.InputStream;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService extends UserDetailsService {
	@PreAuthorize("isAuthenticated() and (hasAuthority('user_delete') or principal.username == #username)")
	void deleteUser(@NotNull String username);

	@PreAuthorize("isAuthenticated() and hasAuthority('user_update')")
	UserEntity enableOrDisableUser(@NotNull String username, boolean enabled);

	@PreAuthorize("isAuthenticated() and hasAuthority('user_update')")
	UserEntity extendsUserExpiration(@NotNull String username, int amountInMinutes);

	@PreAuthorize("isAuthenticated() and (hasAuthority('user_read') or principal.username == #username)")
	List<SessionInformation> findUserActiveSessions(@NotNull String username);

	Optional<UserEntity> findUserByEmail(@NotNull String email);

	Optional<UserEntity> findUserByUsername(@NotNull String username);

	@PreAuthorize("isAuthenticated() and principal.username == #username")
	List<UserOAuth2ClientApprovalEntity> findUserOAuth2ClientApprovalByUsername(String username);

	@PreAuthorize("isAuthenticated() and hasAuthority('user_read_all')")
	Page<UserEntity> findUsersByUsernameStartingWith(@NotNull String username, Pageable pageable);

	@PreAuthorize("isAuthenticated() and hasAuthority('user_update')")
	UserEntity lockOrUnlockUser(@NotNull String username, boolean unlocked);

	@PreAuthorize("isAuthenticated() and principal.username == #username")
	void removeUserOAuth2ClientApprovalByUsername(String username, String clientId);

	@PreAuthorize("isAuthenticated() and (hasAuthority('user_update') or principal.username == #username)")
	void revokeUserSession(String username, String sessionId);

	@PreAuthorize("isAuthenticated() and hasAuthority('user_write')")
	UserEntity saveUser(@NotNull @Valid UserEntity user);

	@PreAuthorize("isAuthenticated() and isAuthenticated() and principal.username == #username")
	UserEntity updateUser(@NotNull String username, @NotNull @Valid UserEntity newUser);

	@PreAuthorize("isAuthenticated() and hasAuthority('user_update')")
	UserEntity updateUserExpirationDate(@NotNull String username, Date date);

	@PreAuthorize("isAuthenticated() and (hasAuthority('user_update') or principal.username == #username)")
	UserEntity updateUserPassword(@NotNull String username, @NotNull String rawPassword);

	@PreAuthorize("isAuthenticated() and principal.username == #username")
	UserEntity updateUserPicture(String username, InputStream image, String contentType, long contentLength) throws Exception;
}
