package org.codenergic.theskeleton.tokenstore;

import java.util.Optional;

import org.codenergic.theskeleton.user.UserEntity;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TokenStoreRepository extends PagingAndSortingRepository<TokenStoreEntity, String> {

	Optional<TokenStoreEntity> findByTokenAndType(String token, TokenStoreType type);

	void deleteTokenStoreEntityByUser(UserEntity userEntity);

}
