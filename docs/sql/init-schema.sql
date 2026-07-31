-- =============================================================================
-- ACJ Signature API - PostgreSQL 16/17 schema (canonical)
-- =============================================================================
-- Script de creacion canonico del esquema de la base de datos.
-- Generado y verificado contra el esquema que produce la aplicacion
-- (Hibernate ddl-auto=update) sobre PostgreSQL.
--
-- Uso en Azure Database for PostgreSQL Flexible Server:
--   1. Crear la base de datos:  CREATE DATABASE android_ws;
--   2. Ejecutar este script como usuario administrador de la base de datos.
--      El esquema se crea en public (schema que ya existe por defecto).
--   3. La aplicacion arranca con ddl-auto=update: si faltara algo, lo crea
--      automaticamente al primer inicio.
--
-- El script es idempotente: puede re-ejecutarse sin error.
-- =============================================================================

-- Roles (admin / user / superadmin)
CREATE TABLE IF NOT EXISTS roles (
    id              BIGSERIAL PRIMARY KEY,
    name            VARCHAR(255) NOT NULL UNIQUE,
    description     VARCHAR(255),
    created_at      TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP,
    created_by      VARCHAR(100),
    updated_by      VARCHAR(100),
    CONSTRAINT roles_name_check CHECK (name IN ('ROLE_USER', 'ROLE_ADMIN', 'ROLE_SUPERADMIN'))
);

-- Users
CREATE TABLE IF NOT EXISTS users (
    id                       BIGSERIAL PRIMARY KEY,
    email                    VARCHAR(100) NOT NULL UNIQUE,
    password                 VARCHAR(255) NOT NULL,
    dni                      VARCHAR(8)   NOT NULL UNIQUE,
    first_name               VARCHAR(100),
    last_name                VARCHAR(100),
    active                   BOOLEAN      NOT NULL DEFAULT TRUE,
    email_verified           BOOLEAN      NOT NULL DEFAULT FALSE,
    -- BCrypt hash of OTP (60 chars); the plaintext OTP is only in the email
    otp_code                 VARCHAR(60),
    otp_expiry_time          TIMESTAMP,
    otp_failed_attempts      INTEGER      NOT NULL DEFAULT 0,
    otp_verified             BOOLEAN      NOT NULL DEFAULT FALSE,
    created_at               TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at               TIMESTAMP,
    created_by               VARCHAR(100),
    updated_by               VARCHAR(100)
);

-- Join table for user <-> role
-- (coincide con la BD real: sin ON DELETE CASCADE, igual que Hibernate)
CREATE TABLE IF NOT EXISTS user_roles (
    user_id    BIGINT NOT NULL,
    role_id    BIGINT NOT NULL,
    PRIMARY KEY (user_id, role_id),
    CONSTRAINT fk_user_roles_user FOREIGN KEY (user_id) REFERENCES users (id),
    CONSTRAINT fk_user_roles_role FOREIGN KEY (role_id) REFERENCES roles (id)
);

-- Seed default roles
INSERT INTO roles (name, description, created_at, updated_at)
VALUES
    ('ROLE_USER',        'Usuario',                CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('ROLE_ADMIN',       'Administrador',          CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('ROLE_SUPERADMIN',  'Super Administrador',    CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
ON CONFLICT (name) DO NOTHING;
