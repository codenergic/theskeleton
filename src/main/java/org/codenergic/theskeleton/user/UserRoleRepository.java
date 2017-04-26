package org.codenergic.theskeleton.user;

import java.util.Set;

import org.codenergic.theskeleton.core.data.AuditingEntityRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRoleRepository extends AuditingEntityRepository<UserRoleEntity> {
	Set<UserRoleEntity> findByUserUsername(String username);

	Set<UserRoleEntity> findByRoleCode(String code);
}
