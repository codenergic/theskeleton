package org.codenergic.theskeleton.post;

import org.codenergic.theskeleton.core.data.AuditingEntityRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface PostFollowingRepository extends AuditingEntityRepository<PostEntity> {
	@Query("FROM PostEntity pe "
			+ "WHERE pe.poster.id IN (SELECT uf.user.id FROM UserFollowerEntity uf "
				+ "WHERE uf.follower.id = ?1) "
			+ "AND pe.postStatus = 'PUBLISHED' ")
	Page<PostEntity> findByFollowerId(String followerId, Pageable pageable);
}
