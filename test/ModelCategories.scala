import org.specs2.mutable._
import org.specs2.runner._
import org.junit.runner._

import play.api.test._
import play.api.test.Helpers._
import models.Category

@RunWith(classOf[JUnitRunner])
class ModelCategories extends Specification {

	"El modelo Category" should{

		"Crear una categoria" in {
			running(FakeApplication(additionalConfiguration = inMemoryDatabase())){
				val cat = new Category("deportes", "alberto")
				Category.create(cat)
				val c = Category.all()

				c.length must equalTo(1)
				c(0).nombre_cat must equalTo("deportes")
				c(0).usuario must equalTo("alberto")
			}
		}

		"Listar todas las categorias" in {
			running(FakeApplication(additionalConfiguration = inMemoryDatabase())){

				val cat = new Category("deportes", "alberto")
				Category.create(cat)
				val cat1 = new Category("estudio", "alberto")
				Category.create(cat1)
				val cat2 = new Category("casa", "alberto")
				Category.create(cat2)

				val c = Category.all()
				c.length must equalTo(3)
				c(1).nombre_cat must equalTo("estudio")
			}
		}

		"Borrar una categoria dado su id" in {
			running(FakeApplication(additionalConfiguration = inMemoryDatabase())){

				val c1 = Category.all()
				c1.length must equalTo(0)
				val cat = new Category("deportes", "alberto")
				Category.create(cat)
				val c2 = Category.all()
				c2.length must equalTo(1)
				Category.delete(1)
				val c3= Category.all()
				c3.length must equalTo(0)
			}
		}

		"Listar las categorias por usuario" in {
			running(FakeApplication(additionalConfiguration = inMemoryDatabase())){

				val cat = new Category("deportes", "alberto")
				Category.create(cat)
				val cat1 = new Category("estudio", "domingo")
				Category.create(cat1)
				val cat2 = new Category("casa", "alberto")
				Category.create(cat2)

				val c1 = Category.listByUser("alberto")
				c1.length must equalTo(2)
				c1(0).nombre_cat must equalTo("deportes")
				c1(1).nombre_cat must equalTo("casa")
				c1(1).usuario must equalTo("alberto")

				val c2 = Category.listByUser("domingo")
				c2.length must equalTo(1)
				c2(0).nombre_cat must equalTo("estudio")
			}
		}
	}
}
