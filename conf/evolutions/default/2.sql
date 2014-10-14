# Crear usuarios y modificar task
 
# --- !Ups

CREATE TABLE task_user (
	nombre varchar(50) PRIMARY KEY
);

ALTER TABLE task ADD nombre varchar(50);
ALTER TABLE task ADD CONSTRAINT fknombre FOREIGN KEY (nombre) REFERENCES task_user(nombre);

ALTER TABLE task ADD fecha timestamp;

insert into task_user (nombre) values ('alberto');
insert into task_user (nombre) values ('domingo');
insert into task_user (nombre) values ('risto');
insert into task_user (nombre) values ('pablo');
insert into task_user (nombre) values ('carlos');
insert into task_user (nombre) values ('rocio');
 
# --- !Downs

delete from task_user;
drop table task_user;
alter table task drop nombre;
alter table task drop fknombre;
alter table task drop fecha;