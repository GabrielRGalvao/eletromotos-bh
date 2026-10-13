package br.com.eletromotosbh.mecanico;

import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "mecanicos")
public class Mecanico {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String nome;

    @Column(nullable = false)
    private boolean ativo = true;

    @Column(name = "data_cadastro", nullable = false, updatable = false)
    private Instant dataCadastro;

    protected Mecanico() {
    }

    public Mecanico(String nome) {
        this.nome = nome;
    }

    @PrePersist
    private void definirDataCadastro() {
        if (dataCadastro == null) {
            dataCadastro = Instant.now();
        }
    }

    public void atualizarDados(String nome) {
        this.nome = nome;
    }

    public void ativar() {
        this.ativo = true;
    }

    public void inativar() {
        this.ativo = false;
    }

    public Long getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public boolean isAtivo() {
        return ativo;
    }

    public Instant getDataCadastro() {
        return dataCadastro;
    }
}