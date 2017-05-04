--liquibase formatted sql
--changeset dianw:0006

insert into ts_oauth2_client 
  (id, authorized_grant_types, auto_approve, client_secret, description, name,
  registered_redirect_uris, scopes, scoped, secret_required, status, created_date,
  last_modified_date)
values
  ('0000015bb4a150850007bf0700000000', 'password,authorization_code,implicit,refresh_token', 0,
  '$2a$06$F0YQTRPvG8M9SPzIgk49GOgwOH7jcHaT2elonRrs9mSCftNtEgMmi', 'Default client', 'default',
  'http://localhost:9000/,http://localhost:9000/oauth', 'read,write', 1, 1, 1, ${now}, ${now});