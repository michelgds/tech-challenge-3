CREATE TABLE IF NOT EXISTS usuarios (
    id             BIGSERIAL PRIMARY KEY,
    nome           VARCHAR(255) NOT NULL,
    email          VARCHAR(255) NOT NULL UNIQUE,
    login          VARCHAR(255) NOT NULL UNIQUE,
    senha          VARCHAR(255) NOT NULL,
    role           VARCHAR(20)  NOT NULL,
    especialidade  VARCHAR(100)
);

INSERT INTO usuarios (nome, email, login, senha, role, especialidade)
SELECT 'Dra. Ana Souza', 'ana.medica@hospital.com', 'medico', '$2b$10$NE06c/CmqBMEXCMNVn5xMecfFi0H0vX118NqHQIEw.LkLdymytLpK', 'MEDICO', 'Cardiologia'
WHERE NOT EXISTS (SELECT 1 FROM usuarios WHERE login = 'medico');

INSERT INTO usuarios (nome, email, login, senha, role, especialidade)
SELECT 'Carlos Lima', 'carlos.enfermeiro@hospital.com', 'enfermeiro', '$2b$10$3Y25Frwao0ZDvnMn0eMpHe/qNSoqCzSTWst5Ende0NhC1j7keB5.S', 'ENFERMEIRO', NULL
WHERE NOT EXISTS (SELECT 1 FROM usuarios WHERE login = 'enfermeiro');

INSERT INTO usuarios (nome, email, login, senha, role, especialidade)
SELECT 'João Paciente', 'joao.paciente@email.com', 'paciente', '$2b$10$GIwvwzojwvknGn2O9d4Da.Y..hAZBjWtbgC5WWDJ6ojamkgi3/qkm', 'PACIENTE', NULL
WHERE NOT EXISTS (SELECT 1 FROM usuarios WHERE login = 'paciente');

CREATE TABLE IF NOT EXISTS consultas (
    id                 BIGSERIAL PRIMARY KEY,
    paciente_id        BIGINT NOT NULL REFERENCES usuarios(id),
    medico_id          BIGINT NOT NULL REFERENCES usuarios(id),
    data_hora          TIMESTAMP NOT NULL,
    status             VARCHAR(20) NOT NULL,
    observacoes        TEXT,
    data_criacao       TIMESTAMP NOT NULL DEFAULT NOW(),
    data_atualizacao   TIMESTAMP NOT NULL DEFAULT NOW()
);

INSERT INTO consultas (paciente_id, medico_id, data_hora, status, observacoes)
SELECT (SELECT id FROM usuarios WHERE login = 'paciente'),
       (SELECT id FROM usuarios WHERE login = 'medico'),
       NOW() + INTERVAL '7 days', 'AGENDADA', 'Consulta de rotina'
WHERE NOT EXISTS (SELECT 1 FROM consultas WHERE observacoes = 'Consulta de rotina');
