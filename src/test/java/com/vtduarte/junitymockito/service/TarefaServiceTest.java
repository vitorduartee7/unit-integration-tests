package com.vtduarte.junitymockito.service;

import com.vtduarte.junitymockito.model.PrioridadeTarefaEnum;
import com.vtduarte.junitymockito.model.StatusTarefaEnum;
import org.junit.jupiter.api.*;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class TarefaServiceTest {

    TarefaService tarefaService;

    @BeforeEach
    void setUp() {
        tarefaService = new TarefaService();
    }

    @Nested
    class CriarTarefa {

        @Test
        @DisplayName("Deve criar tarefa com dados validos")
        void deveCriarTarefaComDadosValidos() {
            String titulo = "Hidratar-se";
            String descricao = "Beba Agua";
            PrioridadeTarefaEnum prioridade = PrioridadeTarefaEnum.ALTA;
            LocalDate dataVencimento = LocalDate.now().plusDays(1);

            var tarefa = tarefaService.criar(titulo, descricao, prioridade, dataVencimento);

            assertEquals(titulo, tarefa.getTitulo());
            Assertions.assertEquals(StatusTarefaEnum.PENDENTE, tarefa.getStatus());
        }

        @Test
        @DisplayName("Deve lancar exceṕtion quando o titulo for vazio")
        void deveLancarExcetionQuandoTituloVazio() {
            String titulo = "";
            String descricao = "Beba Agua";
            PrioridadeTarefaEnum prioridade = PrioridadeTarefaEnum.ALTA;
            LocalDate dataVencimento = LocalDate.now().plusDays(1);

            assertThrows(IllegalArgumentException.class, () -> tarefaService.criar(titulo, descricao, prioridade, dataVencimento));
        }

        @Test
        @DisplayName("Deve lancar exception quando a data estiver no passado")
        void deveLancarExceptionQuandoDataEstiverNoPassado() {
            String titulo = "Hidrate-se";
            String descricao = "Beba Agua";
            PrioridadeTarefaEnum prioridade = PrioridadeTarefaEnum.ALTA;
            LocalDate dataVencimento = LocalDate.now().minusDays(1);

            assertThrows(IllegalArgumentException.class, () -> tarefaService.criar(titulo, descricao, prioridade, dataVencimento));
        }

        @Test
        @DisplayName("Deve criar tarefa com data de vencimento nula")
        void deveCriarTarefaComDataVencimentoNula() {
            String titulo = "Hidrate-se";
            String descricao = "Beba Agua";
            PrioridadeTarefaEnum prioridade = PrioridadeTarefaEnum.ALTA;

            var tarefa = tarefaService.criar(titulo, descricao, prioridade, null);

            assertNull(tarefa.getDataVencimento());
            assertEquals(StatusTarefaEnum.PENDENTE, tarefa.getStatus());
        }

    }
}