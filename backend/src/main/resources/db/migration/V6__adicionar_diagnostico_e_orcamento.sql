ALTER TABLE ordens_servico
    ADD COLUMN diagnostico TEXT,
    ADD COLUMN descricao_orcamento TEXT,
    ADD COLUMN valor_orcamento NUMERIC(12, 2),
    ADD COLUMN orcamento_enviado_em TIMESTAMP WITH TIME ZONE,
    ADD COLUMN decisao_orcamento_em TIMESTAMP WITH TIME ZONE,
    ADD COLUMN observacao_decisao TEXT;

ALTER TABLE ordens_servico
    DROP CONSTRAINT ck_ordens_servico_status;

ALTER TABLE ordens_servico
    ADD CONSTRAINT ck_ordens_servico_status
    CHECK (
        status IN (
            'EM_DIAGNOSTICO',
            'AGUARDANDO_APROVACAO',
            'APROVADO',
            'REPROVADO'
        )
    );

CREATE INDEX idx_ordens_servico_status
    ON ordens_servico (status);