-- Script de referência das tabelas (o sistema também as cria sozinho ao iniciar).
CREATE TABLE IF NOT EXISTS medicao (
    id        BIGINT AUTO_INCREMENT PRIMARY KEY,
    sensor_id VARCHAR(30) NOT NULL,
    tipo      VARCHAR(30) NOT NULL,
    valor     DOUBLE      NOT NULL,
    data_hora TIMESTAMP   NOT NULL
);

CREATE TABLE IF NOT EXISTS alerta (
    id             BIGINT AUTO_INCREMENT PRIMARY KEY,
    sensor_id      VARCHAR(30)  NOT NULL,
    tipo           VARCHAR(30)  NOT NULL,
    nivel          VARCHAR(20)  NOT NULL,
    valor_previsto DOUBLE       NOT NULL,
    limite         DOUBLE       NOT NULL,
    estrategia     VARCHAR(80)  NOT NULL,
    mensagem       VARCHAR(600) NOT NULL,
    data_hora      TIMESTAMP    NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_medicao_sensor ON medicao (sensor_id);
