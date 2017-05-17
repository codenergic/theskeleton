package org.codenergic.theskeleton.user;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.codenergic.theskeleton.client.OAuth2ClientEntity;
import org.codenergic.theskeleton.user.impl.UserOAuth2ClientApprovalStore;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.oauth2.provider.approval.Approval;
import org.springframework.security.oauth2.provider.approval.Approval.ApprovalStatus;
import org.springframework.security.oauth2.provider.approval.ApprovalStore;

public class UserOauth2ClientApprovalStoreTest {
	private ApprovalStore approvalStore;
	@Mock
	private UserOAuth2ClientApprovalRepository approvalRepository;
	@Mock
	private UserRepository userRepository;

	@Before
	public void init() {
		MockitoAnnotations.initMocks(this);
		approvalStore = new UserOAuth2ClientApprovalStore(approvalRepository, userRepository);
	}

	@Test
	public void testAddApprovals() {
		assertThatThrownBy(() -> {
			approvalStore.addApprovals(null);
		}).isInstanceOf(NullPointerException.class);
		when(approvalRepository.findByUserUsernameAndClientIdAndScope(anyString(), anyString(), eq("read")))
				.thenReturn(new UserOAuth2ClientApprovalEntity()
						.setUser(new UserEntity().setId("1"))
						.setClient(new OAuth2ClientEntity().setId("2"))
						.setScope("read")
						.setApprovalStatus(ApprovalStatus.APPROVED));
		approvalStore.addApprovals(Arrays.asList(new Approval("1", "2", "read", new Date(), ApprovalStatus.APPROVED)));
		verify(approvalRepository).findByUserUsernameAndClientIdAndScope(anyString(), anyString(), eq("read"));
		when(approvalRepository.findByUserUsernameAndClientIdAndScope(anyString(), anyString(), eq("write")))
				.thenReturn(null);
		approvalStore.addApprovals(Arrays.asList(new Approval("1", "2", "write", new Date(), ApprovalStatus.APPROVED)));
		verify(approvalRepository).findByUserUsernameAndClientIdAndScope(anyString(), anyString(), eq("write"));
		verify(approvalRepository).save(any(UserOAuth2ClientApprovalEntity.class));
	}

	@Test
	public void testRevokeApprovals() {
		assertThatThrownBy(() -> {
			approvalStore.revokeApprovals(null);
		}).isInstanceOf(NullPointerException.class);
		when(approvalRepository.findByUserUsernameAndClientIdAndScope(anyString(), anyString(), eq("read")))
				.thenReturn(new UserOAuth2ClientApprovalEntity()
						.setUser(new UserEntity().setId("1"))
						.setClient(new OAuth2ClientEntity().setId("2"))
						.setScope("read")
						.setApprovalStatus(ApprovalStatus.APPROVED));
		when(approvalRepository.findByUserUsernameAndClientIdAndScope(anyString(), anyString(), eq("write"))).thenReturn(null);
		List<Approval> approvals = new ArrayList<>();
		approvals.add(new Approval("", "", "write", new Date(), ApprovalStatus.APPROVED));
		for (int i = 0; i < 3; i++) {
			approvals.add(new Approval("", "", "read", new Date(), ApprovalStatus.APPROVED));
		}
		approvalStore.revokeApprovals(approvals);
		verify(approvalRepository, times(1)).findByUserUsernameAndClientIdAndScope(anyString(), anyString(), eq("write"));
		verify(approvalRepository, times(3)).findByUserUsernameAndClientIdAndScope(anyString(), anyString(), eq("read"));
		verify(approvalRepository, times(3)).delete(any(UserOAuth2ClientApprovalEntity.class));
	}

	@Test
	@SuppressWarnings("serial")
	public void testGetApprovals() {
		when(approvalRepository.findByUserUsernameAndClientId(anyString(), anyString()))
				.thenReturn(Arrays.asList(new UserOAuth2ClientApprovalEntity() {{ setCreatedDate(new Date()); }}
								.setApprovalStatus(ApprovalStatus.APPROVED)
								.setUser(new UserEntity())
								.setClient(new OAuth2ClientEntity()),
						new UserOAuth2ClientApprovalEntity() {{ setCreatedDate(new Date()); }}
								.setApprovalStatus(ApprovalStatus.DENIED)
								.setUser(new UserEntity())
								.setClient(new OAuth2ClientEntity())));
		List<Approval> approvals = new ArrayList<>(approvalStore.getApprovals("1", "2"));
		assertThat(approvals.size()).isEqualTo(2);
		assertThat(approvals.get(0).getStatus()).isEqualTo(ApprovalStatus.APPROVED);
		assertThat(approvals.get(1).getStatus()).isEqualTo(ApprovalStatus.DENIED);
		verify(approvalRepository).findByUserUsernameAndClientId(anyString(), anyString());
	}
}
