# Crear usuarios y modificar task
 
# --- !Ups

CREATE TABLE task_user (
	nombre varchar(50)
);

insert into task_user (nombre) values ("alberto");
insert into task_user (nombre) values ("domingo");
insert into task_user (nombre) values ("risto");
insert into task_user (nombre) values ("pablo");
insert into task_user (nombre) values ("carlos");
insert into task_user (nombre) values ("rocio");
 
# --- !Downs

delete from task_user;
drop table task_user;
 
