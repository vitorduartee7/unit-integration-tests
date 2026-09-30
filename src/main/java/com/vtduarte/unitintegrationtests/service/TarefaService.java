package com.vtduarte.unitintegrationtests.service;

import com.vtduarte.unitintegrationtests.exception.TarefaNaoEncontradaException;
import com.vtduarte.unitintegrationtests.exception.TransicaoStatusInvalidaException;
import com.vtduarte.unitintegrationtests.model.PrioridadeTarefaEnum;
import com.vtduarte.unitintegrationtests.model.StatusTarefaEnum;
import com.vtduarte.unitintegrationtests.model.TarefaEntity;
import com.vtduarte.unitintegrationtests.repository.TarefaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TarefaService {

    private final TarefaRepository repository;

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

        return repository.save(tarefa);
    }

    public TarefaEntity buscarPorId(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new TarefaNaoEncontradaException(id));
    }

    public List<TarefaEntity> listarPorStatus(StatusTarefaEnum status) {
        if (status == null) {
            return repository.findAll();
        }
        return repository.findByStatus(status);
    }

    public void marcarComoConcluida(Long id) {
        alterarStatus(id, StatusTarefaEnum.CONCLUIDA);
    }

    public TarefaEntity alterarStatus(Long id, StatusTarefaEnum novoStatus) {
        var tarefa = buscarPorId(id);
        validarTransicao(tarefa.getStatus(), novoStatus);
        tarefa.setStatus(novoStatus);
        tarefa.setConcluidaEm(novoStatus == StatusTarefaEnum.CONCLUIDA
                ? LocalDateTime.now()
                : null);
        return repository.save(tarefa);
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

        return repository.save(tarefa);
    }

    public void atualizarPrioridade(Long id, PrioridadeTarefaEnum prioridade) {
        var tarefa = buscarPorId(id);
        tarefa.setPrioridade(prioridade);
        repository.save(tarefa);
    }

    public void excluir(Long id) {
        if (!repository.existsById(id)) {
            throw new TarefaNaoEncontradaException(id);
        }
        repository.deleteById(id);
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
