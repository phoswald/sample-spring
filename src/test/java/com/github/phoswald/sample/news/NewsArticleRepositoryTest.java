package com.github.phoswald.sample.news;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase.Replace;
import org.springframework.context.annotation.Import;

import com.github.phoswald.sample.TestcontainersConfiguration;

/**
 * Runs against the Testcontainers Postgres so that the Flyway migrations, and the seed
 * data they insert, are exercised as they are in production.
 */
@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Import(TestcontainersConfiguration.class)
class NewsArticleRepositoryTest {

	@Autowired
	private NewsArticleRepository repository;

	@Test
	void findAll_seedData_returnsMigratedArticles() {
		List<NewsArticleEntity> articles = repository.findAllByOrderByPublishedDesc();

		assertThat(articles).hasSize(6);
		assertThat(articles).extracting(NewsArticleEntity::getPublished)
			.isSortedAccordingTo(Comparator.<Instant>naturalOrder().reversed());
		assertThat(articles).extracting(NewsArticleEntity::getSite)
			.containsOnly("tech-daily", "science-weekly", "world-tribune");
	}

	@Test
	void findBySite_seedData_returnsMatchingArticlesNewestFirst() {
		List<NewsArticleEntity> articles = repository.findBySiteOrderByPublishedDesc("tech-daily");

		assertThat(articles).hasSize(3);
		assertThat(articles).extracting(NewsArticleEntity::getSite).containsOnly("tech-daily");
		assertThat(articles.getFirst().getId()).isEqualTo("tech-daily-rust-in-the-kernel");
		assertThat(articles.getFirst().getTitle()).isEqualTo("Rust drivers reach parity in the mainline kernel");
	}

	@Test
	void findBySite_unknownSite_returnsEmpty() {
		assertThat(repository.findBySiteOrderByPublishedDesc("nowhere-gazette")).isEmpty();
	}

	@Test
	void findById_seedData_returnsAllColumns() {
		NewsArticleEntity article = repository.findById("world-tribune-danube-flood-defences").orElseThrow();

		assertThat(article.getSite()).isEqualTo("world-tribune");
		assertThat(article.getTitle()).isEqualTo("Danube states agree joint flood defence fund");
		assertThat(article.getSummary()).startsWith("Eight riparian countries");
		assertThat(article.getAuthor()).isEqualTo("Sofia Marchetti");
		assertThat(article.getPublished()).isEqualTo("2026-08-25T16:20:00Z");
	}
}
