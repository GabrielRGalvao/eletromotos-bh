ALTER TABLE mecanicos
    ADD COLUMN ordem_rodizio INTEGER;

UPDATE mecanicos
SET ordem_rodizio = CASE LOWER(BTRIM(nome))
    WHEN 'edson' THEN 1
    WHEN 'luciano' THEN 2
    WHEN 'gabriel' THEN 3
    ELSE (1000 + id)::INTEGER
END;

ALTER TABLE mecanicos
    ALTER COLUMN ordem_rodizio SET NOT NULL;

CREATE UNIQUE INDEX uk_mecanicos_ordem_rodizio
    ON mecanicos (ordem_rodizio);

CREATE TABLE controle_rodizio_agendamentos (
    id SMALLINT PRIMARY KEY CHECK (id = 1),
    ultimo_mecanico_id BIGINT REFERENCES mecanicos (id)
);

INSERT INTO controle_rodizio_agendamentos (id, ultimo_mecanico_id)
VALUES (1, NULL);