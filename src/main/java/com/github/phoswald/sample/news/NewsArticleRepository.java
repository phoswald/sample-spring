package com.github.phoswald.sample.news;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface NewsArticleRepository extends JpaRepository<NewsArticleEntity, String> {

	List<NewsArticleEntity> findAllByOrderByPublishedDesc();

	List<NewsArticleEntity> findBySiteOrderByPublishedDesc(String site);
}
