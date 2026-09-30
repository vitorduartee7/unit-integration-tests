package com.vtduarte.unitintegrationtests.repository;

import com.vtduarte.unitintegrationtests.model.StatusTarefaEnum;
import com.vtduarte.unitintegrationtests.model.TarefaEntity;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TarefaRepository {

    TarefaEntity salvar(TarefaEntity tarefa);
    Optional<TarefaEntity> buscarPorId(Long id);
    List<TarefaEntity> listarTodas();
    List<TarefaEntity> listarPorStatus(StatusTarefaEnum status);
    void excluir(Long id);
}
