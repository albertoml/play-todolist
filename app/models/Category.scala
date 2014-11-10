package models

import anorm._
import anorm.SqlParser._
import play.api.db._
import play.api.Play.current
import play.api.libs.json._
import play.api.libs.functional.syntax._

case class Category(nombre_cat: String, usuario: String, id: Option[Long]=None)

object Category {

	implicit val categoryReads: Reads[Category] = (
		(JsPath \ "nombre_cat").read[String] and
		(JsPath \ "usuario").read[String] and
		(JsPath \ "id").read[Option[Long]]
		)(Category.apply _ )

	implicit val categoryWrites: Writes[Category] = (
		(JsPath \ "nombre_cat").write[String] and
		(JsPath \ "usuario").write[String] and
		(JsPath \ "id").write[Option[Long]]
		)(unlift(Category.unapply))

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

	def buscar(nombre: String): Option[Category] = DB.withConnection {
		implicit c =>
		SQL("select * from categorias where nombre_cat = {nombre}").on(
		'nombre-> nombre
		).as(Category.category.singleOpt)
	}

	def listByUser(usuario: String): List[Category] = DB.withConnection {
		implicit c =>
		SQL("select * from categorias, task_user where categorias.usuario={usuario} and task_user.nombre=categorias.usuario"
		).on(
		'usuario -> usuario
		).as(category *)
	}

	def addTask(t: Task, cat:Category){
		DB.withConnection { implicit c =>
    	SQL("insert into cat_task (category, task) values ({cat}, {t})").on(
      	'cat -> cat.nombre_cat,
      	't -> t.id.get
    	).executeUpdate()
  		}
	}

	def removeTask(t: Task, cat: Category){
		DB.withConnection { implicit c =>
    	SQL("delete from cat_task where category={cat} and task={t}").on(
      	'cat -> cat.nombre_cat,
      	't -> t.id.get
    	).executeUpdate()
  		}
	}

	def listTask(t: Task): List[Category] = DB.withConnection {
		implicit c =>
		SQL("select * from categorias, cat_task where cat_task.category=categorias.nombre_cat and cat_task.task={id}"
		).on(
		'id -> t.id.get
		).as(category *)
	}
}