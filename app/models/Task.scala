package models

import anorm._
import anorm.SqlParser._
import play.api.db._
import play.api.Play.current
import play.api.libs.json._
import play.api.libs.functional.syntax._
import java.util.Date

case class Task(label: String, nombre: String, fecha: Option[Date], id: Option[Long]=None)

object Task {

	val formatdate = Writes.dateWrites("yyyy-MM-dd")

	implicit val taskReads: Reads[Task] = (
		(JsPath \ "label").read[String] and
		(JsPath \ "nombre").read[String] and
		(JsPath \ "fecha").read[Option[Date]] and
		(JsPath \ "id").read[Option[Long]]
		)(Task.apply _ )

	implicit val taskWrites: Writes[Task] = (
		(JsPath \ "label").write[String] and
		(JsPath \ "nombre").write[String] and
		(JsPath \ "fecha").writeNullable[Date](formatdate) and
		(JsPath \ "id").write[Option[Long]]
		)(unlift(Task.unapply))

	val task = { 
  		get[String]("label") ~
  		get[String]("nombre") ~
  		get[Option[Date]]("fecha") ~ 
  		get[Option[Long]]("id") map {
    	case label~nombre~fecha~id => Task(label, nombre, fecha, id)
  		}
	}
  
	def all(): List[Task] = DB.withConnection { 
		implicit c => 
		SQL("select * from task where nombre = 'alberto'").as(task *)
	}

	def buscar(id: Long): Option[Task] = DB.withConnection {
		implicit c =>
		SQL("select * from task where id = {id}").on(
		'id-> id
		).as(Task.task.singleOpt)
	}
  
	def delete(id: Long) {
		DB.withConnection { implicit c =>
    	SQL("delete from task where id = {id}").on(
      	'id -> id
    	).executeUpdate()
  		}
	} 

	def buscarByUser(login: String): List[Task] =
		DB.withConnection { implicit c =>
		SQL("select * from task, task_user where task.nombre={login} and task_user.nombre=task.nombre"
		).on(
		'login -> login
		).as(task *)
	}

	def create(t: Task) {
		DB.withConnection { implicit c =>
    	SQL("insert into task (label, nombre, fecha) values ({label}, {nombre}, {fecha})").on(
      	'label -> t.label,
      	'nombre -> t.nombre,
      	'fecha -> t.fecha
    	).executeUpdate()
  		}
	}

	def orderAsc() : List[Task] = DB.withConnection {
		implicit c =>
		SQL("select * from task where fecha IS NOT NULL order by fecha ASC").as(task *)
	}

	def listarPorAnyo(anyo: Int) : List[Task] = DB.withConnection {
		implicit c =>
		SQL("select * from task where YEAR(fecha)={anyo}").on(
		'anyo -> anyo
		).as(task *)
	}


	def listCategory(cat: Category): List[Task] = DB.withConnection {
		implicit c =>
		SQL("select * from task, cat_task where cat_task.category={cat} and cat_task.task=task.id"
		).on(
		'cat -> cat.nombre_cat
		).as(task *)
	}
}
