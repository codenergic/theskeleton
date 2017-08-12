package org.codenergic.theskeleton.article;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@RunWith(SpringRunner.class)
@EnableSpringDataWebSupport
@WebMvcTest(controllers = { ArticleRestService.class }, secure = false)
public class ArticleRestServiceTest {
	@Autowired
	private MockMvc mockMvc;
	@Autowired
	private ObjectMapper objectMapper;
	@MockBean
	private ArticleService articleService;

	@Test
	public void testSaveArticle() throws Exception {
		when(articleService.saveArticle(any())).thenReturn(ArticleServiceTest.DUMMY_ARTICLE);
		MockHttpServletRequestBuilder request = post("/api/articles")
			.content("{\"title\": \"It's a disastah\"}")
			.contentType(MediaType.APPLICATION_JSON);
		MockHttpServletResponse response = mockMvc.perform(request)
			.andReturn()
			.getResponse();
		verify(articleService).saveArticle(any());
		assertThat(response.getStatus()).isEqualTo(200);
		assertThat(response.getContentAsByteArray())
			.isEqualTo(objectMapper.writeValueAsBytes(
				ArticleRestData.builder(ArticleServiceTest.DUMMY_ARTICLE).build())
			);
	}

	@Test
	public void testFindArticlesByTitleContaining() throws Exception {
		final Page<ArticleEntity> articles = new PageImpl<>(Arrays.asList(ArticleServiceTest.DUMMY_ARTICLE));
		when(articleService.findArticlesByTitleContaining(contains("disastah"), any())).thenReturn(articles);
		MockHttpServletRequestBuilder request = get("/api/articles?title=disastah")
			.contentType(MediaType.APPLICATION_JSON);
		MockHttpServletResponse response = mockMvc.perform(request)
			.andReturn()
			.getResponse();
		verify(articleService).findArticlesByTitleContaining(eq("disastah"), any());
		assertThat(response.getStatus()).isEqualTo(200);
		assertThat(response.getContentAsByteArray())
			.isEqualTo(objectMapper.writeValueAsBytes(articles.map(a -> ArticleRestData.builder(a).build())));
	}
}
