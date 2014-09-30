package models

import anorm._
import anorm.SqlParser._
import play.api.db._
import play.api.Play.current
import play.api.libs.json._
import play.api.libs.functional.syntax._

case class Task_user(nombre: String)

object Task_user {

	/*implicit val task_userReads: Reads[Task_user] = (
		(JsPath \ "nombre").read[String]
		)(Task_user.apply _ )

	implicit val task_userWrites: Writes[Task_user] = (
		(JsPath \ "nombre").write[String]
		)(unlift(Task_user.unapply) )*/


	val task_user = {
  		get[String]("nombre") map {
    	case nombre => Task_user(nombre)
  		}
	}

	def buscarUser(nombre: String): Option[Task_user] = DB.withConnection {
		implicit c =>
		SQL("select nombre from task_user where nombre = {nombre}").on(
		'nombre-> nombre
		).as(Task_user.task_user.singleOpt)
	}
}