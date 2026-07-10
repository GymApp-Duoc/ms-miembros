# Microservicio: Gestión de Miembros (ms-miembros) 🏋️‍♀️

## Descripción del Proyecto
Este repositorio contiene el microservicio central de la plataforma **GymApp**, encargado de la gestión integral 
del ciclo de vida de los clientes (miembros) del gimnasio.

El microservicio está diseñado para manejar la creación, actualización, validación y borrado lógico de usuarios, 
garantizando la integridad de los datos y permitiendo la generación de reportes estratégicos para la toma de decisiones comerciales.

## Desarrolladora
**Constanza Cerda** - *Desarrollo Backend y Arquitectura*

## Stack Tecnológico y Buenas Prácticas
Este proyecto ha sido desarrollado cumpliendo con los más altos estándares de la industria y los requerimientos técnicos 
de la Evaluación Final Transversal (EFT):

* **Framework:** Spring Boot (Java 17).
* **Arquitectura:** Patrón multicapa CRS (Controller, Service, Repository) totalmente desacoplado.
* **Inversión de Control (IoC):** Inyección de dependencias explícita mediante `@Autowired` en los componentes estratégicos.
* **Transferencia de Datos:** Implementación estricta de DTOs (Request/Response) y Assemblers para no exponer las entidades de la base de datos.
* **Base de Datos:** Migración nativa a **PostgreSQL** para su despliegue en la nube.
* **Control de Versiones BD:** Uso de **Flyway** para la creación automática de esquemas (`BIGSERIAL`) y poblado de Data Demo inicial.
* **Manejo de Excepciones:** Controlador global (`@RestControllerAdvice`) para capturar errores de negocio y validaciones (HTTP 400, 404, 500).

## Despliegue en la Nube (Render.com)
El microservicio está preparado y configurado para operar en un entorno real a través de Render.

Las credenciales de la base de datos están protegidas y se inyectan dinámicamente mediante las siguientes variables de entorno 
(configuradas en el archivo `application.yml`):
* `DB_URL`
* `DB_USER`
* `DB_PASSWORD`

## Documentación de la API (Swagger / OpenAPI)
La API está completamente documentada. Para probar los endpoints y visualizar la interfaz gráfica (UI), visite la siguiente ruta en el entorno desplegado:

`https://[URL-DE-RENDER]/doc/swagger-ui/index.html`

## Endpoints Principales
El controlador expone, entre otros, los siguientes servicios RESTful:

* `GET /api/miembros` - Lista todos los miembros activos.
* `GET /api/miembros/{id}` - Obtiene el detalle de un miembro específico.
* `POST /api/miembros` - Registra un nuevo miembro en el sistema.
* `PUT /api/miembros/{id}` - Actualiza la información de contacto.
* `DELETE /api/miembros/{id}` - Aplica un borrado lógico (desactivación).
* `GET /api/miembros/reportes/activos/total` - Retorna métricas de usuarios activos.