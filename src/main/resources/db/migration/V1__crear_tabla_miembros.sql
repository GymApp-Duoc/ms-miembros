-- Creación robusta de la tabla miembros adaptada a PostgreSQL
CREATE TABLE miembros (
                          id BIGSERIAL PRIMARY KEY,
                          nombre VARCHAR(100) NOT NULL,
                          apellido VARCHAR(100) NOT NULL,
                          email VARCHAR(150) NOT NULL UNIQUE,
                          telefono VARCHAR(20),
                          fecha_registro DATE NOT NULL,
                          activo BOOLEAN NOT NULL DEFAULT TRUE
);

