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
package org.codenergic.theskeleton.core.data;

import java.util.Date;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@MappedSuperclass
@EntityListeners({ AuditingEntityListener.class })
@SuppressWarnings("serial")
public abstract class AbstractAuditingEntity extends AbstractEntity implements AuditingEntity<Date> {
	@CreatedDate
	@Column(name = "created_date")
	@Temporal(TemporalType.TIMESTAMP)
	private Date createdDate;
	@LastModifiedDate
	@Column(name = "last_modified_date")
	@Temporal(TemporalType.TIMESTAMP)
	private Date lastModifiedDate;
	@Embedded
	@CreatedBy
	@AttributeOverrides({ @AttributeOverride(name = "userId", column = @Column(name = "created_by")),
			@AttributeOverride(name = "clientId", column = @Column(name = "created_by_client")) })
	private AuditInformation createdBy;
	@Embedded
	@LastModifiedBy
	@AttributeOverrides({ @AttributeOverride(name = "userId", column = @Column(name = "last_modified_by")),
			@AttributeOverride(name = "clientId", column = @Column(name = "last_modified_by_client")) })
	private AuditInformation lastModifiedBy;

	@Override
	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	@Override
	public Date getLastModifiedDate() {
		return lastModifiedDate;
	}

	public void setLastModifiedDate(Date lastModifiedDate) {
		this.lastModifiedDate = lastModifiedDate;
	}

	@Override
	public AuditInformation getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(AuditInformation createdBy) {
		this.createdBy = createdBy;
	}

	@Override
	public AuditInformation getLastModifiedBy() {
		return lastModifiedBy;
	}

	public void setLastModifiedBy(AuditInformation lastModifiedBy) {
		this.lastModifiedBy = lastModifiedBy;
	}
}
