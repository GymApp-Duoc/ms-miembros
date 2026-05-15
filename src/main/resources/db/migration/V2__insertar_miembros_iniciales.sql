-- Insertar miembros activos (estado activo = true)
INSERT INTO miembros (nombre, apellido, email, telefono, fecha_registro, activo)
VALUES ('Carlos', 'Ramírez', 'carlos.ramirez@email.com', '+56912345678', '2026-04-10', TRUE);

INSERT INTO miembros (nombre, apellido, email, telefono, fecha_registro, activo)
VALUES ('Andrea', 'Silva', 'andrea.silva@email.com', '+56987654321', '2026-05-01', TRUE);

-- Insertar un miembro inactivo para probar el borrado lógico (estado activo = false)
INSERT INTO miembros (nombre, apellido, email, telefono, fecha_registro, activo)
VALUES ('Luis', 'Pérez', 'luis.perez@email.com', '+56911223344', '2025-12-15', FALSE);
