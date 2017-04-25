package org.codenergic.theskeleton.role;

import org.codenergic.theskeleton.core.data.AuditingEntityRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends AuditingEntityRepository<RoleEntity> {
	RoleEntity findByCode(String code);
}
