CREATE DATABASE IF NOT EXISTS pythatriple_db;
USE pythatriple_db;

CREATE TABLE IF NOT EXISTS triple_results (
    id BIGINT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
    leg_a INT UNSIGNED NOT NULL,
    leg_b INT UNSIGNED NOT NULL,
    hypotenuse INT UNSIGNED NOT NULL,
    average DOUBLE NOT NULL,
    CONSTRAINT uq_triple UNIQUE (leg_a, leg_b, hypotenuse)
);

CREATE TABLE IF NOT EXISTS calculation_results (
    id BIGINT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
    hypotenuse_squared INT UNSIGNED NOT NULL,
    triple_id BIGINT UNSIGNED NULL,
    calculated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_hypotsq UNIQUE (hypotenuse_squared),
    CONSTRAINT fk_triple FOREIGN KEY (triple_id) REFERENCES triple_results (id)
);
