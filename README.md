# Registro de productos de la papelería Campus

Aplicación de escritorio desarrollada para la práctica de JavaFX y PostgreSQL. Permite consultar productos, buscar por código o nombre, filtrar por categoría y existencias, y crear, actualizar o eliminar registros con validaciones.

Autor: Erving Josué Miranda Ríos.

## Versiones y dependencias

- Java 21. La segunda ronda de pruebas se realizó con Microsoft OpenJDK 21.0.12.1.
- IntelliJ IDEA 2026.2.1.
- JavaFX 21.0.6.
- PostgreSQL 18.3 en la verificación local.
- Controlador JDBC de PostgreSQL 42.7.13.
- Maven Wrapper 3.8.5, incluido en el proyecto.
- JUnit 5.12.1, declarado como dependencia de pruebas en `pom.xml`.

Se requiere un JDK 21 y PostgreSQL en ejecución. Maven Wrapper descarga las dependencias declaradas; la primera ejecución requiere conexión a Internet.

## Preparación y ejecución

1. Abrir `sql/estructura.sql` en pgAdmin o en el cliente de PostgreSQL y ejecutar primero solamente la instrucción `CREATE DATABASE papeleria_campus;` que aparece al inicio del script.
2. Conectarse a `papeleria_campus` y ejecutar el resto del mismo `sql/estructura.sql`. Se crearán el esquema `inventario`, la tabla `producto` y sus restricciones. El identificador se genera mediante `SERIAL`.
3. Ejecutar `sql/datos.sql` una sola vez para cargar los 12 productos iniciales.
4. Copiar `database.example.properties` como `database.properties` en la raíz del proyecto. Configurar `db.url`, `db.user` y `db.password` con los datos de la instalación local. `database.properties` está ignorado por Git y no debe incluirse en la entrega ni subirse al repositorio.
5. Abrir el proyecto en IntelliJ IDEA 2026.2.1, cargar el proyecto Maven y configurar el SDK del proyecto con JDK 21. Ejecutar la clase `Launcher` con la acción **Run** de IntelliJ, usando la raíz del proyecto como directorio de trabajo.

Como alternativa, Maven Wrapper permite ejecutar `mvnw.cmd clean javafx:run` desde la raíz del proyecto con `JAVA_HOME` configurado para el JDK 21.

## Organización y flujo de datos

- `ProductoApplication` y `Launcher`: inicio de la aplicación.
- `productos-view.fxml`: formulario, filtros y tabla.
- `ProductoController`: acciones de la interfaz, validaciones y actualización del listado.
- `ProductoDAO`: consultas y operaciones CRUD mediante JDBC.
- `ConexionBD`: lectura de la configuración externa y apertura de conexiones.
- `Producto`: datos de cada registro; el precio utiliza `BigDecimal`.
- `sql/`: estructura y datos iniciales.
- `pruebas.md` y `evidencias/`: resultados y capturas de la práctica.

El flujo principal es:

FXML/interfaz → ProductoController → ProductoDAO → PostgreSQL → ResultSet → Producto → ObservableList → TableView.

La interfaz recibe la acción y el controlador valida los campos. El DAO ejecuta las sentencias con parámetros y obtiene los resultados de PostgreSQL. Cada fila del `ResultSet` se convierte en un `Producto`; el controlador coloca los productos en una `ObservableList`, que alimenta el `TableView` y su contador. Después de guardar, actualizar o eliminar un producto, se vuelve a consultar la base con los filtros vigentes.

## Restauración de datos

En la base local de pruebas `papeleria_campus`, ejecutar:

```sql
TRUNCATE TABLE inventario.producto RESTART IDENTITY;
```

Después volver a ejecutar `sql/datos.sql`. Este procedimiento elimina los registros existentes y restaura los 12 productos originales con sus identificadores desde 1. La siguiente inserción recibirá el ID 13.

## Pruebas y evidencias

La segunda ronda se realizó el 16 de octubre de 2026 sobre la aplicación JavaFX y PostgreSQL. La matriz de [pruebas.md](pruebas.md) registra el listado, B1–B7, el ciclo CRUD, V1–V10, E1–E4, las restricciones y el estado final. La base quedó restaurada a los 12 productos originales y la última apertura mostró contador 12.

- [Listado inicial](evidencias/01-listado-inicial.png): filtros limpios y contador 12.
- [Filtro combinado B5](evidencias/02-filtro-combinado-b5.png): USB, Tecnología y Solo con existencias; T001 y T003; contador 2.
- [Validación V9](evidencias/03-validacion-v9.png): código P001 normalizado y aviso de duplicado.

El ciclo CRUD se documenta en la matriz con las consultas de comprobación y los reinicios de la aplicación.

## Fuentes y herramientas de apoyo

Se utilizó como referencia principal la guía oficial «Laboratorio TableView - CRUD», junto con las aclaraciones sobre el uso de `SERIAL`, la prueba E2 mediante CHECK y las evidencias solicitadas.

Codex ayudó a automatizar y acelerar una primera ronda de pruebas y a organizar sus resultados. Después se revisaron y corrigieron aspectos de la implementación. Con la versión final se realizó una segunda tanda de verificaciones sobre la aplicación y PostgreSQL, y se actualizó la documentación con esos resultados. También se ajustaron parte de la redacción y el formato con ayuda de Codex para presentar con mayor claridad el contenido preparado para la práctica. Por ello, algunas secciones pueden conservar rasgos de redacción asistida por IA.
