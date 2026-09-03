-- Registry of the Spring Modulith event publications. This table belongs to Spring Modulith,
-- so it keeps that project's naming instead of the trailing-underscore convention used elsewhere.
-- Copied from org/springframework/modulith/events/jdbc/schemas/v2/schema-postgresql.sql.
create table event_publication (
    id                     uuid not null,
    listener_id            text not null,
    event_type             text not null,
    serialized_event       text not null,
    publication_date       timestamp with time zone not null,
    completion_date        timestamp with time zone,
    status                 text,
    completion_attempts    int,
    last_resubmission_date timestamp with time zone,
    primary key (id)
);

create index event_publication_serialized_event_hash_idx on event_publication using hash(serialized_event);
create index event_publication_by_completion_date_idx on event_publication (completion_date);
