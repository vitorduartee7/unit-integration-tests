package com.vtduarte.junitmockito.repository;

import com.vtduarte.junitmockito.model.StatusTarefaEnum;
import com.vtduarte.junitmockito.model.TarefaEntity;

import java.util.List;
import java.util.Optional;

public interface TarefaRepository {

    TarefaEntity salvar(TarefaEntity tarefa);
    Optional<TarefaEntity> buscarPorId(Long id);
    List<TarefaEntity> listarPorStatus(StatusTarefaEnum status);
    void excluir(Long id);
}
