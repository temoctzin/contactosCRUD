# AgendaTelefonica

API REST para la gestión de una agenda telefónica. Permite crear, listar, consultar, actualizar y eliminar contactos, cada uno con uno o más teléfonos.

## Stack tecnológico

- **Java 17**
- **Spring Boot 4.1.1**
- **Spring Data JPA / Hibernate**
- **MySQL 8+**
- **Gradle 9.7.1** (wrapper incluido, no requiere instalación)
- Empaquetado: WAR

## Requisitos

- JDK 17 instalado
- MySQL 8+ en ejecución
- Puertos libres: `8080` (aplicación) y `3306` (MySQL)

## Configuración de base de datos

1. Crea la base de datos (por defecto `examenJavaSalinas`):

   ```sql
   CREATE DATABASE examenJavaSalinas;
   ```

2. Proporciona credenciales vía variables de entorno, o usa los valores por defecto:

   | Variable | Default | Descripción |
   |----------|---------|-------------|
   | `DB_HOST` | `localhost` | Host de MySQL |
   | `DB_PORT` | `3306` | Puerto de MySQL |
   | `DB_NAME` | `examenJavaSalinas` | Nombre de la base de datos |
   | `DB_USER` | `root` | Usuario de MySQL |
   | `DB_PASSWORD` | `devpass` | Contraseña del usuario |

   Las tablas se crean automáticamente (`ddl-auto=update`), no se necesitan scripts SQL.

## Cómo correr el proyecto

Desde la carpeta del proyecto:

```bash
cd agendaTelefonica
./gradlew bootRun
```

La aplicación se levanta en **http://localhost:8080**.

Para generar el artefacto (WAR):

```bash
./gradlew build
```

El archivo resultante queda en `agendaTelefonica/build/libs/`.

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