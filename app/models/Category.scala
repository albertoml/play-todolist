package models

import anorm._
import anorm.SqlParser._
import play.api.db._
import play.api.Play.current
import play.api.libs.json._
import play.api.libs.functional.syntax._

case class Category(nombre_cat: String, usuario: String, id: Option[Long]=None)

object Category {

	val category = {
		get[String]("nombre_cat") ~
  		get[String]("usuario") ~ 
  		get[Option[Long]]("id") map {
    	case nombre_cat~usuario~id => Category(nombre_cat, usuario, id)
  		}
	}

	def all(): List[Category] = DB.withConnection {
		implicit c =>
		SQL("select * from categorias").as(category *)
	}

	def create(cat: Category){
		DB.withConnection { implicit c =>
    	SQL("insert into categorias (nombre_cat, usuario) values ({nombre_cat}, {usuario})").on(
      	'nombre_cat -> cat.nombre_cat,
      	'usuario -> cat.usuario
    	).executeUpdate()
  		}
	}

	def delete(id: Long){
		DB.withConnection { implicit c =>
    	SQL("delete from categorias where id = {id}").on(
      	'id -> id
    	).executeUpdate()
  		}
	}

	def listByUser(usuario: String): List[Category] = DB.withConnection {
		implicit c =>
		SQL("select * from categorias, task_user where categorias.usuario={usuario} and task_user.nombre=categorias.usuario"
		).on(
		'usuario -> usuario
		).as(category *)
	}
}