PRACTICA 1

La aplicación consta de una base de datos en dos ficheros de evolución de sql.
Consta de dos tablas una task con los campos id, label, fecha y nombre la cual es clave ajena de la tabla task_user que tan solo contiene el campo nombre.

En el api podemos encontrar cuatro peticiones POST capaces de crear tareas:

POST 	/tasks => Crea una tarea con el nombre de “alberto” por defecto y la fecha en null, llama al método create del modelo Task.

POST	/:login/tasks => Crea una tarea con el nombre del usuario pasado en la uri y la fecha null, comprueba que el usuario este en la tabla task_user enviando una respuesta con estado 400 “Usuario incorrecto” si no existe tal usuario, en caso afirmativo llama al método create del modelo Task para hacer la inserción en la base de datos.

POST	/tasks/:fecha => Crea una tarea con el nombre de “alberto y la fecha especificada en la uri, comprueba que la fecha sea correcta enviando una respuesta con estado 400 “Fecha en formato incorrecto”, en caso de estar correcto hace la inserción de la misma manera que las anteriores.

POST 	/:login/tasks/:fecha => Crea una tarea con el nombre y la fecha especificados en la uri, comprueba que los datos sean correctos enviando las respuestas antes descritas si no lo son, en caso contrario hace la inserción.

Se ha decidido modificar el método create para recibir un objeto Task y así unificar todas las inserciones a la base de datos por el mismo método.

En el api podemos encontrar los siguientes GET:

GET     / => Petición a index, redirige a la uri /tasks.

GET 	/tasks => Muestra todas las tareas del usuario por defecto “alberto”.

GET	/tasks/:id => Muestra unicamente la tarea con la id especificada en la uri.

GET 	/:login/tasks => Muestra las tareas del usuario especificado en la uri.

GET	/tasks/orderASC => Ordena todas las tareas que tengan fecha no nula en orden ascendente en el tiempo.

GET	/tasks/list-year/:anyo => Lista todas las tareas del año especificado en la uri.

En el api se pueden borrar tareas con la petición:

DELETE 	/tasks/:id => Borra la tarea con el id especificado en la uri.



