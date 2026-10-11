package com.vtduarte.unitintegrationtests.repository;

import com.vtduarte.unitintegrationtests.model.PrioridadeTarefaEnum;
import com.vtduarte.unitintegrationtests.model.StatusTarefaEnum;
import com.vtduarte.unitintegrationtests.model.TarefaEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class TarefaRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private TarefaRepository repository;

    private TarefaEntity criarTarefa(
            PrioridadeTarefaEnum prioridade,
            StatusTarefaEnum status,
            LocalDate dataVencimento)
    {
        var tarefa = new TarefaEntity("Tarefa", "Descricao", prioridade, dataVencimento);
        tarefa.setStatus(status);
        return entityManager.persist(tarefa);
    }

    private TarefaEntity criarTarefa(
            String titulo,
            PrioridadeTarefaEnum prioridade,
            StatusTarefaEnum status,
            LocalDate dataVencimento)
    {
        var tarefa = new TarefaEntity(titulo, "Descricao", prioridade, dataVencimento);
        tarefa.setStatus(status);
        return entityManager.persist(tarefa);
    }

    @Test
    @DisplayName("Deve salvar tarefa com id gerado")
    void deveSalvarTarefaComIdGerado() {
        var tarefa = new TarefaEntity(
                "Tarefa",
                "Tarefa",
                PrioridadeTarefaEnum.ALTA,
                LocalDate.now().plusDays(1)
        );

        var tarefaSalva = repository.save(tarefa);

        assertNotNull(tarefaSalva.getId());
    }

    @Test
    @DisplayName("Deve encontrar tarefa por id")
    void deveEncontrarTarefaPorId() {
        LocalDate dataVencimento = LocalDate.now().plusDays(1);
        var tarefa = new TarefaEntity(
                "Tarefa",
                "Tarefa",
                PrioridadeTarefaEnum.ALTA,
                dataVencimento
        );
        entityManager.persistAndFlush(tarefa);
        entityManager.clear();

        Optional<TarefaEntity> resultado = repository.findById(tarefa.getId());

        assertTrue(resultado.isPresent());
        assertEquals("Tarefa", resultado.get().getTitulo());
        assertEquals("Tarefa", resultado.get().getDescricao());
        assertEquals(PrioridadeTarefaEnum.ALTA, resultado.get().getPrioridade());
        assertEquals(StatusTarefaEnum.PENDENTE, resultado.get().getStatus());
        assertNotNull(resultado.get().getCriadaEm());
        assertEquals(dataVencimento, resultado.get().getDataVencimento());
        assertNull(resultado.get().getConcluidaEm());
    }

    @Test
    @DisplayName("Deve retornar vazio quando id nao existe")
    void deveRetornarVazioQuandoIdNaoExiste() {
        var tarefa = new TarefaEntity(
                "Tarefa",
                "Tarefa",
                PrioridadeTarefaEnum.ALTA,
                LocalDate.now().plusDays(1)
        );
        entityManager.persistAndFlush(tarefa);

        var idInexistente = tarefa.getId() + 1;

        Optional<TarefaEntity> resultado = repository.findById(idInexistente);

        assertFalse(resultado.isPresent());
    }

    @Test
    @DisplayName("Deve encontrar tarefas vencidas")
    void deveEncontrarTarefasVencidas() {
        var hoje = LocalDate.now();
        var vencidaPendente = criarTarefa(PrioridadeTarefaEnum.ALTA, StatusTarefaEnum.PENDENTE, hoje.minusDays(1));
        var futura = criarTarefa(PrioridadeTarefaEnum.ALTA, StatusTarefaEnum.PENDENTE, hoje.plusDays(1));
        var vencidaConcluida = criarTarefa(PrioridadeTarefaEnum.ALTA, StatusTarefaEnum.CONCLUIDA, hoje.minusDays(1));
        var hojePendente = criarTarefa(PrioridadeTarefaEnum.ALTA, StatusTarefaEnum.PENDENTE, hoje);

        var resultado = repository.buscarTarefasVencidas(hoje);

        assertEquals(1, resultado.size());
        assertTrue(resultado.contains(vencidaPendente));
        assertFalse(resultado.contains(futura));
        assertFalse(resultado.contains(vencidaConcluida));
        assertFalse(resultado.contains(hojePendente));
    }

    @Test
    @DisplayName("Deve ordenar tarefas por data de vencimento a partir da prioridade")
    void deveOrdenarTarefasPorDataVencimentoAPartirDaPrioridade() {
        var maisCedo = criarTarefa(PrioridadeTarefaEnum.ALTA, StatusTarefaEnum.PENDENTE, LocalDate.now().plusDays(1));
        var maisTarde = criarTarefa(PrioridadeTarefaEnum.ALTA, StatusTarefaEnum.PENDENTE, LocalDate.now().plusDays(7));
        var meio = criarTarefa(PrioridadeTarefaEnum.ALTA, StatusTarefaEnum.PENDENTE, LocalDate.now().plusDays(3));
        criarTarefa(PrioridadeTarefaEnum.MEDIA, StatusTarefaEnum.PENDENTE, LocalDate.now().plusDays(5));

        var resultado = repository.findByPrioridadeOrderByDataVencimentoAsc(PrioridadeTarefaEnum.ALTA);
        var datas = resultado.stream()
                .map(TarefaEntity::getDataVencimento)
                .toList();

        assertEquals(List.of(maisCedo.getDataVencimento(), meio.getDataVencimento(), maisTarde.getDataVencimento()), datas);
    }

    @Test
    @DisplayName("Deve encontrar tarefas com o termo em qualquer posição do título ignorando maiusculas")
    void deveEncontrarTarefasComTermoEmQualquerPosicaoDoTituloIgnorandoMaiusculas() {
        var tarefaJava = criarTarefa("Tarefa Java", PrioridadeTarefaEnum.ALTA, StatusTarefaEnum.PENDENTE, LocalDate.now().plusDays(1));
        var javaTarefa = criarTarefa("Java Tarefa", PrioridadeTarefaEnum.ALTA, StatusTarefaEnum.PENDENTE, LocalDate.now().plusDays(1));
        criarTarefa("Tarefa Python", PrioridadeTarefaEnum.ALTA, StatusTarefaEnum.PENDENTE, LocalDate.now().plusDays(1));

        var resultado = repository.buscarPorTituloContendo("java");

        assertEquals(2, resultado.size());
        assertTrue(resultado.contains(tarefaJava));
        assertTrue(resultado.contains(javaTarefa));
    }

    @Test
    @DisplayName("Deve contar pendentes por prioridade")
    void deveContarPendentesPorPrioridade() {
        criarTarefa(PrioridadeTarefaEnum.ALTA, StatusTarefaEnum.PENDENTE, LocalDate.now().plusDays(1));
        criarTarefa(PrioridadeTarefaEnum.ALTA, StatusTarefaEnum.PENDENTE, LocalDate.now().plusDays(1));
        criarTarefa(PrioridadeTarefaEnum.BAIXA, StatusTarefaEnum.PENDENTE, LocalDate.now().plusDays(1));
        criarTarefa(PrioridadeTarefaEnum.ALTA, StatusTarefaEnum.CONCLUIDA, LocalDate.now().plusDays(1));

        var resultado = repository.contarPendentesPorPrioridade();

        Map<PrioridadeTarefaEnum, Long> contagem = resultado.stream()
                .collect(Collectors.toMap(
                        linha -> (PrioridadeTarefaEnum) linha[0],
                        linha -> (Long) linha[1]));

        assertEquals(2, contagem.size());
        assertEquals(2L, contagem.get(PrioridadeTarefaEnum.ALTA));
        assertEquals(1L, contagem.get(PrioridadeTarefaEnum.BAIXA));
        assertFalse(contagem.containsKey(PrioridadeTarefaEnum.MEDIA));
    }

    @Test
    @DisplayName("Deve buscar tarefa por prioridade e status")
    void deveBuscarPorPrioridadeEStatus() {
        var altaPendente = criarTarefa(PrioridadeTarefaEnum.ALTA, StatusTarefaEnum.PENDENTE, LocalDate.now().plusDays(1));
        criarTarefa(PrioridadeTarefaEnum.ALTA, StatusTarefaEnum.EM_ANDAMENTO, LocalDate.now().plusDays(1));
        criarTarefa(PrioridadeTarefaEnum.BAIXA, StatusTarefaEnum.PENDENTE, LocalDate.now().plusDays(1));

        var resultado = repository.findByPrioridadeAndStatus(PrioridadeTarefaEnum.ALTA, StatusTarefaEnum.PENDENTE);

        assertEquals(1, resultado.size());
        assertTrue(resultado.contains(altaPendente));
    }

        @Test
        @DisplayName("Deve agrupar e contar tarefas no periodo de sete dias")
        void deveAgruparEContarTarefasNoPeriodoDeSeteDias() {
            var hoje = LocalDate.now();
            criarTarefa(PrioridadeTarefaEnum.ALTA, StatusTarefaEnum.PENDENTE, hoje.minusDays(1));
            criarTarefa(PrioridadeTarefaEnum.ALTA, StatusTarefaEnum.CONCLUIDA, hoje);
            criarTarefa(PrioridadeTarefaEnum.ALTA, StatusTarefaEnum.PENDENTE, hoje.plusDays(3));
            criarTarefa(PrioridadeTarefaEnum.ALTA, StatusTarefaEnum.PENDENTE, hoje.plusDays(6));
            criarTarefa(PrioridadeTarefaEnum.ALTA, StatusTarefaEnum.CONCLUIDA, hoje.plusDays(7));
            criarTarefa(PrioridadeTarefaEnum.ALTA, StatusTarefaEnum.EM_ANDAMENTO, null);

            var resultado = repository.buscarResumoSemanal(hoje, hoje.plusDays(7));

            Map<StatusTarefaEnum, Long> contagem = resultado.stream()
                    .collect(Collectors.toMap(
                            linha -> (StatusTarefaEnum) linha[0],
                            linha -> (Long) linha[1]));

            assertEquals(2, contagem.size());
            assertEquals(2L, contagem.get(StatusTarefaEnum.PENDENTE));
            assertEquals(1L, contagem.get(StatusTarefaEnum.CONCLUIDA));
            assertFalse(contagem.containsKey(StatusTarefaEnum.EM_ANDAMENTO));
        }
}