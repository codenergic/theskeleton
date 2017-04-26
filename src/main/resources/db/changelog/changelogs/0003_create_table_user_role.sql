--liquibase formatted sql
--changeset dianw:0003
--rollback drop table ts_user_role;

create table ts_user_role (
  id varchar(255) not null,
  status integer not null,
  created_by_client varchar(255),
  created_by varchar(255),
  created_date timestamp,
  last_modified_by_client varchar(255),
  last_modified_by varchar(255),
  last_modified_date timestamp,
  role_id varchar(255) not null,
  user_id varchar(255) not null,
  primary key (id)
);

alter table ts_user_role add constraint UKnwievip3f4fna9hi63w14oaef unique (user_id, role_id);

alter table ts_user_role add constraint FKhxifjuefm5xei96hfclie8vku foreign key (role_id) references ts_role (id);

alter table ts_user_role add constraint FK9c34aneirqb45uopoywi84q1t foreign key (user_id) references ts_user (id);
