--liquibase formatted sql
--changeset dianw:0004

insert into ts_role (id, code, description, status, created_date, last_modified_date)
values
  ('0000015bb4a150730007bf0700000000', 'system', 'System', 1, ${now}, ${now}),
  ('0000015bb4a150750007bf0700000000', 'admin', 'Administrator', 1, ${now}, ${now}),
  ('0000015bb4a150750007bf0700000001', 'user', 'User', 1, ${now}, ${now});

insert into ts_user 
  (id, username, password, email, phone_number, account_non_locked, 
   credentials_non_expired, enabled, expired_at, status, created_date, last_modified_date)
values
  ('0000015bb4a150750007bf0700000002', 'system', 'unhackable', 'system@server', null, 1, 1, 1, null, 1, ${now}, ${now}),
  ('0000015bb4a150750007bf0700000003', 'admin', '$2y$10$zpSon7SQecnMNQbWIyRqpeIo2cu0ahAgRogi.MHU7ZOC02hOOLD0C', 'admin@server', null, 1, 1, 1, null, 1, ${now}, ${now}),
  ('0000015bb4a150750007bf0700000004', 'user', '$2y$10$yAtKrf1wz7nyK7TG7WXbK.M4EDtfSMVFpncnlx3NtAi/arRf0nvGW', 'user@server', null, 1, 1, 1, null, 1, ${now}, ${now});

insert into ts_user_role (id, role_id, user_id, status, created_date, last_modified_date)
values
  ('0000015bb4a150750007bf0700000005', '0000015bb4a150730007bf0700000000', '0000015bb4a150750007bf0700000002', 1, ${now}, ${now}),
  ('0000015bb4a150760007bf0700000000', '0000015bb4a150750007bf0700000000', '0000015bb4a150750007bf0700000003', 1, ${now}, ${now}),
  ('0000015bb4a150760007bf0700000001', '0000015bb4a150750007bf0700000001', '0000015bb4a150750007bf0700000004', 1, ${now}, ${now});
  