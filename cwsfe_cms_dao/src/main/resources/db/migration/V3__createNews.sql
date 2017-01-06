CREATE TABLE CMS_FOLDERS (
    id           NUMERIC(6, 0) PRIMARY KEY,
    parent_id    NUMERIC(6, 0) REFERENCES CMS_FOLDERS (ID),
    folder_name  VARCHAR(100) NOT NULL UNIQUE,
    order_number NUMERIC(4, 0),
    status       CHAR(1)      NOT NULL
);
CREATE SEQUENCE CMS_FOLDERS_S START 1 CACHE 1;

CREATE TABLE CMS_NEWS_TYPES (
    ID     NUMERIC(6) PRIMARY KEY NOT NULL,
    TYPE   VARCHAR(100)           NOT NULL UNIQUE,
    STATUS CHAR(1)                NOT NULL
);
CREATE SEQUENCE CMS_NEWS_TYPES_S START 1 CACHE 1;

CREATE TABLE CMS_NEWS (
    id            NUMERIC(6, 0) PRIMARY KEY,
    author_id     NUMERIC(6, 0) NOT NULL REFERENCES CMS_AUTHORS (id),
    news_type_id  NUMERIC(6, 0) NOT NULL REFERENCES CMS_NEWS_TYPES (id),
    folder_id     NUMERIC(6, 0) NOT NULL REFERENCES CMS_FOLDERS (id),
    creation_date TIMESTAMP     NOT NULL,
    news_code     VARCHAR(300)  NOT NULL UNIQUE,
    status        CHAR(1)       NOT NULL
);
CREATE SEQUENCE CMS_NEWS_S START 1 CACHE 1;

CREATE TABLE CMS_NEWS_I18N_CONTENTS (
    id               NUMERIC(6, 0) PRIMARY KEY,
    news_id          NUMERIC(6, 0) NOT NULL REFERENCES CMS_NEWS (id),
    language_id      NUMERIC(3, 0) NOT NULL REFERENCES cms_languages (id),
    news_title       VARCHAR(100)  NOT NULL,
    news_shortcut    TEXT,
    news_description TEXT,
    status           CHAR(1)       NOT NULL
);
CREATE SEQUENCE CMS_NEWS_I18N_CONTENTS_S START 1 CACHE 1;

CREATE TABLE CMS_NEWS_IMAGES (
    id        NUMERIC(12, 0) PRIMARY KEY,
    news_id   NUMERIC(6, 0)  NOT NULL REFERENCES CMS_NEWS (id),
    title     VARCHAR(100)   NOT NULL,
    file_name VARCHAR(250)   NOT NULL,
    file_size NUMERIC(12, 0) NOT NULL,
    width     NUMERIC(6, 0),
    height    NUMERIC(6, 0),
    mime_type VARCHAR(100),
    content   BYTEA          NOT NULL,
    created   TIMESTAMP      NOT NULL,
    status    CHAR(1)        NOT NULL
);
CREATE INDEX CMS_NEWS_IMAGES_NEWS_ID_INDEX
    ON CMS_NEWS (id);
CREATE SEQUENCE CMS_NEWS_IMAGES_S START 1 CACHE 1;

CREATE TABLE CMS_TEXT_I18N_CATEGORIES (
    ID       NUMERIC(3, 0) NOT NULL PRIMARY KEY,
    CATEGORY VARCHAR(100)  NOT NULL,
    STATUS   CHAR(1)       NOT NULL
);
CREATE SEQUENCE CMS_TEXT_I18N_CATEGORIES_S START 1 CACHE 1;

CREATE TABLE CMS_TEXT_I18N (
    ID            NUMERIC(6, 0) NOT NULL PRIMARY KEY,
    LANG_ID       NUMERIC(6)    NOT NULL REFERENCES CMS_LANGUAGES (ID),
    I18N_CATEGORY NUMERIC(3, 0) NOT NULL REFERENCES CMS_TEXT_I18N_CATEGORIES (ID),
    I18N_KEY      VARCHAR(100)  NOT NULL,
    I18N_TEXT     VARCHAR(500),
    UNIQUE (LANG_ID, I18N_CATEGORY, I18N_KEY)
);
CREATE SEQUENCE CMS_TEXT_I18N_S START 1 CACHE 1;
