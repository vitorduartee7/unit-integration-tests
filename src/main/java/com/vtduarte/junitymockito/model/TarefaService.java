package com.vtduarte.junitymockito.model;

import java.time.LocalDate;

public class TarefaService {

    public TarefaEntity criar(String titulo, String descricao, PrioridadeTarefaEnum prioridade, LocalDate dataVencimento) {
        if (titulo == null || titulo.isBlank()) {
            throw new IllegalArgumentException("Título é obrigatório");
        }
        if (dataVencimento != null && dataVencimento.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Data de vencimento não pode ser no passado");
        }
        return new TarefaEntity(titulo, descricao, prioridade, dataVencimento);
    }
}
