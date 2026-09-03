package com.github.phoswald.sample.news;

import java.time.Instant;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "news_article_")
public class NewsArticleEntity {

	@Id
	@Column(name = "id_", length = 64, nullable = false)
	private String id;

	@Column(name = "site_", length = 64, nullable = false)
	private String site;

	@Column(name = "title_", length = 256, nullable = false)
	private String title;

	@Column(name = "summary_", length = 2000)
	private String summary;

	@Column(name = "author_", length = 128)
	private String author;

	@Column(name = "published_")
	private Instant published;

	protected NewsArticleEntity() { }

	public NewsArticleEntity(String id, String site, String title, String summary, String author, Instant published) {
		this.id = id;
		this.site = site;
		this.title = title;
		this.summary = summary;
		this.author = author;
		this.published = published;
	}

	public String getId() {
		return id;
	}

	public String getSite() {
		return site;
	}

	public String getTitle() {
		return title;
	}

	public String getSummary() {
		return summary;
	}

	public String getAuthor() {
		return author;
	}

	public Instant getPublished() {
		return published;
	}

	@Override
	public boolean equals(Object obj) {
		return obj instanceof NewsArticleEntity other && Objects.equals(id, other.id);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(id);
	}

	@Override
	public String toString() {
		return "NewsArticleEntity[id=" + id + "]";
	}
}
