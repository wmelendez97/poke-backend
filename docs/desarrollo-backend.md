# Guía de desarrollo backend

Este documento define las reglas mínimas para mantener y extender el backend de la prueba técnica Poke. No plantea una idea de negocio; su propósito es conservar una API clara, verificable y alineada con el alcance técnico existente.

## Objetivo del aplicativo

El backend expone una API REST en Spring Boot para consultar información de Pokémon desde PokeAPI, proteger endpoints con JWT, guardar datos consultados en PostgreSQL y registrar el historial de búsquedas o consultas realizadas.

El objetivo de desarrollo es demostrar integración con una API externa, persistencia, autenticación, documentación OpenAPI, migraciones, manejo de errores, pruebas unitarias y separación por capas.

## Alcance funcional

- Autenticación con `POST /auth/login`.
- Consulta de usuario autenticado con `GET /auth/me`.
- Listado paginado de Pokémon con `GET /pokemon?limit={limit}&offset={offset}`.
- Detalle de Pokémon por id o nombre con `GET /pokemon/{idOrName}`.
- Búsqueda de Pokémon por nombre con `GET /pokemon/search?name={name}`.
- Consulta de habilidades y movimientos con `GET /pokemon/{idOrName}/abilities` y `GET /pokemon/{idOrName}/moves`.
- Consulta de tipos con `GET /types`.
- Consulta de Pokémon por tipo con `GET /types/{name}/pokemon`.
- Historial global y filtrado con `GET /search-history`.
- Historial por usuario con `GET /users/{userId}/search-history`.

No agregar funcionalidades de negocio como ventas, favoritos, recomendaciones, roles comerciales o flujos transaccionales si no forman parte explícita de una prueba técnica nueva.

## Arquitectura esperada

Mantener la estructura actual:

- `controller`: recibe solicitudes HTTP, aplica validación mínima y delega al servicio.
- `service`: concentra lógica de aplicación, mapeos internos, persistencia coordinada y decisiones de caché.
- `repository`: acceso a datos con Spring Data JPA.
- `model`: entidades persistidas.
- `dto/request`: contratos de entrada.
- `dto/response`: contratos de salida.
- `dto/external`: contratos de PokeAPI.
- `proxy/client`: consumo HTTP de PokeAPI.
- `proxy/adapter`: conversión de DTO externo a respuesta interna.
- `token`: validación JWT y anotación `@TokenRequired`.
- `config`: beans técnicos como `RestTemplate`, CORS y configuración general.
- `util`: respuesta estándar, mensajes, errores, OpenAPI, filtros y manejo de excepciones.

## Reglas por capa

### Controller

- Usar `ResponseEntity<ApiResponse<T>>` cuando el patrón aplique.
- Proteger endpoints privados con `@TokenRequired`.
- Documentar endpoints con `@Operation`, `@Tag` y `@SecurityRequirement` cuando corresponda.
- Validar parámetros simples con anotaciones como `@Min`.
- No incluir lógica de negocio ni transformaciones extensas.
- Registrar historial de consultas en endpoints de Pokémon y tipos cuando la acción represente una búsqueda, detalle, paginación o filtro.

### Service

- Mantener la lógica de caché, consulta externa, persistencia y mapeo dentro del servicio.
- Usar `@Transactional` cuando una operación escriba en base de datos o coordine varias persistencias.
- Usar `@Transactional(readOnly = true)` para búsquedas locales sin escritura.
- Preferir métodos privados para conversiones, resolución de caché y persistencia auxiliar.
- Devolver listas vacías cuando PokeAPI no entregue datos esperados, salvo que el contrato requiera error.

### Proxy y adapter

- Centralizar llamadas externas a PokeAPI en `proxy/client/PokeApiClient`.
- Usar `RestTemplate` configurado por `AppConfig`.
- Mantener timeouts de conexión y lectura.
- Convertir respuestas externas en `proxy/adapter/PokeApiPokemonAdapter`.
- No exponer directamente DTOs externos desde controllers.

### Repository y model

- Usar repositorios por entidad.
- Mantener relaciones Pokémon-habilidad, Pokémon-movimiento y Pokémon-tipo en tablas relacionales.
- Guardar `rawJson` solo como respaldo técnico de la respuesta consultada.
- No usar `rawJson` como reemplazo de campos principales cuando exista columna específica.

## Respuesta estándar

Todas las respuestas deben respetar `ApiResponse<T>`:

- `data`: contenido de negocio o `null`.
- `success`: resultado lógico.
- `message`: mensaje centralizado desde `ApiMessages` cuando aplique.
- `code`: código HTTP numérico.
- `status`: estado HTTP.
- `errors`: lista de `ApiError`.

Los errores reutilizables deben declararse en `ApiError.ErrorCodes` y los textos en `ApiMessages`.

## Seguridad

- El login valida usuario activo y contraseña con BCrypt.
- Los endpoints privados usan JWT mediante `@TokenRequired`.
- El aspecto de token debe seguir cargando en request los datos autenticados: id, username, email y name.
- No registrar tokens, contraseñas, secretos ni cabeceras sensibles completas.
- No leer ni modificar archivos `.properties` reales sin confirmación explícita.
- Mantener `application.properties.example` como guía sin credenciales reales.

## Persistencia y caché

- PostgreSQL es la base principal.
- Flyway controla el esquema; todo cambio estructural debe tener migración nueva en `src/main/resources/db/migration`.
- La caché local de Pokémon debe conservar datos suficientes para evitar consultas repetidas innecesarias.
- Si el detalle cacheado está incompleto, el servicio puede volver a consultar PokeAPI.
- El historial debe guardar usuario, tipo de búsqueda, valor consultado, endpoint, éxito, código de estado, error y fecha.

## Manejo de errores

- Errores no controlados deben pasar por `GlobalExceptionHandler`.
- Errores de validación deben responder `400`.
- Errores de integridad de base de datos deben responder `409`.
- Token ausente o inválido debe responder `401`.
- Problemas con PokeAPI deben diferenciar timeout (`504`) de error externo general (`502`) cuando el controller ya tenga ese patrón.

## OpenAPI y Postman

- Mantener Swagger UI disponible en `/api/swagger-ui/index.html`.
- Mantener OpenAPI disponible en `/v3/api-docs`.
- Actualizar `docs/POKE.postman_collection.json` cuando se agreguen, eliminen o cambien endpoints.
- El login de la colección debe conservar el guardado automático de `{{token}}`.

## Pruebas

- Priorizar pruebas unitarias de services y controllers cuando cambie comportamiento.
- Usar H2 solo en contexto de pruebas, como ya está configurado.
- No correr pruebas salvo necesidad clara, pero todo cambio funcional debe dejar indicada la prueba recomendada.
- Cubrir casos de éxito, token requerido, error externo, cache local e historial cuando el cambio toque esos flujos.

## Criterios para nuevos endpoints

- Definir primero el contrato HTTP.
- Agregar DTOs de request/response si el contrato no encaja en modelos existentes.
- Implementar controller delgado.
- Implementar lógica en service.
- Agregar repositorio o migración solo si hay persistencia nueva.
- Documentar con OpenAPI.
- Agregar o actualizar colección Postman.
- Agregar prueba mínima si cambia comportamiento observable.

## Antipatrones

- Crear reglas de negocio ficticias para justificar pantallas o endpoints.
- Llamar PokeAPI desde controllers.
- Retornar entidades JPA directamente.
- Devolver DTOs externos de PokeAPI como contrato público.
- Duplicar mensajes de error fuera de `ApiMessages`.
- Crear migraciones que modifiquen datos sin necesidad técnica.
- Guardar secretos en repositorio.
- Mezclar lógica de autenticación dentro de services de Pokémon.
- Cambiar contratos sin actualizar README, Swagger o Postman.

## Checklist de desarrollo

- Endpoint protegido con `@TokenRequired` si corresponde.
- Respuesta envuelta en `ApiResponse<T>`.
- Mensajes y códigos centralizados.
- Lógica principal en service.
- Llamadas externas solo en proxy.
- DTO externo adaptado antes de responder.
- Historial registrado cuando aplique.
- Migración Flyway incluida si cambia esquema.
- Swagger actualizado.
- Colección Postman actualizada si cambia API.
- Sin secretos ni datos sensibles en logs.
- Prueba recomendada o agregada según riesgo.
