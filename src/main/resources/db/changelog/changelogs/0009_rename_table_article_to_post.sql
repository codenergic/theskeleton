--liquibase formatted sql
--changeset diaxz:0009

ALTER TABLE ts_article RENAME TO ts_post;
