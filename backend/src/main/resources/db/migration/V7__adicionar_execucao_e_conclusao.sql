ALTER TABLE ordens_servico
    ADD COLUMN execucao_iniciada_em TIMESTAMP WITH TIME ZONE,
    ADD COLUMN concluido_em TIMESTAMP WITH TIME ZONE,
    ADD COLUMN observacoes_conclusao TEXT;

ALTER TABLE ordens_servico
    DROP CONSTRAINT ck_ordens_servico_status;

ALTER TABLE ordens_servico
    ADD CONSTRAINT ck_ordens_servico_status
    CHECK (
        status IN (
            'EM_DIAGNOSTICO',
            'AGUARDANDO_APROVACAO',
            'APROVADO',
            'REPROVADO',
            'EM_EXECUCAO',
            'CONCLUIDO'
        )
    );

CREATE INDEX idx_ordens_servico_concluido_em
    ON ordens_servico (concluido_em);