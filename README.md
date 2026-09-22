# Proyecto Maven — Gestor de Tareas

# 1. Crear el primer proyecto Maven

### Objetivo

Crear un proyecto Maven completo utilizando **Java 21** y configurar correctamente su estructura inicial.

## Estructura del proyecto

La estructura creada es:

![cap1](caps/cap1.png)

## POM
Se ha configurado Java 21 con:

```xml
<maven.compiler.source>21</maven.compiler.source>
<maven.compiler.target>21</maven.compiler.target>
```

También se han configurado los plugins necesarios para compilación, las pruebas y el empaquetado.

![cap2](caps/cap2.png)

## Clase Main

La clase muestra el mensaje:

```text
Gestor de tareas preparado
```

![cap3](caps/cap3.png)

## Comprobación

Se ejecutó:

```bash
mvn validate
```

Resultado:

```text
BUILD SUCCESS
```

![cap4](caps/cap4.png)

---


