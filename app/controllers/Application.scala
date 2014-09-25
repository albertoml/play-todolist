package controllers

import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import models.Task
import play.api.libs.json._


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
	    	errors => BadRequest(views.html.index(Task.all(), errors)),
	    	label => {
	      		Task.create(label)
	      		Redirect(routes.Application.tasks)
	    	}
	  	)
	}

	def deleteTask(id: Long) = Action {
  		Task.delete(id)
  		Redirect(routes.Application.tasks)
	}
	
}