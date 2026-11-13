# Biblioteca API – Spring Boot REST desplegado en GitHub Codespaces

API REST que gestiona **libros**. Expone un método **GET** y un método **POST** que procesan
objetos `Libro` con 6 campos: `id`, `titulo`, `autor`, `anioPublicacion`, `genero`, `precio`.

| Método | Ruta               | Qué hace                        | Respuesta |
|--------|--------------------|---------------------------------|-----------|
| GET    | `/api/libros`      | Lista todos los libros          | 200 OK |
| GET    | `/api/libros/{id}` | Devuelve un libro por su id     | 200 / 404 |
| POST   | `/api/libros`      | Crea un libro (JSON en el body) | 201 Created / 400 |

---

## 1. Estructura del proyecto (y para qué sirve cada parte)

```
biblioteca-api/
├── .devcontainer/devcontainer.json   ← Le dice a Codespaces qué instalar (Java 21 + Maven) y qué puerto abrir
├── pom.xml                           ← Dependencias de Maven (Spring Web, Validation)
├── postman/…collection.json          ← Peticiones listas para importar en Postman/Insomnia
└── src/main/
    ├── java/com/ejemplo/biblioteca/
    │   ├── BibliotecaApplication.java   ← main(): arranca Spring Boot y Tomcat
    │   ├── model/Libro.java             ← El objeto (6 campos + validaciones)
    │   ├── service/LibroService.java    ← Lógica: guarda libros en memoria
    │   └── controller/LibroController.java ← Endpoints GET y POST
    └── resources/application.properties ← Puerto 8080 y JSON bonito
```

**Arquitectura en capas:** Cliente (Postman) → **Controller** (HTTP/JSON) → **Service** (lógica) → datos en memoria.

---

## 2. Subir el proyecto a GitHub

1. Entra a <https://github.com/new>, crea un repositorio **público** llamado `biblioteca-api` (sin README).
2. En la carpeta del proyecto:
   ```bash
   git remote add origin https://github.com/TU_USUARIO/biblioteca-api.git
   git push -u origin main
   ```
   (La primera vez se abre el navegador para iniciar sesión en GitHub.)

## 3. Abrir en GitHub Codespaces

1. En la página del repo: botón verde **Code** → pestaña **Codespaces** → **Create codespace on main**.
2. Espera 1–3 min: Codespaces lee `.devcontainer/devcontainer.json` e instala Java 21 + Maven.
3. Verifica en la terminal del Codespace:
   ```bash
   java -version
   mvn -version
   ```

## 4. Ejecutar (desplegar) la aplicación

```bash
mvn spring-boot:run
```
Cuando veas `Tomcat started on port 8080` y `Started BibliotecaApplication`, la app está arriba.

## 5. Hacer público el puerto 8080

1. Pestaña **PORTS** (abajo, junto a TERMINAL).
2. Fila del puerto **8080** → clic derecho → **Port Visibility → Public**.
3. Copia la **Forwarded Address**, algo como:
   `https://NOMBRE-CODESPACE-8080.app.github.dev`

> Si queda en *Private*, Postman recibe una página de login de GitHub en vez de JSON.

## 6. Probar con Postman (o Insomnia)

1. Postman → **Import** → elige `postman/Biblioteca-API.postman_collection.json`.
2. En la colección → pestaña **Variables** → cambia `baseUrl` por la URL del paso 5 (**sin** `/` al final) → **Save**.
3. Ejecuta en orden:

**GET** `{{baseUrl}}/api/libros` → 200 con 2 libros iniciales

**POST** `{{baseUrl}}/api/libros`, Body → raw → JSON:
```json
{
  "titulo": "El principito",
  "autor": "Antoine de Saint-Exupéry",
  "anioPublicacion": 1943,
  "genero": "Fábula",
  "precio": 35.00
}
```
→ **201 Created**, devuelve el libro con `"id": 3`.

**GET** `{{baseUrl}}/api/libros/3` → 200 con el libro creado.  
**GET** `{{baseUrl}}/api/libros` → ahora salen 3 libros.

**POST con datos inválidos** (título vacío, año 3000, precio -5) → **400** con los mensajes de error.

También con curl desde la terminal del Codespace:
```bash
curl http://localhost:8080/api/libros
curl -X POST http://localhost:8080/api/libros -H "Content-Type: application/json" \
  -d '{"titulo":"Rayuela","autor":"Julio Cortázar","anioPublicacion":1963,"genero":"Novela","precio":40}'
```

---

## 7. Guion para la presentación (2–3 minutos)

1. "Mi app es una API REST de una biblioteca hecha con Spring Boot 3 y Java 21."
2. Mostrar el repo en GitHub y el Codespace abierto. "El archivo `devcontainer.json` configura el entorno: Java 21, Maven y el puerto 8080."
3. Mostrar `Libro.java`: "El objeto tiene 6 campos: id, título, autor, año, género y precio, con validaciones."
4. Mostrar `LibroController.java`: "`@RestController` + `@RequestMapping('/api/libros')`. `@GetMapping` lista, `@PostMapping` crea; `@RequestBody` convierte el JSON a objeto y `@Valid` lo valida."
5. Terminal: `mvn spring-boot:run` → "Tomcat arrancó en el 8080."
6. PORTS → puerto público → copiar URL.
7. Postman: GET (200) → POST (201) → GET de nuevo (aparece el nuevo) → POST inválido (400).

## 8. Preguntas probables del profesor

- **¿Qué es REST?** Estilo de arquitectura donde los recursos (libros) se manejan por URL con verbos HTTP (GET leer, POST crear, PUT actualizar, DELETE borrar), normalmente en JSON y sin estado.
- **¿Diferencia GET vs POST?** GET obtiene datos, no lleva body y es idempotente. POST envía datos en el body para crear un recurso; repetirlo crea otro.
- **¿Qué es Codespaces?** Un entorno de desarrollo en la nube (contenedor Linux + VS Code) creado desde un repo de GitHub.
- **¿Qué es el devcontainer?** Archivo que define el contenedor: imagen, herramientas, extensiones y puertos. Hace el entorno reproducible.
- **¿Qué hace `@RestController`?** Marca la clase como controlador cuyas respuestas se escriben directo como JSON.
- **¿`@RequestBody`?** Convierte el JSON del body en un objeto Java (lo hace Jackson).
- **¿`@PathVariable`?** Toma un valor de la URL, ej. el `3` en `/api/libros/3`.
- **¿Por qué 201 y no 200 en el POST?** 201 Created indica que se creó un recurso nuevo.
- **¿Dónde se guardan los datos?** En memoria (`ConcurrentHashMap`); se pierden al reiniciar. Para persistir se usaría JPA + una base de datos.
- **¿Qué es Maven / pom.xml?** Herramienta de construcción y gestión de dependencias; el `pom.xml` las declara.
- **¿Qué es la inyección de dependencias?** Spring crea el `LibroService` y lo pasa al constructor del controlador; no usamos `new`.
- **¿Qué es un `record`?** Clase inmutable de Java que genera constructor, getters, `equals` y `toString` automáticamente.

## 9. Problemas comunes

| Problema | Solución |
|---|---|
| `mvn: command not found` | El contenedor no terminó; o usa *Rebuild Container* (Ctrl+Shift+P). |
| `Port 8080 already in use` | Detén la otra ejecución con Ctrl+C o `pkill -f spring-boot`. |
| Postman devuelve HTML de login | El puerto está *Private*: cámbialo a *Public*. |
| 415 Unsupported Media Type | Falta `Content-Type: application/json` (en Postman: Body → raw → **JSON**). |
| 400 Bad Request | El JSON tiene errores o no cumple validaciones (revisa el mensaje). |
| 404 en GET por id | Ese id no existe (los datos se reinician al reiniciar la app). |
