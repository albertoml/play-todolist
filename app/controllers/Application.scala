package controllers

import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import models.Task
import models.Task_user
import models.Category
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
				val listCat = Category.listTask(task)
				if(listCat.length==0){
					Task.delete(task.id.get)
					Ok("Tarea borrada con exito")
				}
				else{
					for( cat <- listCat) {
						Category.removeTask(task, cat)
					}
					Task.delete(task.id.get)
					Ok("La tarea se ha desvinculado de las categorias asociadas y ha sido borrada con exito")
				}
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

	def newCategory(login: String, cat: String) = Action {
		Task_user.buscarUser(login) match {
			case Some(user) => {
				val c = new Category(cat, login)
				Category.create(c)
				Status(201)(Json.toJson(c))
			}
			case None => {Status(400)("Usuario incorrecto")} 
		}
	}

	def deleteCategory(login: String, cat: String) = Action {
		Category.buscar(cat) match {
			case Some(c) => {
				Task_user.buscarUser(login) match {
					case Some(u) => {
						if(u.nombre == c.usuario){
							val listaTareas = Task.listCategory(c)
							if(listaTareas.length==0){
								Category.delete(c.id.get)
								Status(200)("Categoria borrada con exito")
							}
							else{
								Status(400)("La categoria tiene tareas asociadas")
							}
						}
						else{
							Status(400)("La categoria no pertenece al usuario")
						}
					}
					case None => {Status(400)("El usuario no existe")}
				}
			}
			case None => {Status(404)("La categoria no existe")}
		}
	}

	def viewCategories = Action{
		val jsonCategories = Json.toJson(Category.all())
		Ok(jsonCategories)
	}

	def viewUserCategories(login: String) = Action{
		Task_user.buscarUser(login) match {
			case Some(user) => {
				val jsonlist = Json.toJson(Category.listByUser(login))
				Status(200)(jsonlist)
			}
			case None => {Status(400)("Usuario incorrecto")} 
		}
	}

	def addCategory(id: Long, cat: String) = Action{
		Task.buscar(id) match {
			case Some(task) => {
				Category.buscar(cat) match {
					case Some(c) => {
						if(task.nombre == c.usuario){
							val listaTareas = Task.listCategory(c)
							if(listaTareas.contains(task)){
								Status(400)("La categoria ya contiene la tarea")
							}
							else{
								Category.addTask(task, c)
								Ok("Categoria " + cat + " añadida a la tarea")
							}
						}
						else{
							Status(400)("La tarea y la categoria deben ser del mismo usuario")
						}
					}
					case None => {Status(400)("La categoria no existe")} 
				}
			}
			case None => {Status(400)("La tarea no existe")}  
		}
	}

	def removeCategory(id: Long, cat: String) = Action{
		Task.buscar(id) match {
			case Some(task) => {
				Category.buscar(cat) match {
					case Some(c) => {
						if(task.nombre == c.usuario){
							val listaTareas = Task.listCategory(c)
							if(listaTareas.contains(task)){
								Category.removeTask(task, c)
								Ok("Categoria " + cat + " borrada de la tarea")
							}
							else{
								Status(404)("La categoria no contiene a la tarea")
							}
						}
						else{
							Status(400)("La tarea y la categoria deben ser del mismo usuario")
						}
					}
					case None => {Status(400)("La categoria no existe")} 
				}
			}
			case None => {Status(400)("La tarea no existe")}  
		}
	}

	def listTasksOfCategory(cat: String) = Action{
		Category.buscar(cat) match {
			case Some(c) => {
				val jsonTareas = Json.toJson(Task.listCategory(c))
				Ok(jsonTareas)
			}
			case None => {Status(400)("La categoria no existe")}
		}
	}

	def listCategoryOfTasks(id: Long) = Action{
		Task.buscar(id) match {
			case Some(t) => {
				val jsonCategories = Json.toJson(Category.listTask(t))
				Ok(jsonCategories)
			}
			case None => {Status(400)("La tarea no existe")}
		}
		
	}
}