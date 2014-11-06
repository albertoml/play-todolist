package controllers

import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import models.Task
import models.Task_user
import play.api.libs.json._
import java.util.Date

object Application extends Controller {

	val taskForm = Form(
  		"label" -> nonEmptyText
	)

	def index = Action {
	   	Redirect(routes.Application.tasks)
	}

	def tasks = Action{
		val jsonTareas = Json.toJson(Task.all())
	  	Ok(jsonTareas)
	}

	def task(id: Long) = Action{
		val tarea = Task.buscar(id)
		val jsonTarea = Json.toJson(tarea)
		if (jsonTarea == JsNull){
			NotFound("Tarea no encontrada")
		}
		else{
			Ok(jsonTarea) 
		}
	}

	def newTask = Action { implicit request =>
	  	taskForm.bindFromRequest.fold(
	    	errors => BadRequest("Formulario incorrecto"),
	    	label => {
	    		val t = new Task(label, "alberto", None)
	      		Task.create(t)
	      		val tarea: JsValue = Json.obj("label" -> label)
	      		Status(201)(tarea)
	    	}
	  	)
	}

	def deleteTask(id: Long) = Action {
		Task.buscar(id) match {
			case Some(task) => {
				Task.delete(task.id.get)
				Ok("Tarea borrada con exito")
			}
			case None => { NotFound("Tarea no encontrada") }
		}	
	}

	def tasksUser(login: String) = Action {
		Task_user.buscarUser(login) match {
			case Some(user) => {
				val jsonTareas = Json.toJson(Task.buscarByUser(login))
				Ok(jsonTareas)
			}
			case None => {NotFound("Usuario no encontrado")} 
		}
	}

	def newTaskUser(login: String) = Action { implicit request =>
		taskForm.bindFromRequest.fold(
	    	errors => BadRequest(views.html.index(Task.all(), errors)),
	    	label => {
	    		Task_user.buscarUser(login) match {
				case Some(user) => {
					val t = new Task(label, login, None)
	      			Task.create(t)
	      			val tarea: JsValue = Json.obj("label" -> label)
	      			Status(201)(tarea)
				}
				case None => {Status(400)("Usuario incorrecto")} 
				}
	    	}
	  	)
	}

	def rellenarFecha(fecha: String) : Option[Date] = {

		try{
			val array = fecha.split("-")
	    	val f = new Date((array(0).toInt-1900), (array(1).toInt-1), array(2).toInt)
	    	Some(f)
		} catch{
			case e: Exception => None
		}
	}

	def newTaskDate(fecha: String) = Action { implicit request =>
		taskForm.bindFromRequest.fold(
	    	errors => BadRequest(views.html.index(Task.all(), errors)),
	    	label => {
	    		val f_aux = rellenarFecha(fecha)
				val f : Date = f_aux match {
					case Some(i) => i
					case None => null 
				}
				if (f==null){
					Status(400)("Fecha en formato incorrecto")
				}else{

		    		val salida = f.getDate() + "/" + (f.getMonth()+1) + "/" + (f.getYear()+1900)
		      		val t = new Task(label, "alberto", f_aux)
		      		Task.create(t)
		      		val tarea: JsValue = Json.obj("label" -> label, "fecha" -> salida)
		      		Status(201)(tarea)
				} 
	    	}
	  	)
	}

	def newTaskUserDate(login: String, fecha: String) = Action { implicit request =>
		taskForm.bindFromRequest.fold(
	    	errors => BadRequest(views.html.index(Task.all(), errors)),
	    	label => {
	    		Task_user.buscarUser(login) match {
				case Some(user) => {
					val f_aux = rellenarFecha(fecha)
					val f : Date = f_aux match {
						case Some(i) => i
						case None => null 
					}
					if(f==null){
						Status(400)("Fecha en formato incorrecto")
					}else{

						val salida = f.getDate() + "/" + (f.getMonth()+1) + "/" + (f.getYear()+1900)
		      			val t = new Task(label, login, f_aux)
		      			Task.create(t)
		      			val tarea: JsValue = Json.obj("label" -> label, "fecha" -> salida)
		      			Status(201)(tarea)
					}
				}
				case None => {Status(400)("Usuario incorrecto")} 
				}
			}
		)
	}

	def orderByAsc = Action{
		val jsonTareas = Json.toJson(Task.orderAsc())
	  	Ok(jsonTareas)
	}

	def listarAnyo(anyo: Int) = Action {
		val jsonTareas = Json.toJson(Task.listarPorAnyo(anyo))
		Ok(jsonTareas)
	}
}