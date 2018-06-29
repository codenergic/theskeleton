package org.codenergic.theskeleton.tokenstore;

import java.util.Optional;

import org.codenergic.theskeleton.user.UserEntity;

public interface TokenStoreService {

	TokenStoreEntity sendTokenNotification(TokenStoreType type, UserEntity user);

	Optional<TokenStoreEntity> findByTokenAndType(String token, TokenStoreType type);

	void deleteTokenByUser(UserEntity userEntity);
}
