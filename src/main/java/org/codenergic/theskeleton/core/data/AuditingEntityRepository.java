package org.codenergic.theskeleton.core.data;

import org.springframework.data.repository.PagingAndSortingRepository;

public interface AuditingEntityRepository<T extends AbstractAuditingEntity> extends PagingAndSortingRepository<T, String> {

}
