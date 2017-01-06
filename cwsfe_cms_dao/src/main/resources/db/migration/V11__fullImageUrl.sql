ALTER TABLE cms_news_images
    ADD COLUMN URL VARCHAR(1000);
ALTER TABLE blog_post_images
    ADD COLUMN URL VARCHAR(1000);

INSERT INTO CMS_GLOBAL_PARAMS (ID, CODE, DEFAULT_VALUE, VALUE, DESCRIPTION) VALUES (
    nextval('CMS_GLOBAL_PARAMS_S'), 'CWSFE_CMS_MAIN_URL', 'https://cwsfe.eu/CWSFE_CMS', 'https://cwsfe.eu/CWSFE_CMS',
    'CWSFE CMS url'
);
