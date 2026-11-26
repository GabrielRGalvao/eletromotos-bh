ALTER TABLE ordens_servico
RENAME COLUMN video_chave TO video_entrada_chave;

ALTER TABLE ordens_servico
RENAME COLUMN video_tipo TO video_entrada_tipo;

ALTER TABLE ordens_servico
RENAME COLUMN video_tamanho TO video_entrada_tamanho;

ALTER TABLE ordens_servico
ALTER COLUMN video_entrada_chave
DROP NOT NULL,
ALTER COLUMN video_entrada_tipo
DROP NOT NULL,
ALTER COLUMN video_entrada_tamanho
DROP NOT NULL;

ALTER TABLE ordens_servico
ADD COLUMN video_entrada_em TIMESTAMP
WITH
    TIME ZONE,
ADD COLUMN video_saida_chave VARCHAR(255),
ADD COLUMN video_saida_tipo VARCHAR(50),
ADD COLUMN video_saida_tamanho BIGINT,
ADD COLUMN video_saida_em TIMESTAMP
WITH
    TIME ZONE,
ADD COLUMN entregue_em TIMESTAMP
WITH
    TIME ZONE,
ADD COLUMN entregue_por VARCHAR(150);

ALTER TABLE ordens_servico ADD CONSTRAINT uk_ordens_servico_video_saida UNIQUE (video_saida_chave);

ALTER TABLE ordens_servico ADD CONSTRAINT ck_ordens_servico_video_entrada CHECK (
    (
        video_entrada_chave IS NULL
        AND video_entrada_tipo IS NULL
        AND video_entrada_tamanho IS NULL
    )
    OR (
        video_entrada_chave IS NOT NULL
        AND video_entrada_tipo IS NOT NULL
        AND video_entrada_tamanho > 0
    )
);

ALTER TABLE ordens_servico ADD CONSTRAINT ck_ordens_servico_video_saida CHECK (
    (
        video_saida_chave IS NULL
        AND video_saida_tipo IS NULL
        AND video_saida_tamanho IS NULL
    )
    OR (
        video_saida_chave IS NOT NULL
        AND video_saida_tipo IS NOT NULL
        AND video_saida_tamanho > 0
    )
);

ALTER TABLE ordens_servico ADD CONSTRAINT ck_ordens_servico_entrega CHECK (
    (
        entregue_em IS NULL
        AND entregue_por IS NULL
    )
    OR (
        entregue_em IS NOT NULL
        AND entregue_por IS NOT NULL
    )
);