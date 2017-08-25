--liquibase formatted sql
--changeset vickyfaizal:0012

insert into ts_privilege (id, name, description, status, created_date, last_modified_date)
values
  ('0000015bb4a150730007bf4700000000', 'user_write', 'User Write', 1, ${now}, ${now}),
  ('0000015bb4a150730007bf4700000001', 'user_update', 'User Update', 1, ${now}, ${now}),
  ('0000015bb4a150730007bf4700000002', 'user_read', 'User Read', 1, ${now}, ${now}),
  ('0000015bb4a150730007bf4700000003', 'user_read_all', 'User Read All', 1, ${now}, ${now}),
  ('0000015bb4a150730007bf4700000004', 'user_delete', 'User Delete', 1, ${now}, ${now}),
  ('0000015bb4a150730007bf4700000005', 'role_write', 'Role Write', 1, ${now}, ${now}),
  ('0000015bb4a150730007bf4700000006', 'role_update', 'Role Update', 1, ${now}, ${now}),
  ('0000015bb4a150730007bf4700000007', 'role_read', 'Role Read', 1, ${now}, ${now}),
  ('0000015bb4a150730007bf4700000008', 'role_read_all', 'Role Read All', 1, ${now}, ${now}),
  ('0000015bb4a150730007bf4700000009', 'role_delete', 'Role Delete', 1, ${now}, ${now});

insert into ts_role_privilege (id, role_id, privilege_id, status, created_date, last_modified_date)
values
  ('0000015bb4a150730007bf4700000000', '0000015bb4a150750007bf0700000000', '0000015bb4a150730007bf4700000000', 1, ${now}, ${now}),
  ('0000015bb4a150730007bf4700000001', '0000015bb4a150750007bf0700000000', '0000015bb4a150730007bf4700000001', 1, ${now}, ${now}),
  ('0000015bb4a150730007bf4700000002', '0000015bb4a150750007bf0700000000', '0000015bb4a150730007bf4700000002', 1, ${now}, ${now}),
  ('0000015bb4a150730007bf4700000003', '0000015bb4a150750007bf0700000000', '0000015bb4a150730007bf4700000003', 1, ${now}, ${now}),
  ('0000015bb4a150730007bf4700000004', '0000015bb4a150750007bf0700000000', '0000015bb4a150730007bf4700000004', 1, ${now}, ${now}),
  ('0000015bb4a150730007bf4700000005', '0000015bb4a150750007bf0700000000', '0000015bb4a150730007bf4700000005', 1, ${now}, ${now}),
  ('0000015bb4a150730007bf4700000006', '0000015bb4a150750007bf0700000000', '0000015bb4a150730007bf4700000006', 1, ${now}, ${now}),
  ('0000015bb4a150730007bf4700000007', '0000015bb4a150750007bf0700000000', '0000015bb4a150730007bf4700000007', 1, ${now}, ${now}),
  ('0000015bb4a150730007bf4700000008', '0000015bb4a150750007bf0700000000', '0000015bb4a150730007bf4700000008', 1, ${now}, ${now}),
  ('0000015bb4a150730007bf4700000009', '0000015bb4a150750007bf0700000000', '0000015bb4a150730007bf4700000009', 1, ${now}, ${now});
  