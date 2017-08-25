--liquibase formatted sql
--changeset vickyfaizal:0010
--rollback drop table ts_privilege;

create table ts_privilege (
  id varchar(255) not null,
  status integer not null,
  created_by_client varchar(255),
  created_by varchar(255),
  created_date timestamp,
  last_modified_by_client varchar(255),
  last_modified_by varchar(255),
  last_modified_date timestamp,
  description varchar(500),
  name varchar(200) not null,
  primary key (id)
);

alter table ts_privilege add constraint UK_4xeplqti9st45rdulqbll7q2h unique (name);