CREATE TABLE IF NOT EXISTS notificacoes (
    id                  BIGSERIAL PRIMARY KEY,
    consulta_id         BIGINT NOT NULL,
    paciente_id         BIGINT NOT NULL,
    medico_id           BIGINT NOT NULL,
    data_hora_consulta  TIMESTAMP NOT NULL,
    tipo_evento         VARCHAR(20) NOT NULL,
    mensagem            TEXT NOT NULL,
    status              VARCHAR(20) NOT NULL,
    data_envio          TIMESTAMP NOT NULL DEFAULT NOW()
);
