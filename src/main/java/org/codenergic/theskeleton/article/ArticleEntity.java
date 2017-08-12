package org.codenergic.theskeleton.article;

import org.codenergic.theskeleton.core.data.AbstractAuditingEntity;
import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "ts_article")
public class ArticleEntity extends AbstractAuditingEntity {

	@NotNull
	@Column(name = "title")
	private String title;
	@Column(name = "content")
	@Type(type = "text")
	private String content;

	@Override
	public ArticleEntity setId(String id) {
		super.setId(id);
		return this;
	}

	public String getTitle() {
		return title;
	}

	public ArticleEntity setTitle(String title) {
		this.title = title;
		return this;
	}

	public String getContent() {
		return content;
	}

	public ArticleEntity setContent(String content) {
		this.content = content;
		return this;
	}
}
