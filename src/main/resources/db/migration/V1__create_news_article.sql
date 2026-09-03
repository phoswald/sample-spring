create table news_article_ (
    id_        varchar(64)  not null,
    site_      varchar(64)  not null,
    title_     varchar(256) not null,
    summary_   varchar(2000),
    author_    varchar(128),
    published_ timestamp with time zone,
    constraint pk_news_article_ primary key (id_)
);

create index ix_news_article_site_published_ on news_article_ (site_, published_ desc);
