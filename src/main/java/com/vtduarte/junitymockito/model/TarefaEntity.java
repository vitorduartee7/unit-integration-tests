package com.vtduarte.junitymockito.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class TarefaEntity {
    private Long id;
    private String titulo;
    private String descricao;
    private StatusTarefaEnum status;
    private PrioridadeTarefaEnum prioridade;
    private LocalDate dataVencimento;
    private LocalDateTime criadaEm;
}
