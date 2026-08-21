INSERT INTO rol (nombre, descripcion)
VALUES
('ADMINISTRADOR', 'El usuario puede modificar los catálogos, gestionar usuarios.'),
('USUARIO', 'Puede crear planes y guardar itinerarios');

INSERT INTO usuario (contrasena, correo, nombre, apellido_paterno, apellido_materno, estado, rol_id)
VALUES
('$2a$10$NK2IFUBL/Rb4ZbRE0CJKU.Z5ERyAZE9rDwXxhshhPgnb7aKmej8vq', 'alanguba@icloud.com', 'Alan', 'Gutiérrez', 'Banuelos', 1, 1);