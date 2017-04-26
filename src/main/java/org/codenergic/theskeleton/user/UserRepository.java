package org.codenergic.theskeleton.user;

import org.codenergic.theskeleton.core.data.AuditingEntityRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends AuditingEntityRepository<UserEntity> {
	UserEntity findByUsername(String username);

	UserEntity findByEmail(String email);

	Page<UserEntity> findByUsernameStartingWith(String username, Pageable pageable);
}
