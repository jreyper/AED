# Base de datos SQLite de tareas

| Archivo | Contenido |
|---|---|
| `data/tareas.sqlite3` | Base de datos lista para abrir |
| `schema.sql` | Sentencias para crear `tareas_jdbc` y `tareas_orm` |
| `consultas.sql` | Consultas de lectura para inspeccionar las tablas y sus datos |

Las dos tablas empiezan vacías. Cada una tiene un `id` entero autoincremental como clave primaria y un `titulo` de texto obligatorio.

## Abrir la base de datos

Necesitas el cliente de terminal `sqlite3`. Comprueba la instalación con `sqlite3 --version`. Descomprime el ZIP, entra en la carpeta `sqlite-tareas` y abre la base en modo de solo lectura.

```bash
sqlite3 -readonly data/tareas.sqlite3
```

Dentro de SQLite, ejecuta estos comandos:

```text
.tables
.schema tareas_jdbc
.schema tareas_orm
.headers on
.mode column
.read consultas.sql
.quit
```

También puedes ejecutar todas las consultas desde la terminal.

```bash
sqlite3 -readonly -header -column data/tareas.sqlite3 < consultas.sql
```

El listado de tablas mostrará `tareas_jdbc` y `tareas_orm`. El recuento será `0` para ambas hasta que se guarden tareas.

## Crear otra base con el mismo esquema

```bash
sqlite3 tareas-nueva.sqlite3 < schema.sql
```

## Usar la base con Spring Boot

Copia `data/tareas.sqlite3` a la carpeta `data` del proyecto que implementa JDBC y ORM. La URL de conexión es `jdbc:sqlite:data/tareas.sqlite3`.
