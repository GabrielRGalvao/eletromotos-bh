CREATE TABLE agendamentos (
    id BIGSERIAL PRIMARY KEY,
    veiculo_id BIGINT NOT NULL,
    mecanico_id BIGINT NOT NULL,
    data_agendamento DATE NOT NULL,
    hora_agendamento TIME NOT NULL,
    relato_cliente TEXT NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'AGENDADO',
    data_cadastro TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_agendamentos_veiculo
        FOREIGN KEY (veiculo_id)
        REFERENCES veiculos (id),

    CONSTRAINT fk_agendamentos_mecanico
        FOREIGN KEY (mecanico_id)
        REFERENCES mecanicos (id),

    CONSTRAINT ck_agendamentos_status
        CHECK (status IN ('AGENDADO', 'RECEBIDO', 'CANCELADO'))
);

CREATE INDEX idx_agendamentos_veiculo
    ON agendamentos (veiculo_id);

CREATE INDEX idx_agendamentos_mecanico_data
    ON agendamentos (mecanico_id, data_agendamento);

CREATE INDEX idx_agendamentos_data_hora
    ON agendamentos (data_agendamento, hora_agendamento);