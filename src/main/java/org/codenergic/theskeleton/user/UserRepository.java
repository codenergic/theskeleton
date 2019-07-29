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
package org.codenergic.theskeleton.user;

import java.util.Optional;
import java.util.stream.Stream;

import org.codenergic.theskeleton.core.data.AuditingEntityRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends AuditingEntityRepository<UserEntity> {
	Optional<UserEntity> findByEmail(String email);

	Optional<UserEntity> findByUsername(String username);

	@Query("from UserEntity where (username = ?1 or email = ?2) and enabled = ?3")
	Stream<UserEntity> findByUsernameOrEmailAndEnabled(String username, String email, boolean enabled);

	Page<UserEntity> findByUsernameStartingWith(String username, Pageable pageable);
}
