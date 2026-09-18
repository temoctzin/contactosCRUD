# AgendaTelefonica

API REST para la gestión de una agenda telefónica. Permite crear, listar, consultar, actualizar y eliminar contactos, cada uno con uno o más teléfonos.

## Stack tecnológico

- **Java 17**
- **Spring Boot 4.1.1**
- **Spring Data JPA / Hibernate**
- **MySQL 8+**
- **Gradle 9.7.1** (wrapper incluido, no requiere instalación)
- **Docker + Docker Compose** (despliegue en contenedor, opcional)
- Empaquetado: WAR

## Requisitos

- **JDK 17** (solo para correr local con Gradle; la imagen Docker trae su propio JRE)
- **MySQL 8+** en ejecución (o levantarlo con Docker Compose)
- **Docker Engine + Docker Compose** (solo para el despliegue en contenedor)
- Puertos libres: `8080` (aplicación) y `3306` (MySQL) al usar Docker Compose o correr local

## Configuración de base de datos

1. Crea la base de datos (por defecto `ejercicios`):

   ```sql
   CREATE DATABASE ejercicios;
   ```

2. Proporciona credenciales vía variables de entorno, o usa los valores por defecto:

   | Variable | Default | Descripción |
   |----------|---------|-------------|
   | `DB_HOST` | `localhost` | Host de MySQL |
   | `DB_PORT` | `3306` | Puerto de MySQL |
   | `DB_NAME` | `ejercicios` | Nombre de la base de datos |
   | `DB_USER` | `root` | Usuario de MySQL |
   | `DB_PASSWORD` | `devpass` | Contraseña del usuario |

   Las tablas se crean automáticamente (`ddl-auto=update`), no se necesitan scripts SQL.

## Cómo correr el proyecto

### Modo desarrollo con Gradle

Desde la carpeta del proyecto:

```bash
cd agendaTelefonica
./gradlew bootRun
```

`bootRun` compila y ejecuta la clase principal directamente (sin empaquetar) y levanta un **Tomcat 11 embebido** en el puerto 8080. Requiere **JDK 17** (toolchain declarada en `build.gradle.kts`) y un MySQL accesible según la configuración de arriba.

La aplicación se levanta en **http://localhost:8080**.

Alternativa desde IDE: ejecutar la clase `com.examen.AgendaTelefonicaApplication` (Spring Boot Dashboard / Run).

### Generar el artefacto (WAR)

```bash
./gradlew build
```

El archivo resultante queda en `agendaTelefonica/build/libs/`.

## Despliegue con Docker

El proyecto está dockerizado con un `Dockerfile` multi-etapa y un `docker-compose.yml`:

- **Etapa de build**: imagen `gradle:9.7.1-jdk17` genera el WAR.
- **Etapa de runtime**: imagen `eclipse-temurin:17-jre`; la app corre con `java -jar` usando el **servidor Tomcat embebido** dentro del contenedor (no requiere WildFly).

### Opción 1 — Todo junto (recomendado)

Levanta MySQL 8 y la aplicación en una red interna propia:

```bash
docker compose up -d
```

- Crea la red interna, el volumen `agendatelefonica_mysql-data` (los datos persisten) y espera el healthcheck de MySQL antes de iniciar la app.
- La aplicación queda en **http://localhost:8080/contactos** y se conecta al servicio `db` por su nombre en la red (el mapeo `3306:3306` solo se usa para clientes externos como Workbench).

Comandos útiles:

```bash
docker compose up -d --build   # reconstruye la imagen tras cambios en el código
docker compose logs -f app     # sigue los logs de la aplicación
docker compose down            # detiene los contenedores (conserva el volumen de datos)
docker compose down -v         # detiene y ELIMINA el volumen de datos
```

### Opción 2 — Conectar a un MySQL que ya tienes en Docker

Si ya tienes un contenedor MySQL corriendo, se conectan por una **red Docker compartida** (nunca por `localhost`):

```bash
docker network create agenda-net
docker network connect agenda-net <nombre-de-tu-mysql> --alias mysql

docker run -p 8080:8080 \
  --network agenda-net \
  -e DB_HOST=mysql \
  -e DB_PORT=3306 \
  -e DB_NAME=ejercicios \
  -e DB_USER=root \
  -e DB_PASSWORD=devpass \
  agendatelefonica-app
```

`DB_HOST` se resuelve por el **nombre/alias del contenedor en la red**, no por IP. Para apuntar a un MySQL del host (no Docker) usarías `host.docker.internal` (Linux: `--add-host=host.docker.internal:host-gateway`).

### Despliegue en servidor externo (WildFly)

El WAR generado también es desplegable en un **WildFly ≥ 40** (requiere **Servlet 6.1 / Jakarta EE 11**, base de Spring Boot 4). El servidor embebido no interfiere: Tomcat solo aparece en `bootRun` y en el WAR va en `WEB-INF/lib-provided`, que WildFly ignora y usa su propio Undertow. El contexto por defecto es el nombre del WAR (`/agendaTelefonica-0.0.1-SNAPSHOT`); para servir en la raíz (`/contactos`), despliega el archivo como `ROOT.war`.

### Acceso a Docker

Si `docker` falla con `permission denied while trying to connect to the docker API at unix:///var/run/docker.sock`, tu sesión no tiene el grupo `docker` cargado. Solución: cerrar sesión y volver a entrar (o ejecutar `newgrp docker` en la terminal). No se requiere `sudo`.

## Endpoints expuestos

Base URL: `http://localhost:8080`

| Método | Ruta | Descripción | Códigos de estado |
|--------|------|-------------|-------------------|
| `POST` | `/contactos` | Crear un contacto | `201`, `400`, `409` |
| `GET` | `/contactos` | Listar todos los contactos | `200` |
| `GET` | `/contactos/{id}` | Obtener un contacto por id | `200`, `404` |
| `PUT` | `/contactos/{id}` | Actualizar un contacto | `200`, `400`, `404`, `409` |
| `DELETE` | `/contactos/{id}` | Eliminar un contacto (borrado lógico) | `204`, `404` |

### Esquemas JSON

**ContactoRequest** (body de POST y PUT):

```json
{
  "nombre": "Juan",
  "apellidoPaterno": "Pérez",
  "apellidoMaterno": "García",
  "email": "juan.perez@example.com",
  "telefonos": [
    { "numero": "5512345678", "tipo": "CELULAR" },
    { "numero": "5522345678", "tipo": "TRABAJO" }
  ]
}
```

- `nombre`, `apellidoPaterno`, `apellidoMaterno`, `email`: obligatorios.
- `email`: debe ser un correo válido y no puede repetirse entre contactos.
- `telefonos`: obligatorio, mínimo un teléfono.
- `tipo`: enum `CASA`, `CELULAR` o `TRABAJO`.

**ContactoResponse** (respuesta):

```json
{
  "id": 1,
  "nombre": "Juan",
  "apellidoPaterno": "Pérez",
  "apellidoMaterno": "García",
  "email": "juan.perez@example.com",
  "fechaCreacion": "2026-09-17T18:45:00",
  "telefonos": [
    { "numero": "5512345678", "tipo": "CELULAR" }
  ]
}
```

### Consumo de endpoints (Postman)

Para cada petición: elige el **método**, ingresa la **URL**, agrega los **headers** y, donde se indique, un **body** en formato _raw → JSON_. El id (`{id}`) es un **path param** que debes sustituir por el valor real.

#### 1. Crear contacto

**POST** `http://localhost:8080/contactos`

**Headers**
| Key | Value |
|-----|-------|
| `Content-Type` | `application/json` |

**Body (raw - JSON)**
```json
{
  "nombre": "Juan",
  "apellidoPaterno": "Pérez",
  "apellidoMaterno": "García",
  "email": "juan.perez@example.com",
  "telefonos": [
    { "numero": "5512345678", "tipo": "CELULAR" },
    { "numero": "5522345678", "tipo": "TRABAJO" }
  ]
}
```

**Respuesta esperada:** `201 Created`
```json
{
  "id": 1,
  "nombre": "Juan",
  "apellidoPaterno": "Pérez",
  "apellidoMaterno": "García",
  "email": "juan.perez@example.com",
  "fechaCreacion": "2026-09-17T18:45:00",
  "telefonos": [
    { "numero": "5512345678", "tipo": "CELULAR" },
    { "numero": "5522345678", "tipo": "TRABAJO" }
  ]
}
```

#### 2. Listar todos los contactos

**GET** `http://localhost:8080/contactos`

Sin headers ni body.

**Respuesta esperada:** `200 OK`
```json
[
  {
    "id": 1,
    "nombre": "Juan",
    "apellidoPaterno": "Pérez",
    "apellidoMaterno": "García",
    "email": "juan.perez@example.com",
    "fechaCreacion": "2026-09-17T18:45:00",
    "telefonos": [
      { "numero": "5512345678", "tipo": "CELULAR" }
    ]
  }
]
```

#### 3. Obtener un contacto por id

**GET** `http://localhost:8080/contactos/1`

**Path params**
| Key | Value |
|-----|-------|
| `id` | `1` |

Sin headers ni body.

**Respuesta esperada:** `200 OK` con el `ContactoResponse`; `404 Not Found` si el contacto no existe.

#### 4. Actualizar un contacto

**PUT** `http://localhost:8080/contactos/1`

**Path params**
| Key | Value |
|-----|-------|
| `id` | `1` |

**Headers**
| Key | Value |
|-----|-------|
| `Content-Type` | `application/json` |

**Body (raw - JSON)**
```json
{
  "nombre": "Juan Carlos",
  "apellidoPaterno": "Pérez",
  "apellidoMaterno": "García",
  "email": "juancarlos.perez@example.com",
  "telefonos": [
    { "numero": "5512345678", "tipo": "CELULAR" }
  ]
}
```

**Respuesta esperada:** `200 OK` con el `ContactoResponse` actualizado; `404 Not Found` si el contacto no existe; `409 Conflict` si el email ya pertenece a otro contacto.

#### 5. Eliminar un contacto

**DELETE** `http://localhost:8080/contactos/1`

**Path params**
| Key | Value |
|-----|-------|
| `id` | `1` |

Sin headers ni body.

**Respuesta esperada:** `204 No Content`; `404 Not Found` si el contacto no existe.

## Códigos de estado y errores

- **400** Bad Request: falló la validación (campos obligatorios, email inválido, sin teléfonos).
- **404** Not Found: el contacto no existe.
- **409** Conflict: el email ya está registrado en otro contacto.

## Notas y limitaciones

- **Borrado lógico**: `DELETE` marca el contacto como inactivo (`activo = false`), pero no se elimina físicamente. Actualmente `GET /contactos` y `GET /contactos/{id}` no filtran por `activo`, por lo que los contactos "eliminados" siguen apareciendo en las consultas.
- **Email único**: no se pueden registrar dos contactos con el mismo email.
- La hora de creación (`fechaCreacion`) la asigna la aplicación al crear el contacto.
