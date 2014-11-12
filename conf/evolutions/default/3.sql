# Crear categorias
 
# --- !Ups
CREATE SEQUENCE category_id_seq;

CREATE TABLE categorias (
	id integer NOT NULL DEFAULT nextval('category_id_seq'),
	nombre_cat varchar(50),
	usuario varchar(50) REFERENCES task_user (nombre)
);

CREATE TABLE cat_task (
	category varchar(50) REFERENCES categorias (nombre_cat),
	task integer REFERENCES task (id),
	PRIMARY KEY(category, task)
);


# --- !Downs

delete from categorias;
drop table categorias;
DROP SEQUENCE category_id_seq;