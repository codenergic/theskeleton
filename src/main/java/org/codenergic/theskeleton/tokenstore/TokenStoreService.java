package org.codenergic.theskeleton.tokenstore;

import org.codenergic.theskeleton.user.UserEntity;

public interface TokenStoreService {
	TokenStoreRestData findAndVerifyToken(String token);

	TokenStoreRestData sendTokenNotification(TokenStoreType type, UserEntity user);
}
