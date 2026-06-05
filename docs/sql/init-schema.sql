-- =============================================================================
-- ACJ Signature API - PostgreSQL 16 initial schema
-- =============================================================================
-- This script creates the initial database schema for PostgreSQL 16.
-- The application runs with ddl-auto=update, so this is the canonical
-- baseline. Use this when creating a fresh database; running the app
-- will add columns incrementally as the entity model evolves.
-- =============================================================================

-- Pre-requisites (run as postgres superuser):
--   CREATE DATABASE android_ws;
--   CREATE USER android_ws WITH PASSWORD 'your_secure_password';
--   GRANT ALL PRIVILEGES ON DATABASE android_ws TO android_ws;
--   \c android_ws
--   GRANT ALL ON SCHEMA public TO android_ws;

-- Roles (admin / user / superadmin)
CREATE TABLE IF NOT EXISTS roles (
    id              BIGSERIAL PRIMARY KEY,
    name            VARCHAR(50)  NOT NULL UNIQUE,
    description     VARCHAR(255),
    created_at      TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP,
    created_by      VARCHAR(100),
    updated_by      VARCHAR(100)
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
    created_at               TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at               TIMESTAMP,
    created_by               VARCHAR(100),
    updated_by               VARCHAR(100)
);

-- Join table for user <-> role
CREATE TABLE IF NOT EXISTS user_roles (
    user_id    BIGINT NOT NULL,
    role_id    BIGINT NOT NULL,
    PRIMARY KEY (user_id, role_id),
    CONSTRAINT fk_user_roles_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
    CONSTRAINT fk_user_roles_role FOREIGN KEY (role_id) REFERENCES roles (id) ON DELETE CASCADE
);

-- Indexes
CREATE INDEX IF NOT EXISTS idx_users_email ON users (email);
CREATE INDEX IF NOT EXISTS idx_users_dni   ON users (dni);
CREATE INDEX IF NOT EXISTS idx_roles_name  ON roles (name);

-- Seed default roles
INSERT INTO roles (name, description, created_at, updated_at)
VALUES
    ('ROLE_USER',        'Usuario',                CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('ROLE_ADMIN',       'Administrador',          CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('ROLE_SUPERADMIN',  'Super Administrador',    CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
ON CONFLICT (name) DO NOTHING;
