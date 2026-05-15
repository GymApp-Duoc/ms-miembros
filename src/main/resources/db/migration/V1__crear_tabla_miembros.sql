-- Creación robusta de la tabla miembros con sus respectivas restricciones (Constraints)
CREATE TABLE miembros (
                          id BIGINT AUTO_INCREMENT PRIMARY KEY,
                          nombre VARCHAR(100) NOT NULL,
                          apellido VARCHAR(100) NOT NULL,
                          email VARCHAR(150) NOT NULL UNIQUE, -- Unique garantiza que la BD no acepte correos duplicados
                          telefono VARCHAR(20),
                          fecha_registro DATE NOT NULL,
                          activo BOOLEAN NOT NULL DEFAULT TRUE -- Por defecto, todo miembro nace activo
);
