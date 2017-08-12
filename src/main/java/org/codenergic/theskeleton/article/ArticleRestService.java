package org.codenergic.theskeleton.article;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/articles")
public class ArticleRestService {

	@Autowired
	private ArticleService articleService;

	@PostMapping
	public ArticleRestData saveArticle(@RequestBody @Valid ArticleRestData articleRestData) {
		return convertEntityToRestData(articleService.saveArticle(articleRestData.toEntity()));
	}

	@GetMapping
	public Page<ArticleRestData> findArticlesByTitleContaining(
		@RequestParam(name = "title", defaultValue = "") String title, Pageable pageable) {
		return articleService.findArticlesByTitleContaining(title, pageable)
			.map(this::convertEntityToRestData);
	}

	private ArticleRestData convertEntityToRestData(ArticleEntity article) {
		return article == null ? null : ArticleRestData.builder(article).build();
	}

}
