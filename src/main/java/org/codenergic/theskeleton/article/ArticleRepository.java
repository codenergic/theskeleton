package org.codenergic.theskeleton.article;

import org.codenergic.theskeleton.core.data.AuditingEntityRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
public interface ArticleRepository extends AuditingEntityRepository<ArticleEntity> {

	Page<ArticleEntity> findByTitleContaining(String title, Pageable pageable);

}
