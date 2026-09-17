package com.vtduarte.junitymockito.service;

import com.vtduarte.junitymockito.exception.ResourceNotFoundException;
import com.vtduarte.junitymockito.model.PrioridadeTarefaEnum;
import com.vtduarte.junitymockito.model.StatusTarefaEnum;
import com.vtduarte.junitymockito.model.TarefaEntity;
import com.vtduarte.junitymockito.repository.TarefaRepository;

import java.time.LocalDate;

public class TarefaService {

    private final TarefaRepository tarefaRepository;

    public TarefaService(TarefaRepository tarefaRepository) {
        this.tarefaRepository = tarefaRepository;
    }

    public TarefaEntity criar(String titulo, String descricao, PrioridadeTarefaEnum prioridade, LocalDate dataVencimento) {

        if (titulo == null || titulo.isBlank()) {
            throw new IllegalArgumentException("Titulo e obrigatorio");
        }

        if (dataVencimento != null && dataVencimento.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Data de vencimento nao pode ser no passado");
        }

        var tarefa = new TarefaEntity(titulo, descricao, prioridade, dataVencimento);

        return tarefaRepository.salvar(tarefa);
    }

    public TarefaEntity buscarPorId(Long idTarefa) {
        return tarefaRepository.buscarPorId(idTarefa)
                .orElseThrow(() -> new ResourceNotFoundException("Tarefa nao encontrada"));
    }

    public TarefaEntity atualizarStatus(TarefaEntity tarefa, StatusTarefaEnum novoStatus) {

        StatusTarefaEnum statusAtual = tarefa.getStatus();

        switch (statusAtual) {
            case PENDENTE, CONCLUIDA:
                if (novoStatus.equals(StatusTarefaEnum.EM_ANDAMENTO)) {
                    tarefa.setStatus(StatusTarefaEnum.EM_ANDAMENTO);
                } else {
                    throw new IllegalArgumentException("Transição de " + statusAtual
                            + " para " + novoStatus + " não é permitida");
                }
                break;

            case EM_ANDAMENTO:
                if (novoStatus.equals(StatusTarefaEnum.CONCLUIDA)) {
                    tarefa.setStatus(StatusTarefaEnum.CONCLUIDA);
                } else if (novoStatus.equals(StatusTarefaEnum.PENDENTE)) {
                    tarefa.setStatus(StatusTarefaEnum.PENDENTE);
                } else {
                    throw new IllegalArgumentException("Transição de " + statusAtual
                            + " para " + novoStatus + " não é permitida");
                }
                break;
        }

        return tarefa;
    }
}
