# PruebaCorte2 — Registro de productos

Práctica de inventario con búsqueda, filtros y operaciones CRUD.

## Herramientas

- Java: el proyecto compila para Java 21. Las pruebas se ejecutaron con JDK 25.0.2.
- JavaFX 21.0.6 para la interfaz.
- Maven Wrapper 3.8.5 para compilar y ejecutar, sin instalar Maven aparte.
- PostgreSQL y controlador JDBC 42.7.13.

## Preparación y ejecución

1. Instalar un JDK compatible y PostgreSQL. Comprobar `java -version` y configurar `JAVA_HOME` si es necesario.
2. En PostgreSQL, ejecutar primero `CREATE DATABASE papeleria_campus;`, que aparece al inicio de `sql/estructura.sql`.
3. Conectarse a **papeleria_campus** y ejecutar el resto de `sql/estructura.sql` para crear el esquema `inventario` y la tabla. Después ejecutar `sql/datos.sql` una sola vez para insertar los 12 productos iniciales.
4. Copiar `database.example.properties` como `database.properties` en la raíz del proyecto. Configurar ahí la URL, el usuario y la contraseña de PostgreSQL. `database.properties` está ignorado por Git y no debe subirse al repositorio.
5. Desde la raíz del proyecto, en PowerShell:

   ```powershell
   .\mvnw.cmd clean javafx:run
   ```

   La primera ejecución necesita conexión para descargar Maven y las dependencias.

## Organización

Interfaz FXML → `ProductoController` → `ProductoDAO` → PostgreSQL.
Los resultados se convierten en objetos `Producto` y se muestran en el `TableView`.
El DAO obtiene las conexiones mediante `ConexionBD`, que lee la configuración local desde `database.properties`.

## Pruebas y apoyo

Los resultados están en [pruebas.md](pruebas.md) y las tres capturas en `evidencias/`.

Se utilizó Codex como apoyo para agilizar la ejecución de pruebas repetitivas y organizar sus resultados debido al tiempo disponible. Las pruebas se realizaron sobre la aplicación y la base de datos del proyecto, tomando como referencia los resultados establecidos en la guía.
