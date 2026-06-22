# Actualización del Proyecto: Migración a Backend Reactivo (WebFlux + R2DBC)

Hola Grupo D,

Como parte de la **Práctica 3** y para resolver los cuelgues en el frontend por operaciones bloqueantes, he realizado la migración **completa** del backend a una arquitectura **100% reactiva y no bloqueante**. 

A continuación, un resumen de los cambios y de cómo nos organizaremos a partir de ahora:

## 1. Lo que cambió en el Backend

Se eliminó por completo el uso de Spring Web MVC y JPA/Hibernate clásico. 
El nuevo stack tecnológico es:
- **Spring WebFlux**: Ejecutándose sobre Netty (servidor no bloqueante).
- **Spring Data R2DBC**: Driver de base de datos asíncrono y reactivo para MySQL.
- **Seguridad Reactiva**: Migración de `OncePerRequestFilter` a `WebFilter` y uso de `ServerHttpSecurity`.

**¿Qué significa esto para el código?**
- **No más `ResponseEntity<T>` estándar:** Todos los controladores y servicios ahora retornan `Mono<T>` (para 0 a 1 elemento) o `Flux<T>` (para 0 a N elementos).
- **No más `@ManyToOne` (ORM):** R2DBC no es un ORM complejo como Hibernate. Las entidades ahora guardan los IDs foráneos (ej. `Long userId`, `Long itemId`) y los cruces de datos para armar los DTOs se realizan con operaciones reactivas en la capa de servicio usando `Mono.zip` o `.flatMap()`.
- **Imágenes binarias:** Mantenemos la subida de imágenes, pero ahora usamos `FilePart` de WebFlux en lugar del clásico `MultipartFile` de Servlet. Los bytes se leen de forma no bloqueante usando `DataBufferUtils`.

## 2. Compatibilidad del Frontend

**El Frontend no necesita refactorización de llamadas.** 
Se mantuvo exactamente la misma estructura de rutas de la API REST (ej. `/api/auth/login`, `/api/items`, `/api/claims`) y las mismas estructuras JSON para solicitudes y respuestas (DTOs). El frontend de React (Vite) seguirá funcionando igual, pero con mejor rendimiento bajo carga gracias al I/O no bloqueante.

## 3. Extensión de la Práctica 3: El Monitor Reactivo

Se agregó un nuevo flujo en tiempo real (SSE - Server-Sent Events) basado en los conceptos del laboratorio (sensores y promedios):

1. **Sinks.Many (Hot Stream):** Cada vez que se crea, aprueba, rechaza o elimina un reclamo, se emite un evento asíncrono.
2. **Estadísticas Asíncronas (Mono.zip):** Un endpoint que junta 6 conteos distintos de la base de datos de manera paralela y retorna las métricas agregadas (total de reclamos, aprobados, tasa de éxito).
3. **Simulador (Flux.interval):** Un endpoint que inyecta un stream de eventos ficticios cada 3 segundos, similar al simulador de sensores del lab.
4. **Nueva Página Frontend:** Se agregó la página "Monitor Reactivo" en el menú lateral. ¡Pruébenla para ver los datos moviéndose en tiempo real!

## 4. Instrucciones para correr el proyecto

1. A nivel de base de datos, no es necesario hacer un `UPDATE` estructural en MySQL. R2DBC no tiene un "ddl-auto", por lo que agregué un archivo `schema.sql` que inicializa las tablas (si no existen) cuando el backend arranca.
2. Hacer pull de la rama `feature/reactive-backend-webflux`.
3. Levantar el backend como siempre (`./gradlew bootRun`). Verán que ahora arranca **Netty en el puerto 8080** en vez de Tomcat.
4. Levantar el frontend (`npm run dev`).
5. El seeder asíncrono sigue existiendo, así que la base de datos se llenará automáticamente con datos de prueba al iniciar si está vacía.

Si alguno va a agregar una funcionalidad nueva al backend en el futuro, debe usar tipos `Mono` y `Flux`. ¡Cualquier duda, revisen `ClaimServiceImpl` o me avisan!
