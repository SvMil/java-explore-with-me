DROP TABLE IF EXISTS compilation_events;
DROP TABLE IF EXISTS requests;
DROP TABLE IF EXISTS compilations;
DROP TABLE IF EXISTS events;
DROP TABLE IF EXISTS users;
DROP TABLE IF EXISTS categories;

CREATE TABLE IF NOT EXISTS categories (
    id   BIGSERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS users (
    id    BIGSERIAL PRIMARY KEY,
    name  VARCHAR(250),
    email VARCHAR(254) NOT NULL UNIQUE
);


CREATE TABLE IF NOT EXISTS events (
    id                 BIGSERIAL PRIMARY KEY,
    title              VARCHAR(120) NOT NULL,
    annotation         VARCHAR(2000) NOT NULL,
    description        VARCHAR(7000) NOT NULL,
    initiator_id       INTEGER NOT NULL REFERENCES users(id),
    category_id        INTEGER NOT NULL REFERENCES categories(id),
    event_date         TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    lat                FLOAT,
    lon                FLOAT,
    paid               BOOLEAN DEFAULT FALSE,
    participant_limit  INTEGER DEFAULT 0,
    request_moderation BOOLEAN DEFAULT TRUE,
    state              VARCHAR(50) NOT NULL,
    created_on         TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    published_on       TIMESTAMP WITHOUT TIME ZONE
);


CREATE TABLE IF NOT EXISTS compilations (
    id     BIGSERIAL PRIMARY KEY,
    title  VARCHAR(50) NOT NULL,
    pinned BOOLEAN DEFAULT FALSE
);

CREATE TABLE IF NOT EXISTS requests (
    id           BIGSERIAL PRIMARY KEY,
    event_id     INTEGER NOT NULL,
    requester_id INTEGER NOT NULL,
    status       VARCHAR(50) NOT NULL,
    created      TIMESTAMP WITHOUT TIME ZONE NOT NULL
);


CREATE TABLE IF NOT EXISTS compilation_events (
    compilation_id BIGINT NOT NULL REFERENCES compilations(id) ON DELETE CASCADE,
    event_id       BIGINT NOT NULL REFERENCES events(id) ON DELETE CASCADE,
    PRIMARY KEY (compilation_id, event_id)
);