package com.vtduarte.unitintegrationtests.model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "tarefas")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Setter
public class TarefaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String titulo;

    private String descricao;

    @Enumerated(EnumType.STRING)
    private StatusTarefaEnum status;

    @Enumerated(EnumType.STRING)
    private PrioridadeTarefaEnum prioridade;

    private LocalDate dataVencimento;

    private LocalDateTime criadaEm;

    private LocalDateTime concluidaEm;

    public TarefaEntity(String titulo, String descricao, PrioridadeTarefaEnum prioridade, LocalDate dataVencimento) {
        this.titulo = titulo;
        this.descricao = descricao;
        this.status = StatusTarefaEnum.PENDENTE;
        this.prioridade = prioridade;
        this.dataVencimento = dataVencimento;
        this.criadaEm = LocalDateTime.now();
        this.concluidaEm = null;
    }
}
