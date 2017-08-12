package org.codenergic.theskeleton.article;

import org.codenergic.theskeleton.article.impl.ArticleServiceImpl;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

public interface ArticleService {
	static ArticleService newInstance(ArticleRepository articleRepository) {
		return new ArticleServiceImpl(articleRepository);
	}

	ArticleEntity saveArticle(@NotNull @Valid ArticleEntity article);

	Page<ArticleEntity> findArticlesByTitleContaining(@NotNull String title, Pageable pageable);
}
