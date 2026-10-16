# Pruebas de la práctica

Fecha: 15 de octubre de 2026. JDK 25.0.2, destino Java 21 y JavaFX 21.0.6.
Compilación: `mvnw.cmd -o compile`, resultado `BUILD SUCCESS`.

Se usaron clases Java temporales para cargar el FXML real, accionar sus botones en el hilo JavaFX, leer tabla, contador y alertas, y consultar PostgreSQL mediante DAO/JDBC. Las capturas corresponden a la ventana real. No se modificó la implementación.

En la matriz, **FX** significa comprobación de controles y alertas JavaFX; **BD**, consulta independiente a `papeleria_campus`, esquema `inventario`. No se generaron capturas de cada prueba.

| Prueba | Entrada / acción | Resultado esperado | Resultado obtenido | Estado | Evidencia |
|---|---|---|---|---|---|
| Arranque offline | mvnw.cmd -o javafx:run | Abrir aplicación | BUILD FAILURE: faltaban dependencias del plugin en caché; fallo del entorno, previo al arranque | Fallida | Salida Maven: PluginResolutionException |
| Arranque, repetición | mvnw.cmd javafx:run con acceso de red | Abrir aplicación | Descargó dependencias declaradas; ventana abierta con contador 12; cierre normal y BUILD SUCCESS | Pasada | Maven y lectura de accesibilidad de ventana |
| Inicial | Abrir y consultar productos | 12 productos; contador 12 | 12 en TableView y BD; contador 12 | Pasada | FX/BD; captura 01 |
| B1 | CUADERNO; sin otros filtros | P001, P002; 2 | P001, P002; contador 2 | Pasada | FX |
| B2 | T00; sin otros filtros | T001–T004; 4 | T001, T002, T003, T004; contador 4 | Pasada | FX |
| B3 | Texto vacío; Papelería | P001–P004; 4 | P001, P002, P003, P004; contador 4 | Pasada | FX |
| B4 | Texto vacío; Todas; existencias | 9; excluir P004, T004 y A003 | 9; los tres excluidos; contador 9 | Pasada | FX |
| B5 | USB; Tecnología; existencias | T001, T003; 2 | T001, T003; contador 2 | Pasada | FX; captura 02 |
| B6 | ZZZ999; sin otros filtros | 0 y aviso | 0; contador 0; «Sin coincidencias» | Pasada | FX |
| B7 | Repetir B5 y Limpiar filtros | Vacío, Todas, desmarcado; 12 | Filtros restablecidos; 12 filas y contador 12 | Pasada | FX |
| CRUD crear | X001; Producto de prueba; Accesorios; 10.00; 1 | 13; persistencia | 13; X001 con 10.00 y stock 1 al volver a consultar | Pasada | FX/BD |
| CRUD actualizar | X001 a precio 15.50 y stock 3 | 13; solo X001; por id_producto | Mismo id; 13; originales sin cambios; valores persistentes | Pasada | FX/BD; WHERE id_producto en DAO |
| CRUD cancelar | Eliminar X001 y cancelar | Permanece; 13 | X001 permanece en BD; 13 filas | Pasada | FX/BD |
| CRUD eliminar | Volver a eliminar y confirmar | 12; X001 ausente | 12; X001 ausente en consulta nueva | Pasada | FX/BD |
| V1 | Código vacío, espacios y 11 caracteres; límites 1 y 10 con espacios alrededor | Rechazar inválidos; aceptar límites tras trim | Rechazados al crear/actualizar; límites aceptados y normalizados | Pasada | FX/BD |
| V2 | Nombre espacios, AB y 81 caracteres; límites 3 y 80 con espacios alrededor | Rechazar inválidos; aceptar límites tras trim | Rechazados al crear/actualizar; límites aceptados y recortados | Pasada | FX/BD |
| V3 | Categoría sin seleccionar; revisar ComboBox | Rechazar ausencia e impedir categoría ajena | Crear/actualizar rechazados; ComboBox no editable, catálogo de 3 categorías | Pasada | FX |
| V4 | Precio abc | Rechazar sin cerrar | «Precio inválido» al crear/actualizar; aplicación continúa | Pasada | FX/BD |
| V5 | Precio 0 y -1 | Rechazar ambos | Ambos rechazados al crear/actualizar; BD sin cambios | Pasada | FX/BD |
| V6 | Precio 12.345 y 100000000.00 | Rechazar decimales/rango; sin redondeo | Ambos rechazados al crear/actualizar; BD sin cambios | Pasada | FX/BD |
| V7 | Stock abc y 2.5 | Rechazar ambos | Ambos rechazados al crear/actualizar | Pasada | FX/BD |
| V8 | Stock -1, 2147483648 y 0 | Rechazar primeros; aceptar 0 | Rechazos al crear/actualizar; 0 aceptado y persistido en ambos | Pasada | FX/BD |
| V9 crear | Código « p001 » | Normalizar y rechazar duplicado | Campo normalizado a P001; «Código duplicado»; BD intacta | Pasada | FX/BD; captura 03 |
| V9 otro | Seleccionar P002 y cambiar código a P001 | Rechazar | «Código duplicado»; BD intacta | Pasada | FX/BD |
| V9 propio | Actualizar P001 conservando P001 y demás valores | Permitir | «Registro actualizado»; mismos datos e id | Pasada | FX/BD |
| V10 | Nuevo/Limpiar; Actualizar y Eliminar sin selección | Bloquear con mensaje | Ambas muestran «Producto no seleccionado» | Pasada | FX/BD |
| E1 | Inicio y Buscar usando copia temporal de conexión con puerto 1 | Error comprensible; no cerrar ni fingir 0 resultados válidos | «Error de base de datos / No se pudieron cargar los productos»; ventana abierta; sin «Sin coincidencias» | Pasada | FX; consola: buscar productos, SQLState 08001 |
| E2 | DAO: guardar directamente otro P001 | SQLException real; operación y SQLState; integridad | PostgreSQL rechaza; DAO registra y propaga SQLException 23505; originales intactos | Pasada | DAO/BD; consola: guardar producto, SQLState 23505 |
| E3 UPDATE | Seleccionar temporal, borrarlo por JDBC y actualizar sin refrescar | 0 filas; sin éxito; permitir recarga | «Producto no encontrado» y solicitud de recargar; recarga correcta; DAO también devuelve 0 con id inexistente | Pasada | FX/DAO/BD |
| E3 DELETE | Seleccionar temporal, borrarlo por JDBC y confirmar eliminación sin refrescar | 0 filas; sin éxito; permitir recarga | Mismo aviso y recarga correcta; DAO también devuelve 0 con id inexistente | Pasada | FX/DAO/BD |
| E4 | Revisar errores E1–E3 | Operación/SQLState en excepciones; alertas sin secretos/trazas; sin éxito falso | Consola registra 08001 y 23505 con operación; mensajes genéricos; ningún éxito falso. E3 no genera SQLException | Pasada | Consola y alertas FX |
| Restricciones SQL | Duplicado; precio 0/-1; stock negativo; categoría Otra; nombre AB; código sin normalizar | Rechazar sin dejar registros | Duplicado 23505; seis CHECK con 23514; transacción revertida | Pasada | JDBC/PostgreSQL |
| Estado final | COUNT y comparación con datos.sql y estado inicial | Exactamente los 12 originales | COUNT = 12; mismos valores que datos.sql; mismos ids y valores del inicio; sin temporales | Pasada | Consultas independientes BD |

## Evidencias y límites

- [01: listado inicial](evidencias/01-listado-inicial.png): contador 12. La tabla original muestra ocho filas simultáneamente y tiene desplazamiento; **queda pendiente una captura que muestre los 12 renglones a la vez**. Para completarla manualmente, capturar el listado sin filtros y contador 12 en una vista que permita ver las 12 filas; no se alteró el FXML para obtenerla.
- [02: filtro B5](evidencias/02-filtro-combinado-b5.png): USB, Tecnología, existencias, T001/T003 y contador 2.
- [03: validación V9](evidencias/03-validacion-v9.png): P001 ya normalizado y mensaje de duplicado.

No hubo pruebas funcionales fallidas ni casos funcionales pendientes. El intento de arranque offline falló por dependencias ausentes; la repetición normal pasó. La primera consulta fue bloqueada por el aislamiento de red del entorno; se repitió con acceso local autorizado y pasó. Esto no se registró como fallo de la aplicación. El JDK 25 emitió advertencias de compatibilidad de dependencias, sin impedir estas pruebas. No se verificó con un JDK 21 instalado ni se hizo un recorrido manual completo con teclado/ratón.

## Limpieza

Los productos temporales se eliminaron. Las pruebas SQL directas usaron transacción y rollback. E1 utilizó una copia compilada temporal: el archivo local original de conexión nunca se editó y su SHA-256 permaneció igual. Se eliminaron las clases, fuentes, registros y señales temporales de verificación. `sql/estructura.sql` y `sql/datos.sql` no se modificaron. Los INSERT de prueba pueden avanzar la secuencia SERIAL; no se reinició, y los ids originales se conservaron. Sin commit ni push.
