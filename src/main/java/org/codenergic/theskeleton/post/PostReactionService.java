/*
 * Copyright 2018 original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.codenergic.theskeleton.post;

import org.codenergic.theskeleton.user.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;

public interface PostReactionService {
	@PreAuthorize("principal.id == #userId")
	void deletePostReaction(String userId, String postId);

	Page<UserEntity> findUserByPostReaction(String postId, PostReactionType reactionType, Pageable pageable);

	long getNumberOfPostReactions(String postId, PostReactionType reactionType);

	@PreAuthorize("principal.id == #userId")
	PostReactionEntity reactToPost(String userId, String postId, PostReactionType reactionType);
}
