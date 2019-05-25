/*
 * Copyright 2018 the original author or authors.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.codenergic.theskeleton.user;

import java.util.Collection;
import java.util.Objects;
import java.util.stream.Collectors;

import org.codenergic.theskeleton.client.OAuth2ClientEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.provider.approval.Approval;
import org.springframework.security.oauth2.provider.approval.ApprovalStore;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
class UserOAuth2ClientApprovalStore implements ApprovalStore {
	private UserOAuth2ClientApprovalRepository approvalRepository;
	private UserRepository userRepository;

	public UserOAuth2ClientApprovalStore(UserOAuth2ClientApprovalRepository approvalRepository,
			UserRepository userRepository) {
		this.approvalRepository = approvalRepository;
		this.userRepository = userRepository;
	}

	@Override
	public boolean addApprovals(Collection<Approval> approvals) {
		Objects.requireNonNull(approvals);
		approvals.forEach(a -> {
			UserOAuth2ClientApprovalEntity approval = approvalRepository.findByUserUsernameAndClientIdAndScope(a.getUserId(),
					a.getClientId(), a.getScope());
			UserOAuth2ClientApprovalEntity newApproval = new UserOAuth2ClientApprovalEntity();
			if (approval != null) {
				newApproval.setId(approval.getId());
				newApproval.setCreatedBy(approval.getCreatedBy());
				newApproval.setCreatedDate(approval.getCreatedDate());
			}
			UserEntity user = userRepository.findByUsername(a.getUserId())
				.orElseThrow(() -> new UsernameNotFoundException(a.getUserId()));
			newApproval
					.setUser(user)
					.setClient(new OAuth2ClientEntity().setId(a.getClientId()))
					.setScope(a.getScope())
					.setApprovalStatus(a.getStatus());
			approvalRepository.save(newApproval);
		});

		return true;
	}

	@Override
	public boolean revokeApprovals(Collection<Approval> approvals) {
		Objects.requireNonNull(approvals);
		approvals.forEach(a -> {
			UserOAuth2ClientApprovalEntity approval = approvalRepository.findByUserUsernameAndClientIdAndScope(a.getUserId(),
					a.getClientId(), a.getScope());
			if (approval != null) {
				approvalRepository.delete(approval);
			}
		});

		return true;
	}

	@Override
	@Transactional(readOnly = true)
	public Collection<Approval> getApprovals(String userId, String clientId) {
		return approvalRepository.findByUserUsernameAndClientId(userId, clientId).stream()
				.map(UserOAuth2ClientApprovalEntity::toApproval)
				.collect(Collectors.toList());
	}

}
