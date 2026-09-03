package com.github.phoswald.sample.web;

import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.oidcLogin;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.github.phoswald.sample.config.DummyClientConfig;
import com.github.phoswald.sample.config.SecurityConfig;
import com.github.phoswald.sample.news.NewsArticleEntity;
import com.github.phoswald.sample.news.NewsArticleService;

@WebMvcTest(NewsArticleController.class)
@Import({ SecurityConfig.class, DummyClientConfig.class })
class NewsArticleControllerTest {

	private static final NewsArticleEntity SAMPLE = new NewsArticleEntity("tech-daily-rust-in-the-kernel",
		"tech-daily", "Rust drivers reach parity in the mainline kernel", "The 7.2 merge window landed the bindings.",
		"Priya Raghunathan", Instant.parse("2026-09-01T05:00:00Z"));

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private NewsArticleService service;

	@Test
	void getNewsArticles_authenticated_returnsArticles() throws Exception {
		given(service.getNewsArticles(null)).willReturn(List.of(SAMPLE));

		mockMvc.perform(get("/api/news-article").with(oidcLogin()))
			.andExpect(status().isOk())
			.andExpect(content().json("""
				[ {
					"id": "tech-daily-rust-in-the-kernel",
					"site": "tech-daily",
					"title": "Rust drivers reach parity in the mainline kernel",
					"author": "Priya Raghunathan",
					"published": "2026-09-01T05:00:00Z"
				} ]
				"""));
	}

	@Test
	void getNewsArticles_site_filtersBySite() throws Exception {
		given(service.getNewsArticles("tech-daily")).willReturn(List.of(SAMPLE));

		mockMvc.perform(get("/api/news-article").param("site", "tech-daily").with(oidcLogin()))
			.andExpect(status().isOk())
			.andExpect(content().json("[ { \"site\": \"tech-daily\" } ]"));
	}

	@Test
	void getNewsArticle_existingId_returnsArticle() throws Exception {
		given(service.getNewsArticle(SAMPLE.getId())).willReturn(Optional.of(SAMPLE));

		mockMvc.perform(get("/api/news-article/{id}", SAMPLE.getId()).with(oidcLogin()))
			.andExpect(status().isOk())
			.andExpect(content().json("{ \"id\": \"tech-daily-rust-in-the-kernel\" }"));
	}

	@Test
	void getNewsArticle_unknownId_returnsNotFound() throws Exception {
		given(service.getNewsArticle("does-not-exist")).willReturn(Optional.empty());

		mockMvc.perform(get("/api/news-article/{id}", "does-not-exist").with(oidcLogin()))
			.andExpect(status().isNotFound());
	}

	@Test
	void getNewsArticles_notAuthenticated_redirectsToLogin() throws Exception {
		mockMvc.perform(get("/api/news-article"))
			.andExpect(status().is3xxRedirection());
	}
}
