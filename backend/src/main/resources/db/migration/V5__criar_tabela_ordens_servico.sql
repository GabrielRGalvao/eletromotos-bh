CREATE TABLE ordens_servico (
    id BIGSERIAL PRIMARY KEY,
    agendamento_id BIGINT NOT NULL,
    veiculo_id BIGINT NOT NULL,
    mecanico_id BIGINT NOT NULL,
    relato_cliente TEXT NOT NULL,
    carregador_recebido BOOLEAN NOT NULL,
    acessorios_recebidos TEXT NOT NULL,
    observacoes_recebimento TEXT,
    video_chave VARCHAR(255) NOT NULL,
    video_tipo VARCHAR(50) NOT NULL,
    video_tamanho BIGINT NOT NULL,
    status VARCHAR(30) NOT NULL DEFAULT 'EM_DIAGNOSTICO',
    recebido_em TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT uk_ordens_servico_agendamento
        UNIQUE (agendamento_id),

    CONSTRAINT uk_ordens_servico_video
        UNIQUE (video_chave),

    CONSTRAINT fk_ordens_servico_agendamento
        FOREIGN KEY (agendamento_id)
        REFERENCES agendamentos (id),

    CONSTRAINT fk_ordens_servico_veiculo
        FOREIGN KEY (veiculo_id)
        REFERENCES veiculos (id),

    CONSTRAINT fk_ordens_servico_mecanico
        FOREIGN KEY (mecanico_id)
        REFERENCES mecanicos (id),

    CONSTRAINT ck_ordens_servico_video_tamanho
        CHECK (video_tamanho > 0),

    CONSTRAINT ck_ordens_servico_status
        CHECK (status IN ('EM_DIAGNOSTICO'))
);

CREATE INDEX idx_ordens_servico_veiculo
    ON ordens_servico (veiculo_id);

CREATE INDEX idx_ordens_servico_mecanico
    ON ordens_servico (mecanico_id);

CREATE INDEX idx_ordens_servico_recebido_em
    ON ordens_servico (recebido_em);    