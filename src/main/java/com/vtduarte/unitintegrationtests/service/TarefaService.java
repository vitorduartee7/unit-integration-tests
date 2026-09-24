package com.vtduarte.unitintegrationtests.service;

import com.vtduarte.unitintegrationtests.exception.TarefaNaoEncontradaException;
import com.vtduarte.unitintegrationtests.exception.TransicaoStatusInvalidaException;
import com.vtduarte.unitintegrationtests.model.PrioridadeTarefaEnum;
import com.vtduarte.unitintegrationtests.model.StatusTarefaEnum;
import com.vtduarte.unitintegrationtests.model.TarefaEntity;
import com.vtduarte.unitintegrationtests.repository.TarefaRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

public class TarefaService {

    private final TarefaRepository tarefaRepository;

    public TarefaService(TarefaRepository tarefaRepository) {
        this.tarefaRepository = tarefaRepository;
    }

    public TarefaEntity criarTarefa(
            String titulo,
            String descricao,
            PrioridadeTarefaEnum prioridade,
            LocalDate dataVencimento
    ) {

        if (titulo == null || titulo.isBlank()) {
            throw new IllegalArgumentException("Titulo e obrigatorio");
        }

        if (dataVencimento != null && dataVencimento.isBefore(LocalDate.now(ZoneId.systemDefault()))) {
            throw new IllegalArgumentException("Data de vencimento nao pode ser no passado");
        }

        var tarefa = new TarefaEntity(titulo, descricao, prioridade, dataVencimento);

        return tarefaRepository.salvar(tarefa);
    }

    public TarefaEntity buscarPorId(Long id) {
        return tarefaRepository.buscarPorId(id)
                .orElseThrow(() -> new TarefaNaoEncontradaException(id));
    }

    public List<TarefaEntity> listarTodas() {
        return tarefaRepository.listarTodas();
    }

    public List<TarefaEntity> listarPendentes() {
        return tarefaRepository.listarPorStatus(StatusTarefaEnum.PENDENTE);
    }

    public void marcarComoConcluida(Long id) {
        var tarefa = buscarPorId(id);
        validarTransicao(tarefa.getStatus(), StatusTarefaEnum.CONCLUIDA);
        tarefa.setStatus(StatusTarefaEnum.CONCLUIDA);
        tarefa.setConcluidaEm(LocalDateTime.now(ZoneId.systemDefault()));
        tarefaRepository.salvar(tarefa);
    }

    public TarefaEntity atualizarTarefa(
            Long id,
            String titulo,
            String descricao,
            PrioridadeTarefaEnum prioridade,
            LocalDate dataVencimento
    ) {
        var tarefa = buscarPorId(id);

        if (titulo == null || titulo.isBlank()) {
            throw new IllegalArgumentException("Titulo e obrigatorio");
        }

        if (dataVencimento != null && dataVencimento.isBefore(LocalDate.now(ZoneId.systemDefault()))) {
            throw new IllegalArgumentException("Data de vencimento nao pode ser no passado");
        }

        tarefa.setTitulo(titulo);
        tarefa.setDescricao(descricao);
        tarefa.setPrioridade(prioridade);
        tarefa.setDataVencimento(dataVencimento);

        return tarefaRepository.salvar(tarefa);
    }

    public void atualizarPrioridade(Long id, PrioridadeTarefaEnum prioridade) {
        var tarefa = buscarPorId(id);
        tarefa.setPrioridade(prioridade);
        tarefaRepository.salvar(tarefa);
    }

    public TarefaEntity alterarStatus(Long id, StatusTarefaEnum novoStatus) {
        var tarefa = buscarPorId(id);
        validarTransicao(tarefa.getStatus(), novoStatus);
        tarefa.setStatus(novoStatus);
        return tarefaRepository.salvar(tarefa);
    }

    public void excluir(Long id) {
        TarefaEntity tarefa = buscarPorId(id);
        tarefaRepository.excluir(tarefa.getId());
    }

    private void validarTransicao(StatusTarefaEnum statusAtual, StatusTarefaEnum novoStatus) {
        switch (statusAtual) {
            case PENDENTE, CONCLUIDA:
                if (!novoStatus.equals(StatusTarefaEnum.EM_ANDAMENTO)) {
                    throw new TransicaoStatusInvalidaException("Transição de " + statusAtual
                            + " para " + novoStatus + " não é permitida");
                }
                break;
            case EM_ANDAMENTO:
                if (!novoStatus.equals(StatusTarefaEnum.CONCLUIDA) && !novoStatus.equals(StatusTarefaEnum.PENDENTE)) {
                    throw new TransicaoStatusInvalidaException("Transição de " + statusAtual
                            + " para " + novoStatus + " não é permitida");
                }
                break;
        }
    }
}
