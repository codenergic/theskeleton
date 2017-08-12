package org.codenergic.theskeleton.article;

import org.codenergic.theskeleton.user.UserEntity;
import org.codenergic.theskeleton.user.UserRestData;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.util.Arrays;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

public class ArticleServiceTest {

	@Mock
	private ArticleRepository articleRepository;

	private ArticleService articleService;

	public static final ArticleEntity DUMMY_ARTICLE = new ArticleEntity()
		.setId(UUID.randomUUID().toString())
		.setTitle("It's a disastah")
		.setContent("some text are **bold**,//italic//,__underline__ or even ~~strikethrough~~");

	@Before
	public void init() {
		MockitoAnnotations.initMocks(this);
		this.articleService = ArticleService.newInstance(articleRepository);
	}

	@Test
	public void testSaveArticle() {
		articleService.saveArticle(DUMMY_ARTICLE);
	}

	@Test
	public void testFindArticlesByTitleContaining() {
		Page<ArticleEntity> dbResult = new PageImpl<>(Arrays.asList(DUMMY_ARTICLE));
		when(articleRepository.findByTitleContaining(eq("disastah"), any()))
			.thenReturn(dbResult);
		Page<ArticleEntity> result = articleRepository.findByTitleContaining("disastah", null);
		assertThat(result.getNumberOfElements()).isEqualTo(1);
		assertThat(result).isEqualTo(dbResult);
		verify(articleRepository).findByTitleContaining(eq("disastah"), any());
	}

}
