/*
 * Copyright 2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.codenergic.theskeleton.post;

import org.codenergic.theskeleton.core.data.AuditingEntityRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface PostRepository extends AuditingEntityRepository<PostEntity> {
	Page<PostEntity> findByPosterUsername(String posterUsername, Pageable pageable);

	Page<PostEntity> findByResponseToId(String postId, Pageable pageable);

	@Query("select p from PostEntity  p where p.title like %?1% and p.postStatus = 'PUBLISHED'")
	Page<PostEntity> findByTitleContaining(String title, Pageable pageable);

	Page<PostEntity> findByPosterIdAndPostStatus(String posterId, PostStatus postStatus, Pageable pageable);
}
