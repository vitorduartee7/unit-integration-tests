package com.vtduarte.unitintegrationtests.dto;

import com.vtduarte.unitintegrationtests.model.PrioridadeTarefaEnum;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record AtualizarTarefaRequest(
        @NotBlank(message = "Título é obrigatório") String titulo,
        String descricao,
        @NotNull PrioridadeTarefaEnum prioridade,
        @Future(message = "Data de vencimento deve ser futura") LocalDate dataVencimento
) {}
