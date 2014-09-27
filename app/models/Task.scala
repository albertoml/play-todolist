package models

import anorm._
import anorm.SqlParser._
import play.api.db._
import play.api.Play.current
import play.api.libs.json._
import play.api.libs.functional.syntax._

case class Task(id: Long, label: String)

object Task {

	implicit val taskReads: Reads[Task] = (
		(JsPath \ "id").read[Long] and
		(JsPath \ "label").read[String]
		)(Task.apply _ )

	implicit val taskWrites: Writes[Task] = (
		(JsPath \ "id").write[Long] and
		(JsPath \ "label").write[String]
		)(unlift(Task.unapply) )


	val task = {
  		get[Long]("id") ~ 
  		get[String]("label") map {
    	case id~label => Task(id, label)
  		}
	}
  
	def all(): List[Task] = DB.withConnection { 
		implicit c => 
		SQL("select * from task where nombre='alberto'").as(task *)
	}

	def buscar(id: Long): Option[Task] = DB.withConnection {
		implicit c =>
		SQL("select * from task where id = {id}").on(
		'id-> id
		).as(Task.task.singleOpt)
	}
  
	def create(label: String) {
		DB.withConnection { implicit c =>
    	SQL("insert into task (label, nombre) values ({label}, 'alberto')").on(
      	'label -> label
    	).executeUpdate()
  		}
	}
  
	def delete(id: Long) {
		DB.withConnection { implicit c =>
    	SQL("delete from task where id = {id}").on(
      	'id -> id
    	).executeUpdate()
  		}
	} 

	def byUser(login: String): List[Task] =
		DB.withConnection { implicit c =>
		SQL("select * from task, task_user where task.nombre={login} and task_user.nombre=task.nombre"
		).on(
		'login -> login
		).as(task *)
	}
}
