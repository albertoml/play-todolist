import org.specs2.mutable._
import org.specs2.runner._
import org.junit.runner._

import play.api.test._
import play.api.test.Helpers._
import models.Task_user

@RunWith(classOf[JUnitRunner])
class ModelTaskUser extends Specification {

	"El modelo Task_user" should{

		"Buscar usuarios por nombre" in {
			running(FakeApplication(additionalConfiguration = inMemoryDatabase())){

				val u = Task_user.buscarUser("alberto")
				val user : Task_user = u match {
					case Some(i) => i
					case None => null 
				}
				user.nombre must equalTo("alberto")
			}
		}
	}
}
