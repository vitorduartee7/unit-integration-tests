package com.vtduarte.junitymockito.repository;

import com.vtduarte.junitymockito.model.TarefaEntity;

import java.util.Optional;

public interface TarefaRepository {

    TarefaEntity salvar(TarefaEntity tarefa);

    Optional<TarefaEntity> buscarPorId(Long id);
}
