package models

import anorm._
import anorm.SqlParser._
import play.api.db._
import play.api.Play.current
import play.api.libs.json._
import play.api.libs.functional.syntax._
import java.util.Date

case class Task(id: Long, label: String, nombre: String, fecha: Option[Date])

object Task {

	implicit val taskReads: Reads[Task] = (
		(JsPath \ "id").read[Long] and
		(JsPath \ "label").read[String] and
		(JsPath \ "nombre").read[String] and
		(JsPath \ "fecha").read[Option[Date]]
		)(Task.apply _ )

	implicit val taskWrites: Writes[Task] = (
		(JsPath \ "id").write[Long] and
		(JsPath \ "label").write[String] and
		(JsPath \ "nombre").write[String] and
		(JsPath \ "fecha").write[Option[Date]]
		)(unlift(Task.unapply) )

	val task = {
  		get[Long]("id") ~ 
  		get[String]("label") ~
  		get[String]("nombre") ~
  		get[Option[Date]]("fecha") map {
    	case id~label~nombre~fecha => Task(id, label, nombre, fecha)
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
}
