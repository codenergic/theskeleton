--liquibase formatted sql
--changeset dianw:0005

create table ts_oauth2_client (
  authorized_grant_types varchar(150) not null,
  id varchar(255) not null,
  status integer not null,
  created_by_client varchar(255),
  created_by varchar(255),
  created_date timestamp,
  last_modified_by_client varchar(255),
  last_modified_by varchar(255),
  last_modified_date timestamp,
  auto_approve boolean,
  client_secret longtext not null,
  description varchar(500),
  name varchar(300) not null,
  registered_redirect_uris longtext,
  resource_ids varchar(255),
  scopes longtext,
  scoped boolean,
  secret_required boolean,
  primary key (id));
