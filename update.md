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

---

## 5. Aporte individual: Implementación de Auditoría Reactiva con Backpressure y Refactorización de Seguridad (Práctica 4)

Mi contribución en esta rama se enfocó en el desarrollo de un flujo reactivo avanzado aplicando conceptos nativos de Reactive Streams (Publisher/Subscriber), la gestión explícita de Backpressure, la actualización de la arquitectura de seguridad JWT y la estabilización del pipeline reactivo del backend.

### 5.1. Implementación de CustomSubscriber y Backpressure Nativo
Para demostrar el control de flujo y evitar el desbordamiento de memoria bajo alta carga, se implementó un suscriptor personalizado (`CustomSubscriber<T>`) que implementa la especificación nativa de `org.reactivestreams.Subscriber`.
- **Control de Demanda (Backpressure):** En lugar de solicitar un volumen indefinido de datos, el suscriptor interactúa dinámicamente con la interfaz `Subscription`, solicitando elementos en bloques controlados mediante un parámetro `batchSize`.
- **Trazabilidad del Ciclo de Vida:** Registra en consola de manera explícita cada fase del stream reactivo:
    - `[Subscriber] Suscripción iniciada. Solicitando N elementos.`
    - `[onNext] Procesado: + valor`
    - `[Subscriber] Solicitando N elementos más.` (al completarse el lote actual).
    - `[onComplete] Flujo finalizado. Total procesados: X`

### 5.2. Capa de Servicios y Endpoint de Auditoría Reactiva
Diseñé e integré la lógica de negocio necesaria para orquestar la auditoría no bloqueante de los objetos perdidos:
- **Controlador (`LostItemController`):** Se creó el endpoint `POST /api/items/auditoria` que retorna un `Mono<ResponseEntity<Map<String, String>>>`.
- **Servicio (`LostItemAuditServiceImpl`):** Implementa la interfaz del servicio de auditoría extrayendo los registros desde `LostItemRepository` de manera reactiva.
- **Consumo del Flujo:** El servicio instancia `CustomSubscriber` con un tamaño de lote estricto (`batchSize = 5`) y lo acopla al flujo mediante la invocación explícita de `.subscribe(subscriber)`, demostrando la manipulación y el consumo directo de publishers sin intermediarios de alto nivel.
- **Optimización en Flujos de Reclamos:** En `ClaimServiceImpl`, se migró el operador `.flatMap()` hacia `.concatMap()` para asegurar que el procesamiento de DTOs mantenga un orden secuencial estricto de llegada en el stream.

### 5.3. Simulación de Flujos Temporales (`ReactiveIntervalDemo`)
- Se implementó la clase `ReactiveIntervalDemo` y su controlador asociado (`ReactiveDemoController`) con el endpoint `POST /api/demo/interval`.
- Genera un flujo asíncronos continuo e infinito simulando emisión de eventos en tiempo real mediante el uso de `Flux.interval(Duration.ofSeconds(3))`, ideal para propósitos de prueba en el frontend.

### 5.4. Actualización y Robustez en Seguridad JWT (Migración a JJWT 0.12.6)
Se actualizaron las dependencias en `build.gradle` de la librería `io.jsonwebtoken` (de la versión `0.11.5` a la `0.12.6`), resolviendo múltiples *breaking changes* de la API mediante la refactorización de `JwtService.java`:
- Se reemplazó el uso de los métodos obsoletos por el nuevo estándar fluido de la librería: `Jwts.parser().verifyWith(getSigningKey()).build().parseSignedClaims(token).getPayload()`.
- Se adaptó la firma y validación de tokens utilizando firmas criptográficas basadas estrictamente bajo la interfaz `SecretKey` en lugar de la clase genérica `Key`.

### 5.5. Estabilización de Filtros y Manejo de Excepciones Reactivas
- **Manejo de Errores en `JwtWebFilter`:** Se introdujo un bloque `try/catch` reactivo interno. Si la extracción del usuario falla por un token expirado o malformado, la excepción se captura de forma segura en el hilo y se delega la petición limpiamente a `chain.filter(exchange)`, garantizando que el pipeline reactivo de WebFlux no se interrumpa ni bloquee el servidor Netty.
- **Rutas Públicas Homogéneas:** Se expandió el filtro de exclusión de seguridad para soportar de manera consistente variantes de endpoints con y sin prefijo global (`/auth/**`, `/api/auth/**`, `/reactive/**`, `/api/reactive/**`).
- **Global Exception Handler:** Se ajustó la captura genérica de excepciones para imprimir de manera explícita el `ex.printStackTrace()` en la consola del servidor y asegurar que las respuestas HTTP de error manejen cabeceras explícitas de tipo `MediaType.APPLICATION_JSON`.