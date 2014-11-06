# Crear categorias
 
# --- !Ups
CREATE SEQUENCE category_id_seq;

CREATE TABLE categorias (
	id integer NOT NULL DEFAULT nextval('category_id_seq'),
	nombre_cat varchar(50),
	usuario varchar(50) REFERENCES task_user (nombre)
);


# --- !Downs

delete from categorias;
drop table categorias;
DROP SEQUENCE category_id_seq;