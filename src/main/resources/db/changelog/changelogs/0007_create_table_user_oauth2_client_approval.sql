--liquibase formatted sql
--changeset dianw:0007

create table ts_user_oauth2_client_approval (
  id varchar(255) not null,
  status integer not null,
  created_by_client varchar(255),
  created_by varchar(255),
  created_date timestamp,
  last_modified_by_client varchar(255),
  last_modified_by varchar(255),
  last_modified_date timestamp,
  approval_status integer not null,
  scope varchar(200) not null,
  oauth2_client_id varchar(255) not null,
  user_id varchar(255) not null,
  primary key (id));

alter table ts_user_oauth2_client_approval add constraint UKkv28pemist8h46vav202ho5el unique (user_id, oauth2_client_id, scope);
alter table ts_user_oauth2_client_approval add constraint FK9424ha2thpaixxma8yvph6y55 foreign key (oauth2_client_id) references ts_oauth2_client (id);
alter table ts_user_oauth2_client_approval add constraint FK3b81sbpafe70v6piinfvb8xg7 foreign key (user_id) references ts_user (id);