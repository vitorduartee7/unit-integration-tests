package com.vtduarte.junitmockito.model;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Data
public class TarefaEntity {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String titulo;
    private String descricao;
    private StatusTarefaEnum status;
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
        this.criadaEm = LocalDateTime
                .now(ZoneId.systemDefault());
    }
}
