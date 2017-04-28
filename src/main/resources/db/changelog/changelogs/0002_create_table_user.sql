--liquibase formatted sql
--changeset dianw:0002
--rollback drop table ts_user;

create table ts_user (
  id varchar(255) not null,
  status integer not null,
  created_by_client varchar(255),
  created_by varchar(255),
  created_date timestamp,
  last_modified_by_client varchar(255),
  last_modified_by varchar(255),
  last_modified_date timestamp,
  account_non_locked boolean not null,
  credentials_non_expired boolean not null,
  email varchar(500) not null,
  enabled boolean not null,
  expired_at timestamp null,
  password longtext not null,
  phone_number varchar(255),
  username varchar(255) not null,
  primary key (id)
);

alter table ts_user add constraint UK_dse4p2lm8il266muvi723q777 unique (email);

alter table ts_user add constraint UK_h2heum2ao735j82wjfost34l unique (username);