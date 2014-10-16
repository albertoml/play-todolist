import org.specs2.mutable._
import org.specs2.runner._
import org.junit.runner._

import play.api.test._
import play.api.test.Helpers._
import models.Task
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

		val t = new Task(0, "prueba", "alberto", Some(f))
		val t2 = new Task(1, "prueba1", "domingo", None)
		val t3 = new Task(2, "prueba2", "domingo", Some(f2))
		val t4 = new Task(3, "prueba3", "alberto", Some(f3))
		val t5 = new Task(4, "prueba4", "alberto", Some(f))
		val t6 = new Task(5, "prueba5", "carlos", None)
		val t7 = new Task(6, "prueba6", "alberto", Some(f2))
		val t8 = new Task(7, "prueba7", "rocio", None)

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

				Task.delete(tareas(0).id)
				Task.delete(tareas(1).id)
				Task.delete(tareas(2).id)
				Task.delete(tareas(3).id)

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

				Task.delete(0)
				Task.delete(1)

				//no me deja quitarlo pero no tiene ningun sentido
				//PREGUNTAR A DOMINGO
				val tareasborradas = Task.all()
				tareasborradas.length must equalTo(0)
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

				Task.delete(tareas(0).id)
				Task.delete(tareas(1).id)

				val tareasborradas = Task.buscarByUser("domingo")
				tareasborradas.length must equalTo(0)
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

				Task.delete(tareas(0).id)
				Task.delete(tareas(1).id)
				Task.delete(tareas(2).id)

				val tareasborradas = Task.all()
				tareasborradas.length must equalTo(0)
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

				Task.delete(tareas(0).id)
				Task.delete(tareas(1).id)

				val tareasborradas = Task.listarPorAnyo(2011)
				tareasborradas.length must equalTo(0)
			}
		}
	}
}
