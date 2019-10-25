create table migration
(
  id serial not null
    constraint migration_id_pk
    primary key,
  date timestamp default current_timestamp
)
;

create unique index migration_id_uindex on migration (id);

INSERT into migration (id) VALUES (1);