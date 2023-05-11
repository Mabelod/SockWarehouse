--liquibase formatted sql

-- changeset ivan:1
-- preconditions onFail:MARK_RAN onError:MARK_RAN
-- precondition-sql-check expectedResult:0 SELECT count(*) FROM information_schema.tables WHERE table_schema = 'public' AND table_name = 'socks'
CREATE TABLE socks
(
    id       BIGSERIAL PRIMARY KEY,
    color    VARCHAR(100),
    cotton_part  INTEGER,
    quantity INTEGER
);

-- changeset ivan:2
-- preconditions onFail:MARK_RAN onError:MARK_RAN
-- precondition-sql-check expectedResult:0 SELECT count(*) FROM information_schema.tables WHERE table_schema = 'public' AND table_name = 'registration'
CREATE TABLE registration
(
    id       BIGSERIAL PRIMARY KEY,
    date     TIMESTAMP,
    quantity INTEGER,
    condition   VARCHAR(7),
    socks_id BIGINT REFERENCES socks (id)
);