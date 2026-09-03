package com.github.phoswald.sample.web;

import java.time.Instant;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.github.phoswald.sample.news.NewsArticleEntity;
import com.github.phoswald.sample.news.NewsArticleService;

@RestController
@RequestMapping("/api/news-article")
public class NewsArticleController {

	record NewsArticleDto(String id, String site, String title, String summary, String author, Instant published) { }

	private final NewsArticleService service;

	NewsArticleController(NewsArticleService service) {
		this.service = service;
	}

	@GetMapping
	List<NewsArticleDto> getNewsArticles(@RequestParam(required = false) String site) {
		return service.getNewsArticles(site).stream().map(NewsArticleController::toDto).toList();
	}

	@GetMapping("/{id}")
	ResponseEntity<NewsArticleDto> getNewsArticle(@PathVariable String id) {
		return service.getNewsArticle(id)
			.map(NewsArticleController::toDto)
			.map(ResponseEntity::ok)
			.orElseGet(() -> ResponseEntity.notFound().build());
	}

	private static NewsArticleDto toDto(NewsArticleEntity entity) {
		return new NewsArticleDto(entity.getId(), entity.getSite(), entity.getTitle(), entity.getSummary(),
			entity.getAuthor(), entity.getPublished());
	}
}
