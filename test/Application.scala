import org.specs2.mutable._
import org.specs2.runner._
import org.junit.runner._
import play.api.test._
import play.api.test.Helpers._
import models.Task
import models.Category
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

    val c1 = new Category("deportes", "alberto")
    val c2 = new Category("ocio", "alberto")
    val c3 = new Category("mads", "alberto")
    val c4 = new Category("iphone", "domingo")
    val c5 = new Category("apple", "domingo")
    val c6 = new Category("examenes", "domingo")

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
			}
    	}

    	"mostrar una tarea por id" in new WithApplication{
    		running(FakeApplication(additionalConfiguration = inMemoryDatabase())){
				Task.create(t2)
    			val home = route(FakeRequest(GET, "/tasks/" + 1)).get
    			status(home) must equalTo(200)
				contentAsString(home) must contain ("\"label\":\"prueba1\"")
			}
    	}

    	"mostrar error 404 cuando consultas una tarea inexistente" in new WithApplication{
    		running(FakeApplication(additionalConfiguration = inMemoryDatabase())){
    			Task.create(t1)
    			val home = route(FakeRequest(GET, "/tasks/" + 2)).get
    			status(home) must equalTo(404)
    			contentAsString(home) must contain ("Tarea no encontrada")
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

        "Crear una categoria por un usuario" in new WithApplication{
            val Some(home) = route(FakeRequest(POST, "/domingo/category/deportes"))
            status(home) must equalTo(201)
            contentAsString(home) must contain("deportes")
            contentAsString(home) must contain("domingo")
        }

        "Borrar una categoria" in new WithApplication{
            Category.create(c1)
            val list = Category.all()
            list.length must equalTo(1)
            val Some(home) = route(FakeRequest(DELETE, "/alberto/category/deportes"))
            status(home) must equalTo(200)
            contentAsString(home) must contain("Categoria borrada con exito")
            val list2 = Category.all()
            list2.length must equalTo(0)

            val Some(home2) = route(FakeRequest(DELETE, "/alberto/category/deportes"))
            status(home2) must equalTo(404)
            contentAsString(home2) must contain("La categoria no existe")
        }

        "No borrar la categoria si tiene tareas asociadas" in new WithApplication{
            Category.create(c1)
            Task.create(t1)
            Category.addTask(Task.buscar(1).get, c1)
            val Some(home) = route(FakeRequest(DELETE, "/alberto/category/deportes"))
            status(home) must equalTo(400)
            contentAsString(home) must contain("La categoria tiene tareas asociadas")
        }

        "No borrar la categoria si no pertenece al usuario creador" in new WithApplication{
            Category.create(c1)
            val Some(home) = route(FakeRequest(DELETE, "/domingo/category/deportes"))
            status(home) must equalTo(400)
            contentAsString(home) must contain("La categoria no pertenece al usuario")
        }

        "listar todas las categorias" in new WithApplication{
            Category.create(c1)
            Category.create(c2)
            Category.create(c3)
            Category.create(c5)

            val home = route(FakeRequest(GET, "/category")).get
            status(home) must equalTo(200)
            contentAsString(home) must contain ("\"usuario\":\"alberto\"")
            contentAsString(home) must contain ("\"usuario\":\"domingo\"")
            contentAsString(home) must contain ("\"nombre_cat\":\"mads\"")
            contentAsString(home) must contain ("\"nombre_cat\":\"ocio\"")
            contentAsString(home) must contain ("\"nombre_cat\":\"deportes\"")
        }

        "listar categorias por usuario" in new WithApplication{
            Category.create(c1)
            Category.create(c2)
            Category.create(c3)
            Category.create(c5)

            val home = route(FakeRequest(GET, "/domingo/category")).get
            val home2 = route(FakeRequest(GET, "/alberto/category")).get
            status(home) must equalTo(200)
            status(home2) must equalTo(200)
            contentAsString(home) must contain ("\"nombre_cat\":\"apple\"")
            contentAsString(home) must contain ("\"usuario\":\"domingo\"")
            contentAsString(home2) must contain ("\"usuario\":\"alberto\"")
            contentAsString(home2) must contain ("\"nombre_cat\":\"ocio\"")
            contentAsString(home2) must contain ("\"nombre_cat\":\"deportes\"")
            contentAsString(home2) must contain ("\"nombre_cat\":\"mads\"")
        }

        "Añadir una categoria a una tarea" in new WithApplication{
            Category.create(c1)
            Task.create(t1)

            val home = route(FakeRequest(POST, "/addCat/1/deportes")).get
            status(home) must equalTo(200)
            contentAsString(home) must contain ("Categoria deportes añadida a la tarea")
        }

        "No añadir la categoria a la tarea si la tarea no existe" in new WithApplication{
            Category.create(c1)
            Task.create(t1)

            val home = route(FakeRequest(POST, "/addCat/2/deportes")).get
            status(home) must equalTo(400)
            contentAsString(home) must contain("La tarea no existe")
        }

        "No añadir la categoria a la tarea si la categoria no existe" in new WithApplication{
            Category.create(c1)
            Task.create(t1)

            val home = route(FakeRequest(POST, "/addCat/1/noexiste")).get
            status(home) must equalTo(400)
            contentAsString(home) must contain("La categoria no existe")
        }

        "No añadir la categoria a la tarea si ambas no pertenecen al mismo usuario" in new WithApplication{
            Category.create(c1)
            Task.create(t2)

            val home = route(FakeRequest(POST, "/addCat/1/deportes")).get
            status(home) must equalTo(400)
            contentAsString(home) must contain("La tarea y la categoria deben ser del mismo usuario")
        }

        "No añadir la categoria a la tarea si la categoria ya contiente la tarea" in new WithApplication{
            Category.create(c1)
            Task.create(t1)
            Category.addTask(Task.buscar(1).get, c1)

            val home = route(FakeRequest(POST, "/addCat/1/deportes")).get
            status(home) must equalTo(400)
            contentAsString(home) must contain ("La categoria ya contiene la tarea")
        }

        "Borrar una tarea de una categoria" in new WithApplication{
            Category.create(c1)
            Task.create(t1)
            Category.addTask(Task.buscar(1).get, c1)
            val list1 = Task.listCategory(c1)
            list1.length must equalTo(1)

            val home = route(FakeRequest(POST, "/removeCat/1/deportes")).get
            status(home) must equalTo(200)
            contentAsString(home) must contain("Categoria deportes borrada de la tarea")
            val list2 = Task.listCategory(c1)
            list2.length must equalTo(0)

            val home2 = route(FakeRequest(POST, "/removeCat/1/deportes")).get
            status(home2) must equalTo(404)
            contentAsString(home2) must contain("La categoria no contiene a la tarea")
        }

        "No borrar la categoria de la tarea si la tarea no existe" in new WithApplication{
            Category.create(c1)
            Task.create(t1)

            val home = route(FakeRequest(POST, "/removeCat/2/deportes")).get
            status(home) must equalTo(400)
            contentAsString(home) must contain("La tarea no existe")
        }

        "No borrar la categoria de la tarea si la categoria no existe" in new WithApplication{
            Category.create(c1)
            Task.create(t1)

            val home = route(FakeRequest(POST, "/addCat/1/noexiste")).get
            status(home) must equalTo(400)
            contentAsString(home) must contain("La categoria no existe")
        }

        "No borrar la categoria de la tarea si ambas no pertenecen al mismo usuario" in new WithApplication{
            Category.create(c1)
            Task.create(t2)

            val home = route(FakeRequest(POST, "/addCat/1/deportes")).get
            status(home) must equalTo(400)
            contentAsString(home) must contain("La tarea y la categoria deben ser del mismo usuario")
        }

        "Listar tareas por categoria" in new WithApplication{
            Category.create(c1)
            Category.create(c5)
            Task.create(t1)
            Task.create(t2)
            Task.create(t4)
            Task.create(t5)
            Category.addTask(Task.buscar(1).get, c1)
            Category.addTask(Task.buscar(2).get, c5)
            Category.addTask(Task.buscar(3).get, c1)
            Category.addTask(Task.buscar(4).get, c1)

            val home = route(FakeRequest(GET, "/category/apple")).get
            val home2 = route(FakeRequest(GET, "/category/deportes")).get
            status(home) must equalTo(200)
            status(home2) must equalTo(200)
            contentAsString(home) must contain ("\"label\":\"prueba1\"")
            contentAsString(home) must contain ("\"nombre\":\"domingo\"")
            contentAsString(home2) must contain ("\"nombre\":\"alberto\"")
            contentAsString(home2) must contain ("\"label\":\"prueba\"")
            contentAsString(home2) must contain ("\"label\":\"prueba3\"")
            contentAsString(home2) must contain ("\"label\":\"prueba4\"")
        }

        "No listar las tareas por categoria si la categoria no existe" in new WithApplication{
            Category.create(c1)
            Task.create(t1)
            Category.addTask(Task.buscar(1).get, c1)

            val home = route(FakeRequest(GET, "/category/noexiste")).get
            status(home) must equalTo(400)
            contentAsString(home) must contain ("La categoria no existe")
        }
	}
}
