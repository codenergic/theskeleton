package org.codenergic.theskeleton.core.data;

import javax.persistence.Embeddable;

@Embeddable
public class AuditInformation {
	private String username;
	private String clientId;

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getClientId() {
		return clientId;
	}

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}
}
