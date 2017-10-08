package org.codenergic.theskeleton.tokenstore;

import org.codenergic.theskeleton.user.UserEntity;

public interface TokenStoreService {

	TokenStoreEntity sendTokenNotification(TokenStoreType type, UserEntity user);

	TokenStoreEntity findByTokenAndType(String token, TokenStoreType type);

	void deleteTokenByUser(UserEntity userEntity);
}
