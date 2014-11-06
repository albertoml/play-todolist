import org.specs2.mutable._
import org.specs2.runner._
import org.junit.runner._
import play.api.test._
import play.api.test.Helpers._
import models.Task
import java.util.Date

@RunWith(classOf[JUnitRunner])
class Application extends Specification {

	val f = new Date(2014, 8, 21)
	val f2 = new Date(2011, 8, 21)
	val f3 = new Date(2011, 10, 22)

	val t1 = new Task("prueba", "alberto", Some(f))
	val t2 = new Task("prueba1", "domingo", None)
	val t3 = new Task("prueba2", "domingo", Some(f2))
	val t4 = new Task("prueba3", "alberto", Some(f3))
	val t5 = new Task("prueba4", "alberto", Some(f))
	val t6 = new Task("prueba5", "carlos", None)
	val t7 = new Task("prueba6", "alberto", Some(f2))
	val t8 = new Task("prueba7", "rocio", None)

	"La aplicacion" should{

		"enviar un error HTTP 404 en una peticion inexistente" in new WithApplication{
      		route(FakeRequest(GET, "/boum")) must beNone
    	}

    	"redireccionar la pagina index a /tasks" in new WithApplication{
    		val home = route(FakeRequest(GET, "/")).get
    		status(home) must equalTo(303)
    		redirectLocation(home) must equalTo(Some("/tasks"))
    	}

    	"mostrar las tareas de (alberto) al llamar a /tasks" in new WithApplication{
    		running(FakeApplication(additionalConfiguration = inMemoryDatabase())){
				Task.create(t4)
    			val home = route(FakeRequest(GET, "/tasks")).get
    			status(home) must equalTo(200)
				contentAsString(home) must contain ("\"nombre\":\"alberto\"")
				//Task.delete(1)
			}
    	}

    	"mostrar una tarea por id" in new WithApplication{
    		running(FakeApplication(additionalConfiguration = inMemoryDatabase())){
				Task.create(t2)
    			val home = route(FakeRequest(GET, "/tasks/" + 1)).get
    			status(home) must equalTo(200)
				contentAsString(home) must contain ("\"label\":\"prueba1\"")
				//Task.delete(1)
			}
    	}

    	"mostrar error 404 cuando consultas una tarea inexistente" in new WithApplication{
    		running(FakeApplication(additionalConfiguration = inMemoryDatabase())){
    			Task.create(t1)
    			val home = route(FakeRequest(GET, "/tasks/" + 2)).get
    			status(home) must equalTo(404)
    			contentAsString(home) must contain ("Tarea no encontrada")
    			//Task.delete(1)
    		}
    	}

    	"crear una tarea cuando envias un formulario con el campo label" in new WithApplication{
    		val Some(home) = route(FakeRequest(POST, "/tasks").withFormUrlEncodedBody(("label", "Comprar tomate")))
    		status(home) must equalTo(201)
    		contentAsString(home) must contain("Comprar tomate")
    	}

    	"mostrar error al crear una tarea sin el formulario" in new WithApplication{
    		val home = route(FakeRequest(POST, "/tasks")).get
    		status(home) must equalTo(400)
    		contentAsString(home) must contain("Formulario incorrecto")
    	}

    	"borrar una tarea especificando el id" in new WithApplication{
    		running(FakeApplication(additionalConfiguration = inMemoryDatabase())){
    			Task.create(t2)
    			val home = route(FakeRequest(DELETE, "/tasks/1")).get
    			status(home) must equalTo(200)
    			contentAsString(home) must contain("Tarea borrada con exito")
    		}
    	}

    	"no borrar la tarea si la id es incorrecta" in new WithApplication{
    		running(FakeApplication(additionalConfiguration = inMemoryDatabase())){
    			Task.create(t2)
    			val home = route(FakeRequest(DELETE, "/tasks/2")).get
    			status(home) must equalTo(404)
    			contentAsString(home) must contain("Tarea no encontrada")
    			//Task.delete(1)
    		}
    	}

    	"buscar tareas por usuario especificado" in new WithApplication{
    		running(FakeApplication(additionalConfiguration = inMemoryDatabase())){
    			Task.create(t2)
    			Task.create(t3)
    			Task.create(t4)
    			val home = route(FakeRequest(GET, "/domingo/tasks")).get
    			status(home) must equalTo(200)
    			contentAsString(home) must contain("prueba1")
    			contentAsString(home) must contain("prueba2")
    			contentAsString(home) must not contain("prueba3")
    			//Task.delete(1)
    			//Task.delete(2)
    			//Task.delete(3)
    		}
    	}

    	"mostrar error al consultar tareas de un usuario inexistente" in new WithApplication{
			val home = route(FakeRequest(GET, "/eduardonoesta/tasks")).get
			status(home) must equalTo(404)
			contentAsString(home) must contain("Usuario no encontrado")
    	}

    	"crear una tarea especificando el usuario" in new WithApplication{
    		val Some(home) = route(FakeRequest(POST, "/domingo/tasks").withFormUrlEncodedBody(("label", "Comprar lechuga")))
    		status(home) must equalTo(201)
    		contentAsString(home) must contain("Comprar lechuga")
    	}

    	"no crear la tarea si el usuario no existe" in new WithApplication{
    		val Some(home) = route(FakeRequest(POST, "/eduardonoesta/tasks").withFormUrlEncodedBody(("label", "Comprar naranjas")))
    		status(home) must equalTo(400)
    		contentAsString(home) must contain("Usuario incorrecto")
    	}

    	"crear una tarea especificando la fecha" in new WithApplication{
    		val Some(home) = route(FakeRequest(POST, "/tasks/2014-10-23").withFormUrlEncodedBody(("label", "Comprar oregano")))
    		status(home) must equalTo(201)
    		contentAsString(home) must contain("Comprar oregano")
    		contentAsString(home) must contain("23/10/2014")
    	}

    	"crear una tarea especificando la fecha y el usuario" in new WithApplication{
    		val Some(home) = route(FakeRequest(POST, "/domingo/tasks/2014-10-23").withFormUrlEncodedBody(("label", "Comprar oregano")))
    		status(home) must equalTo(201)
    		contentAsString(home) must contain("Comprar oregano")
    		contentAsString(home) must contain("23/10/2014")
    	}

    	"no crear una tarea si la fecha es incorrecta" in new WithApplication{
    		val Some(home) = route(FakeRequest(POST, "/tasks/20df14-sfd10-23").withFormUrlEncodedBody(("label", "Comprar oregano")))
    		status(home) must equalTo(400)
    		contentAsString(home) must contain("Fecha en formato incorrecto")
    	}

    	"no crear una tarea si la fecha es incorrecta pero el usuario es correcto" in new WithApplication{
    		val Some(home) = route(FakeRequest(POST, "/domingo/tasks/20df14-sfd10-23").withFormUrlEncodedBody(("label", "Comprar oregano")))
    		status(home) must equalTo(400)
    		contentAsString(home) must contain("Fecha en formato incorrecto")

    	}

    	"no crear una tarea si el usuario es incorrecto pero la fecha correcta" in new WithApplication{
    		val Some(home) = route(FakeRequest(POST, "/eduardonoesta/tasks/2014-10-23").withFormUrlEncodedBody(("label", "Comprar oregano")))
    		status(home) must equalTo(400)
    		contentAsString(home) must contain("Usuario incorrecto")
    	}
	}
}
