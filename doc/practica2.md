PRACTICA 2 -> ALBERTO MARTINEZ LOPEZ

La primera parte de la practica constaba de implementar todos los test tanto del modelo como de la aplicación.
Tanto los test de la primera parte como de la segunda que consistía en ampliar el api para añadir categorías a las tareas (mas tarde especificaremos ese tema) se han recogido en cuatro archivos.

Application.scala -> Contiene todos los test de la aplicación, es decir, restricciones y soporte de errores, es la capa que interactua con el usuario por lo tanto testea que no introduzca errores a la hora de interactuar con la base de datos, ya sea por restricciones de la base de datos como (claves primarias, claves ajenas, etc) o peticiones erróneas como consultar tareas inexistentes etc…

ModelTask.scala -> Contiene los test del modelo de Tarea, añadir, borrar y listar las tareas de varios modos incluidas las funcionalidades de la fecha vistas en la practica anterior.

ModelTaskUser.scala -> Contiene los test del modelo de usuario, al crearlos en la base de datos y ser fijos solo testea que se listen correctamente.

ModelCategories.scala -> Creado para la segunda parte de la practica, contiene los test relaciones con el crud de categorias, ademas de poder añadirlas y eliminarlas de las tareas.

Para la nueva característica, la creación de categorías se ha necesitado una nueva evolución en la base de datos.
Se ha creado la tabla categorías con los atributos id, nombre_cat y usuario.
Al poder tener una categoría a muchas tareas y una tarea a muchas categorías se ha necesitado una tabla auxiliar llamada cat_task con los atributos category y task, que son claves ajenas de categorías(nombre) y task(id) respectivamente.

A continuación vamos a desarrollar la descripción de las nuevas funcionalidades del api.

POST	/:login/category/:cat -> Crea una categoría con el nombre :cat asignada al usuario :login, no permite la inserción de dos categorías con el mismo nombre.

GET	/category -> Lista un Json con todas las tareas creadas por los usuarios.

GET	/:login/category -> Lista las categorías del usuario :login.

DELETE 	/:login/category/:cat -> Borra una categoría que había creado el usuario :login con el nombre :cat, para borrar la categoría no puede tener tareas asociadas.

POST	/addCat/:id/:cat -> Asocia una tarea con id :id a la categoría :cat, la tarea y la categoría deben estar creadas por el mismo usuario.

POST 	/removeCat/:id/:cat -> Desvincula una tarea con id id: de la categoría :cat.

GET	/category/:cat -> Lista las tareas que contiene la categoría :cat.

GET 	/tasks/category/:id -> Lista las categorías a la que esta asociada la tarea con id :id.

MODIFICACIONES DE LA PRACTICA ANTERIOR

DELETE 	/tasks/:id -> Al poder asociar las tareas a categorías había que optar por una política de predominio de las tareas o de las categorías. Al ser un gestor de tareas se ha optado por predominio de las tareas, es decir, se ha modificado el delete de tareas ya que al optar por borrar una tarea con categorías asociadas se desvincula automáticamente de las categorías y procede a borrar la tarea, justo al contrario que con las categorías que si contiene tareas asociadas no procede a borrar la categoría hasta que el usuario no la deje vacía.