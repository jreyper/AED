-- Listar las tablas del catálogo.
SELECT name AS tabla
FROM sqlite_schema
WHERE type = 'table' AND name IN ('tareas_jdbc', 'tareas_orm')
ORDER BY name;

-- Ver las columnas y los tipos de cada tabla.
PRAGMA table_info('tareas_jdbc');
PRAGMA table_info('tareas_orm');

-- Listar las tareas por identificador.
SELECT id, titulo FROM tareas_jdbc ORDER BY id;
SELECT id, titulo FROM tareas_orm ORDER BY id;

-- Contar cuántas tareas hay en cada tabla.
SELECT 'tareas_jdbc' AS tabla, COUNT(*) AS total FROM tareas_jdbc
UNION ALL
SELECT 'tareas_orm', COUNT(*) FROM tareas_orm;

-- Buscar una tarea por ID. Cambia 1 por el identificador que quieras consultar.
SELECT id, titulo FROM tareas_jdbc WHERE id = 1;
SELECT id, titulo FROM tareas_orm WHERE id = 1;
