create table usuario
(
    id         serial
        primary key,
    nombre     varchar(255) not null,
    apellido   varchar(255) not null,
    correo     varchar(255) not null
        unique,
    contrasena varchar(255) not null,
    rol        varchar(255) not null
);

