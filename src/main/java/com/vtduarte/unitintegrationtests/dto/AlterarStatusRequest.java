package com.vtduarte.unitintegrationtests.dto;

import com.vtduarte.unitintegrationtests.model.StatusTarefaEnum;
import jakarta.validation.constraints.NotNull;

public record AlterarStatusRequest(@NotNull StatusTarefaEnum novoStatus) {
}
