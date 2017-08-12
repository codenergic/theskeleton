package org.codenergic.theskeleton.article;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ArticleRestData {
	@JsonProperty
	private String id;
	@JsonProperty
	private String title;
	@JsonProperty
	private String content;

	public ArticleRestData() {}

	private ArticleRestData(Builder builder) {
		setId(builder.id);
		setTitle(builder.title);
		setContent(builder.content);
	}

	public static Builder newBuilder() {
		return new Builder();
	}
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}
	public ArticleEntity toEntity() {
		ArticleEntity articleEntity = new ArticleEntity();
		articleEntity.setId(id);
		articleEntity.setTitle(title);
		articleEntity.setContent(content);
		return articleEntity;
	}

	public static Builder builder(ArticleEntity article) {
		return newBuilder().title(article.getTitle()).content(article.getContent());
	}


	public static final class Builder {
		private String id;
		private String title;
		private String content;

		public Builder() {
		}

		public Builder id(String val) {
			id = val;
			return this;
		}

		public Builder title(String val) {
			title = val;
			return this;
		}

		public Builder content(String val) {
			content = val;
			return this;
		}

		public ArticleRestData build() {
			return new ArticleRestData(this);
		}
	}
}
