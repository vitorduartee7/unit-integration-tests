package com.vtduarte.junitmockito.service;

import com.vtduarte.junitmockito.exception.TarefaNaoEncontradaException;
import com.vtduarte.junitmockito.model.PrioridadeTarefaEnum;
import com.vtduarte.junitmockito.model.StatusTarefaEnum;
import com.vtduarte.junitmockito.model.TarefaEntity;
import com.vtduarte.junitmockito.repository.TarefaRepository;

import java.time.LocalDate;
import java.util.List;

public class TarefaService {

    private final TarefaRepository tarefaRepository;

    public TarefaService(TarefaRepository tarefaRepository) {
        this.tarefaRepository = tarefaRepository;
    }

    public TarefaEntity criar(
            String titulo,
            String descricao,
            PrioridadeTarefaEnum prioridade,
            LocalDate dataVencimento
    ) {

        if (titulo == null || titulo.isBlank()) {
            throw new IllegalArgumentException("Titulo e obrigatorio");
        }

        if (dataVencimento != null && dataVencimento.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Data de vencimento nao pode ser no passado");
        }

        var tarefa = new TarefaEntity(titulo, descricao, prioridade, dataVencimento);

        return tarefaRepository.salvar(tarefa);
    }

    public TarefaEntity buscarPorId(Long id) {
        return tarefaRepository.buscarPorId(id)
                .orElseThrow(() -> new TarefaNaoEncontradaException(id));
    }

    public List<TarefaEntity> listarPendentes() {
        return tarefaRepository.listarPorStatus(StatusTarefaEnum.PENDENTE);
    }

    public void excluir(Long id) {
        TarefaEntity tarefa = buscarPorId(id);
        tarefaRepository.excluir(tarefa.getId());
    }

    public void atualizarPrioridade(Long id, PrioridadeTarefaEnum prioridade) {

        var tarefa = buscarPorId(id);

        tarefa.setPrioridade(prioridade);

        tarefaRepository.salvar(tarefa);
    }

    public void atualizarStatus(TarefaEntity tarefa, StatusTarefaEnum novoStatus) {

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
    }
}
