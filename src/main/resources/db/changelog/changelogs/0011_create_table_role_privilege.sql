--liquibase formatted sql
--changeset vickyfaizal:0011
--rollback drop table ts_role_privilege;

create table ts_role_privilege (
  id varchar(255) not null,
  status integer not null,
  created_by_client varchar(255),
  created_by varchar(255),
  created_date timestamp,
  last_modified_by_client varchar(255),
  last_modified_by varchar(255),
  last_modified_date timestamp,
  privilege_id varchar(255) not null,
  role_id varchar(255) not null,
  primary key (id))
;

alter table ts_role_privilege add constraint UK2idi7p1xqtq22rw7mntd9jeuh unique (role_id, privilege_id);

alter table ts_role_privilege add constraint FK20lmuf3rtmev37a70521ow6vt foreign key (privilege_id) references ts_privilege (id);

alter table ts_role_privilege add constraint FKsgyeuvfx5qjf7uaru1n8psb8a foreign key (role_id) references ts_role (id);