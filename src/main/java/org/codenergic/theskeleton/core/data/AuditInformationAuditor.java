package org.codenergic.theskeleton.core.data;

import java.util.Optional;

import org.codenergic.theskeleton.core.security.User;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.provider.OAuth2Authentication;

public class AuditInformationAuditor implements AuditorAware<AuditInformation> {
	@Override
	public Optional<AuditInformation> getCurrentAuditor() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		AuditInformation auditInformation = new AuditInformation();
		if (authentication instanceof OAuth2Authentication)
			auditInformation.setClientId(((OAuth2Authentication) authentication).getOAuth2Request().getClientId());
		if (authentication.getPrincipal() instanceof User)
			auditInformation.setUserId(((User) authentication.getPrincipal()).getId());
		return Optional.of(auditInformation);
	}
}
