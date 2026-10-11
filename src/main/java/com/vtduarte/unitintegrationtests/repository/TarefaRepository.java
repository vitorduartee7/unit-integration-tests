package com.vtduarte.unitintegrationtests.repository;

import com.vtduarte.unitintegrationtests.model.PrioridadeTarefaEnum;
import com.vtduarte.unitintegrationtests.model.StatusTarefaEnum;
import com.vtduarte.unitintegrationtests.model.TarefaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface TarefaRepository extends JpaRepository<TarefaEntity, Long> {

    List<TarefaEntity> findByStatus(StatusTarefaEnum status);

    List<TarefaEntity> findByPrioridadeAndStatus(PrioridadeTarefaEnum prioridade, StatusTarefaEnum status);

    @Query("SELECT t FROM TarefaEntity t WHERE t.dataVencimento < :hoje AND t.status <> 'CONCLUIDA'")
    List<TarefaEntity> buscarTarefasVencidas(@Param("hoje") LocalDate hoje);

    List<TarefaEntity> findByPrioridadeOrderByDataVencimentoAsc(PrioridadeTarefaEnum prioridade);

    @Query(value = "SELECT * FROM tarefas WHERE titulo ILIKE %:termo%", nativeQuery = true)
    List<TarefaEntity> buscarPorTituloContendo(@Param("termo") String termo);

    @Query("SELECT t.prioridade, COUNT(t) FROM TarefaEntity t WHERE t.status = 'PENDENTE' GROUP BY t.prioridade")
    List<Object[]> contarPendentesPorPrioridade();

    @Query("SELECT t.status, COUNT(t) FROM TarefaEntity t WHERE t.dataVencimento >= :inicio AND t.dataVencimento < :fim GROUP BY t.status")
    List<Object[]> buscarResumoSemanal(@Param("inicio") LocalDate inicio, @Param("fim") LocalDate fim);
}
