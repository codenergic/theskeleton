package org.codenergic.theskeleton.core.data;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.MappedSuperclass;

@MappedSuperclass
public interface AuditingEntity extends Serializable {
	Date getCreatedDate();

	Date getLastModifiedDate();

	AuditInformation getCreatedBy();

	AuditInformation getLastModifiedBy();
}
