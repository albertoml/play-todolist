import org.specs2.mutable._
import org.specs2.runner._
import org.junit.runner._

import play.api.test._
import play.api.test.Helpers._
import models.Task
import models.Category
import java.util.Date

@RunWith(classOf[JUnitRunner])
class ModelTask extends Specification {

	def dateIs(date: java.util.Date, str: String) = new java.text.SimpleDateFormat("yyyy-MM-dd").format(date) == str  
    def strToDate(str: String) = new java.text.SimpleDateFormat("yyyy-MM-dd").parse(str)

	"El modelo Task" should{

		val f = new Date(2014, 8, 21)
    	val f2 = new Date(2011, 8, 21)
    	val f3 = new Date(2011, 10, 22)

    	/*Suponemos que todos los nombre son correctos ya que 
    	la comprobacion del nombre lo hace el controlador*/

		val t = new Task("prueba", "alberto", Some(f))
		val t2 = new Task("prueba1", "domingo", None)
		val t3 = new Task("prueba2", "domingo", Some(f2))
		val t4 = new Task("prueba3", "alberto", Some(f3))
		val t5 = new Task("prueba4", "alberto", Some(f))
		val t6 = new Task("prueba5", "carlos", None)
		val t7 = new Task("prueba6", "alberto", Some(f2))
		val t8 = new Task("prueba7", "rocio", None)

		"listar por usuario por defecto (alberto) y borrado de tareas" in {
			running(FakeApplication(additionalConfiguration = inMemoryDatabase())){

				Task.create(t)
				Task.create(t4)
				Task.create(t5)
				Task.create(t7)

				val tareas = Task.all()
				tareas.length must equalTo(4)
				tareas(0).label must equalTo("prueba")
				tareas(1).nombre must equalTo("alberto")

				Task.delete(tareas(0).id.get)
				Task.delete(tareas(1).id.get)
				Task.delete(tareas(2).id.get)
				Task.delete(tareas(3).id.get)

				val tareasborradas = Task.all()
				tareasborradas.length must equalTo(0)
			}
		}

		"listar una tarea por id" in {
			running(FakeApplication(additionalConfiguration = inMemoryDatabase())){

				Task.create(t)
				Task.create(t3)

				val tareabuscada = Task.buscar(2)
				val tarea : Task = tareabuscada match {
					case Some(i) => i
					case None => null 
				}
				tarea.label must equalTo("prueba2")
				//2011-8-21 con la adapatacion a date 2011+1900-8+1-21 = 3911-09-21
				dateIs(tarea.fecha.get, "3911-09-21") must beTrue
				tarea.nombre must equalTo("domingo")
			}
		}

		"listar tareas por usuario" in {
			running(FakeApplication(additionalConfiguration = inMemoryDatabase())){

				Task.create(t2)
				Task.create(t3)

				val tareas = Task.buscarByUser("domingo")
				tareas.length must equalTo(2)
				tareas(1).label must equalTo("prueba2")
				tareas(0).nombre must equalTo("domingo")
				tareas(1).nombre must equalTo("domingo")
			}
		}

		"Ordenar fechas en orden ascendente" in {
			running(FakeApplication(additionalConfiguration = inMemoryDatabase())){

				Task.create(t5)
				Task.create(t4)
				Task.create(t7)

				val tareas = Task.all()
				tareas.length must equalTo(3)
				//comprobamos que esta desordenado
				tareas(0).label must equalTo("prueba4")
				tareas(1).label must equalTo("prueba3")
				tareas(2).label must equalTo("prueba6")

				val tareasordenadas = Task.orderAsc()
				//comprobamos que ha ordenado
				tareasordenadas(0).label must equalTo("prueba6")
				tareasordenadas(1).label must equalTo("prueba3")
				tareasordenadas(2).label must equalTo("prueba4")
			}
		}

		"Listar fechas por un año especifico" in {
			running(FakeApplication(additionalConfiguration = inMemoryDatabase())){

				Task.create(t7)
				Task.create(t4)

				//Adaptar el año a la clase date 2011+1900 = 3911
				val tareas = Task.listarPorAnyo(3911)
				tareas.length must equalTo(2)
				tareas(0).label must equalTo("prueba6")
				tareas(1).label must equalTo("prueba3")
			}
		}


		"Listar tareas por una categoria" in {
			running(FakeApplication(additionalConfiguration = inMemoryDatabase())){

				val cat = new Category("deportes", "alberto")
				Category.create(cat)
				val t = new Task("jugar al padel", "alberto", None, Some(1))
				val t2 = new Task("jugar al futbol", "alberto", None, Some(2))
				val t3 = new Task("jugar al golf", "alberto", None, Some(3))
				Task.create(t)
				Task.create(t2)
				Task.create(t3)
				Category.addTask(t, cat)
				Category.addTask(t2, cat)
				Category.addTask(t3, cat)

				val c1= Task.listCategory(cat)
				c1.length must equalTo(3)
				c1(0).nombre must equalTo("alberto")
				c1(0).label must equalTo("jugar al padel")
				c1(1).label must equalTo("jugar al futbol")
				c1(2).label must equalTo("jugar al golf")
			}
		}
	}
}
