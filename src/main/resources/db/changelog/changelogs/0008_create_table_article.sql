--liquibase formatted sql
--changeset diaxz:0008
--rollback drop table ts_article;

create table ts_article (
  id varchar(255) not null,
  status integer not null,
  created_by_client varchar(255),
  created_by varchar(255),
  created_date datetime,
  last_modified_by_client varchar(255),
  last_modified_by varchar(255),
  last_modified_date datetime,
  title varchar(200) not null,
  content varchar(65535),
  primary key (id)
);
