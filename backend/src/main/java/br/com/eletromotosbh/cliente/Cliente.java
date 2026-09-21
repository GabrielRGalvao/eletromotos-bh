package br.com.eletromotosbh.cliente;

import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

@Entity
@Table(name = "clientes")
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String nome;

    @Column(nullable = false, length = 20)
    private String telefone;

    @Column(length = 254)
    private String email;

    @Column(length = 11)
    private String cpf;

    @Column(columnDefinition = "TEXT")
    private String observacoes;

    @Column(name = "data_cadastro", nullable = false, updatable = false)
    private Instant dataCadastro;

    protected Cliente() {
    }

    public Cliente(String nome, String telefone, String email,
                   String cpf, String observacoes) {
        this.nome = nome;
        this.telefone = telefone;
        this.email = email;
        this.cpf = cpf;
        this.observacoes = observacoes;
    }

    @PrePersist
    private void definirDataCadastro() {
        dataCadastro = Instant.now();
    }

    public Long getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getTelefone() {
        return telefone;
    }

    public String getEmail() {
        return email;
    }

    public String getCpf() {
        return cpf;
    }

    public String getObservacoes() {
        return observacoes;
    }

    public Instant getDataCadastro() {
        return dataCadastro;
    }
}