package org.codenergic.theskeleton.article.impl;

import org.codenergic.theskeleton.article.ArticleEntity;
import org.codenergic.theskeleton.article.ArticleRepository;
import org.codenergic.theskeleton.article.ArticleService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class ArticleServiceImpl implements ArticleService {

	private ArticleRepository articleRepository;

	public ArticleServiceImpl(ArticleRepository articleRepository) {
		this.articleRepository = articleRepository;
	}

	@Override
	@Transactional
	public ArticleEntity saveArticle(ArticleEntity article) {
		return articleRepository.save(article);
	}

	@Override
	public Page<ArticleEntity> findArticlesByTitleContaining(String title, Pageable pageable) {
		return articleRepository.findByTitleContaining(title, pageable);
	}
}
