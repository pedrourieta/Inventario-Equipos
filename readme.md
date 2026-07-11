# Tarea Integradora - 3°F DSM
# Inventario de Equipos de Cómputo

Sistema de gestión de inventario desarrollado en Java con interfaz gráfica (Swing) y conexión a base de datos MySQL mediante JDBC.

## Requisitos previos
* Java Development Kit (JDK) 26+.
* Entorno de desarrollo compatible con Maven (en este caso IntelliJ).
* Servidor MySQL Workbench 8.0 CE en ejecución (puerto 3306).

## Instalación y ejecución
1. Clonar este repositorio (.zip).
2. Ejecutar el script `setup_bd.sql` en el gestor de MySQL local para crear la base de datos y la tabla correspondiente.
3. Abrir el archivo `src/main/java/org/example/ConexionBD.java` y actualizar las variables `USUARIO` y `PASSWORD` con las credenciales del servidor MySQL local.
4. Verificar la conexión con MySQL "localhost" o "127.X.X.X"
5. Sincronizar el archivo `pom.xml` para descargar la dependencia de MySQL Connector/J.
6. Ejecutar la clase `Main.java` para iniciar la aplicación de inventario.