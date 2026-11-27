ALTER TABLE ordens_servico
DROP CONSTRAINT ck_ordens_servico_status;

ALTER TABLE ordens_servico ADD CONSTRAINT ck_ordens_servico_status CHECK (
    status IN (
        'EM_DIAGNOSTICO',
        'AGUARDANDO_APROVACAO',
        'APROVADO',
        'REPROVADO',
        'EM_EXECUCAO',
        'CONCLUIDO',
        'ENTREGUE'
    )
);