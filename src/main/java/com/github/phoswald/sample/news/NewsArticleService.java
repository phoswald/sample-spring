package com.github.phoswald.sample.news;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class NewsArticleService {

	private final NewsArticleRepository repository;

	NewsArticleService(NewsArticleRepository repository) {
		this.repository = repository;
	}

	/**
	 * Returns the most recently published articles first, optionally restricted to a single site.
	 */
	public List<NewsArticleEntity> getNewsArticles(String site) {
		return site == null || site.isBlank()
			? repository.findAllByOrderByPublishedDesc()
			: repository.findBySiteOrderByPublishedDesc(site);
	}

	public Optional<NewsArticleEntity> getNewsArticle(String id) {
		return repository.findById(id);
	}
}
