--liquibase formatted sql
--changeset dianw:0001
--rollback drop table ts_role;

create table ts_role (
  id varchar(255) not null,
  status integer not null,
  created_by_client varchar(255),
  created_by varchar(255),
  created_date datetime,
  last_modified_by_client varchar(255),
  last_modified_by varchar(255),
  last_modified_date datetime,
  code varchar(200) not null,
  description varchar(500),
  primary key (id)
);

alter table ts_role add constraint UK_e6jee71co0x4yh2nehqe4sxti unique (code);
