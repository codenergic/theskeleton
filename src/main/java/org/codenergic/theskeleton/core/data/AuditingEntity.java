package org.codenergic.theskeleton.core.data;

import java.io.Serializable;

import javax.persistence.MappedSuperclass;

@MappedSuperclass
public interface AuditingEntity<DATE> extends Serializable {
	DATE getCreatedDate();

	DATE getLastModifiedDate();

	AuditInformation getCreatedBy();

	AuditInformation getLastModifiedBy();
}
