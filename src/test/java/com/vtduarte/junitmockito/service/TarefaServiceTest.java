package com.vtduarte.junitmockito.service;

import com.vtduarte.junitmockito.exception.TarefaNaoEncontradaException;
import com.vtduarte.junitmockito.model.PrioridadeTarefaEnum;
import com.vtduarte.junitmockito.model.StatusTarefaEnum;
import com.vtduarte.junitmockito.model.TarefaEntity;
import com.vtduarte.junitmockito.repository.TarefaRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.NullSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TarefaServiceTest {

    @Mock
    TarefaRepository tarefaRepository;

    @InjectMocks
    TarefaService tarefaService;

    @Captor
    private ArgumentCaptor<TarefaEntity> tarefaCaptor;

    @Nested
    @DisplayName("Testes do método criar tarefa")
    class CriarTarefa {

        @Test
        @DisplayName("Deve criar e salvar tarefa com dados validos")
        void deveCriarTarefaComDadosValidos() {
            String titulo = "Hidrate-se";
            String descricao = "Beba Agua";
            PrioridadeTarefaEnum prioridade = PrioridadeTarefaEnum.ALTA;
            LocalDate dataVencimento = LocalDate.now().plusDays(1);

            when(tarefaRepository.salvar(any()))
                    .thenAnswer(inv -> inv.getArgument(0));

            var tarefa = tarefaService.criar(titulo, descricao, prioridade, dataVencimento);

            assertEquals(titulo, tarefa.getTitulo());
            verify(tarefaRepository).salvar(tarefa);
        }

        @Test
        @DisplayName("Deve criar e salvar tarefa com campos corretos")
        void deveCriarTarefaComCamposCorretos() {
            String titulo = "Hidrate-se";
            String descricao = "Beba Agua";
            PrioridadeTarefaEnum prioridade = PrioridadeTarefaEnum.ALTA;
            LocalDate dataVencimento = LocalDate.now().plusDays(1);

            when(tarefaRepository.salvar(any()))
                    .thenAnswer(inv -> inv.getArgument(0));

            tarefaService.criar(titulo, descricao, prioridade, dataVencimento);

            verify(tarefaRepository).salvar(tarefaCaptor.capture());

            var tarefaSalva = tarefaCaptor.getValue();
            assertEquals(titulo, tarefaSalva.getTitulo());
            assertEquals(descricao, tarefaSalva.getDescricao());
            assertEquals(prioridade, tarefaSalva.getPrioridade());
            assertEquals(StatusTarefaEnum.PENDENTE, tarefaSalva.getStatus());
            assertEquals(dataVencimento, tarefaSalva.getDataVencimento());
        }

        @Test
        @DisplayName("Deve criar tarefa com data de vencimento nula")
        void deveCriarTarefaComDataVencimentoNula() {
            String titulo = "Hidrate-se";
            String descricao = "Beba Agua";
            PrioridadeTarefaEnum prioridade = PrioridadeTarefaEnum.ALTA;

            when(tarefaRepository.salvar(any()))
                    .thenAnswer(inv -> inv.getArgument(0));

            var tarefa = tarefaService.criar(titulo, descricao, prioridade, null);

            assertNull(tarefa.getDataVencimento());
            assertEquals(StatusTarefaEnum.PENDENTE, tarefa.getStatus());
        }

        @Test
        @DisplayName("Deve lancar exception quando a data estiver no passado")
        void deveLancarExceptionQuandoDataEstiverNoPassado() {
            String titulo = "Hidrate-se";
            String descricao = "Beba Agua";
            PrioridadeTarefaEnum prioridade = PrioridadeTarefaEnum.ALTA;
            LocalDate dataVencimento = LocalDate.now().minusDays(1);

            assertThrows(IllegalArgumentException.class,
                    () -> tarefaService.criar(titulo, descricao, prioridade, dataVencimento));
        }

        @Test
        @DisplayName("Deve lancar exception quando validacao falha")
        void deveLancarExceptionQuandoValidacaoFalha() {
            String titulo = "";
            String descricao = "Beba Agua";
            PrioridadeTarefaEnum prioridade = PrioridadeTarefaEnum.ALTA;
            LocalDate dataVencimento = LocalDate.now().plusDays(1);

            assertThrows(IllegalArgumentException.class,
                    () -> tarefaService.criar(titulo, descricao, prioridade, dataVencimento));
            verify(tarefaRepository, never()).salvar(any());
        }

        @ParameterizedTest
        @EnumSource(PrioridadeTarefaEnum.class)
        @DisplayName("Deve criar tarefa para qualquer prioridade")
        void deveCriarTarefaParaQualquerPrioridade(PrioridadeTarefaEnum prioridade) {
            String titulo = "Hidratar-se";
            String descricao = "Beba Agua";
            LocalDate dataVencimento = LocalDate.now().plusDays(1);

            when(tarefaRepository.salvar(any()))
                    .thenAnswer(inv -> inv.getArgument(0));

            var tarefa = tarefaService.criar(titulo, descricao, prioridade, dataVencimento);

            assertEquals(prioridade, tarefa.getPrioridade());
        }

        @ParameterizedTest
        @NullSource
        @CsvSource({"''", "' '"})
        @DisplayName("Deve lançar exception para títulos inváilidos")
        void deveLancarExceptionParaTitulosInvalidos(String titulo) {
            String descricao = "Beba Agua";
            LocalDate dataVencimento = LocalDate.now().plusDays(1);
            PrioridadeTarefaEnum prioridade = PrioridadeTarefaEnum.ALTA;

            assertThrows(IllegalArgumentException.class,
                    () -> tarefaService.criar(titulo, descricao, prioridade, dataVencimento));
        }
    }

    @Nested
    @DisplayName("Testes do método buscar por id")
    class BuscarPorId {

        @Test
        @DisplayName("Deve retornar tarefa quando encontrada")
        void deveRetornarTarefaQuandoEncontrada() {
            var tarefa = new TarefaEntity(
                    "Tarefa",
                    "Tarefa",
                    PrioridadeTarefaEnum.ALTA,
                    LocalDate.now().plusDays(1));
            tarefa.setId(1L);

            when(tarefaRepository.buscarPorId(1L))
                    .thenReturn(Optional.of(tarefa));

            var tarefaBuscada = tarefaService.buscarPorId(1L);

            assertEquals(tarefa, tarefaBuscada);
            verify(tarefaRepository).buscarPorId(1L);
        }

        @Test
        @DisplayName("Deve lancar exception quando tarefa nao for encontrada")
        void deveLancarExceptionQuandoTarefaNaoEncontrada() {
            when(tarefaRepository.buscarPorId(99L))
                    .thenReturn(Optional.empty());

            assertThrows(TarefaNaoEncontradaException.class,
                    () -> tarefaService.buscarPorId(99L));
            verify(tarefaRepository).buscarPorId(99L);
        }
    }

    @Nested
    @DisplayName("Testes do método listar pendentes")
    class ListarPendentes {

        @Test
        @DisplayName("Deve retornar uma lista de tarefas pendentes")
        void deveListarTarefasPendentes() {
            var tarefa1 = new TarefaEntity(
                    "Tarefa1",
                    "Tarefa1",
                    PrioridadeTarefaEnum.ALTA,
                    LocalDate.now().plusDays(1));
            var tarefa2 = new TarefaEntity(
                    "Tarefa2",
                    "Tarefa2",
                    PrioridadeTarefaEnum.ALTA,
                    LocalDate.now().plusDays(1));
            List<TarefaEntity> listaComDuasTarefas = List.of(tarefa1, tarefa2);

            when(tarefaRepository.listarPorStatus(StatusTarefaEnum.PENDENTE))
                    .thenReturn(listaComDuasTarefas);

            var lista = tarefaService.listarPendentes();

            assertEquals(listaComDuasTarefas, lista);
        }

        @Test
        @DisplayName("Deve retornar uma lista vazia quando nao tiver tarefa pendente")
        void deveRetornarListaVaziaQuandoNaoTiverTarefaPendente() {
            List<TarefaEntity> listaVazia = List.of();
            when(tarefaRepository.listarPorStatus(StatusTarefaEnum.PENDENTE))
                    .thenReturn(listaVazia);

            var lista = tarefaService.listarPendentes();

            assertTrue(lista.isEmpty());
        }
    }

    @Nested
    @DisplayName("Testes do método marcar como concluida")
    class MarcarComoConcluida {

        @Test
        @DisplayName("Deve marcar tarefa como concluida")
        void deveMarcarTarefaComoConcluida() {
            var tarefa = new TarefaEntity(
                    "Hidrate-se",
                    "Beba agua",
                    PrioridadeTarefaEnum.ALTA,
                    LocalDate.now().plusDays(1));
            tarefa.setId(1L);
            tarefa.setStatus(StatusTarefaEnum.EM_ANDAMENTO);

            when(tarefaRepository.buscarPorId(1L))
                    .thenReturn(Optional.of(tarefa));
            when(tarefaRepository.salvar(any()))
                    .thenReturn(tarefa);

            tarefaService.marcarComoConcluida(1L);

            verify(tarefaRepository).buscarPorId(1L);
            verify(tarefaRepository).salvar(tarefaCaptor.capture());

            var tarefaSalva = tarefaCaptor.getValue();
            assertNotNull(tarefaSalva.getConcluidaEm());
            assertEquals(StatusTarefaEnum.CONCLUIDA, tarefaSalva.getStatus());
        }

            @Test
            @DisplayName("Deve lançar exception quando transição inválida")
            void deveLancarExceptionQuandoTransicaoInvalida() {
                var tarefa = new TarefaEntity(
                        "Hidrate-se",
                        "Beba agua",
                        PrioridadeTarefaEnum.ALTA,
                        LocalDate.now().plusDays(1));
                tarefa.setId(1L);

                when(tarefaRepository.buscarPorId(1L))
                        .thenReturn(Optional.of(tarefa));

                assertThrows(IllegalArgumentException.class,
                        () -> tarefaService.marcarComoConcluida(1L));

                verify(tarefaRepository).buscarPorId(1L);
                verify(tarefaRepository, never()).salvar(any());
            }
    }

    @Nested
    @DisplayName("Testes do método atualizar prioridade")
    class AtualizarPrioridade {

        @Test
        @DisplayName("Deve atualizar prioridade da tarefa")
        void deveAtualizarPrioridadeDaTarefa() {
            var tarefa = new TarefaEntity(
                    "Tarefa",
                    "Tarefa",
                    PrioridadeTarefaEnum.ALTA,
                    LocalDate.now().plusDays(1));
            tarefa.setId(1L);

            when(tarefaRepository.buscarPorId(1L))
                    .thenReturn(Optional.of(tarefa));
            when(tarefaRepository.salvar(any()))
                    .thenReturn(tarefa);

            tarefaService.atualizarPrioridade(tarefa.getId(), PrioridadeTarefaEnum.MEDIA);

            assertEquals(PrioridadeTarefaEnum.MEDIA, tarefa.getPrioridade());
            verify(tarefaRepository).buscarPorId(1L);
            verify(tarefaRepository).salvar(tarefa);
        }

        @Test
        @DisplayName("Deve propagar exception quando tarefa nao encontrada")
        void devePropagarExceptionQuandoTarefaNaoEncontrada() {
            when(tarefaRepository.buscarPorId(1L))
                    .thenReturn(Optional.empty());

            assertThrows(TarefaNaoEncontradaException.class,
                    () -> tarefaService.atualizarPrioridade(1L, PrioridadeTarefaEnum.MEDIA));
            verify(tarefaRepository, never()).salvar(any());
        }

        @Test
        @DisplayName("Deve propagar exception quando repository falha ao salvar")
        void devePropagarExceptionQuandoRepositoryFalhaAoSalvar() {
            var tarefa = new TarefaEntity(
                    "Tarefa",
                    "Tarefa",
                    PrioridadeTarefaEnum.ALTA,
                    LocalDate.now().plusDays(1));
            tarefa.setId(1L);

            when(tarefaRepository.buscarPorId(1L))
                    .thenReturn(Optional.of(tarefa));
            when(tarefaRepository.salvar(any()))
                    .thenThrow(new RuntimeException());

            assertThrows(RuntimeException.class,
                    () -> tarefaService.atualizarPrioridade(1L, PrioridadeTarefaEnum.MEDIA));
            verify(tarefaRepository).buscarPorId(1L);
            verify(tarefaRepository).salvar(tarefa);
        }
    }

    @Nested
    @DisplayName("Testes do método atualizar status")
    class AtualizarStatus {

        @ParameterizedTest
        @CsvSource({
                "PENDENTE, EM_ANDAMENTO",
                "EM_ANDAMENTO, PENDENTE",
                "EM_ANDAMENTO, CONCLUIDA",
                "CONCLUIDA, EM_ANDAMENTO"
        })
        @DisplayName("Deve atualizar status quando a transicao for valida")
        void deveAtualizarStatusQuandoTransicaoValida(StatusTarefaEnum statusAtual, StatusTarefaEnum novoStatus) {
            var tarefa = new TarefaEntity("Hidrate-se",
                    "Beba Agua",
                    PrioridadeTarefaEnum.ALTA,
                    LocalDate.now().plusDays(1));
            tarefa.setStatus(statusAtual);
            tarefa.setId(1L);

            when(tarefaRepository.buscarPorId(1L))
                    .thenReturn(Optional.of(tarefa));
            when(tarefaRepository.salvar(any()))
                    .thenReturn(tarefa);

            tarefaService.atualizarStatus(1L, novoStatus);

            assertEquals(novoStatus, tarefa.getStatus());
            verify(tarefaRepository).salvar(tarefa);
        }

        @ParameterizedTest
        @CsvSource({
                "PENDENTE, PENDENTE",
                "PENDENTE, CONCLUIDA",
                "EM_ANDAMENTO, EM_ANDAMENTO",
                "CONCLUIDA, CONCLUIDA",
                "CONCLUIDA, PENDENTE"
        })
        @DisplayName("Deve lancar exception para transicao invalida de status")
        void deveLancarExceptionParaTransicaoInvalidaDeStatus(StatusTarefaEnum statusAtual, StatusTarefaEnum novoStatus) {
            var tarefa = new TarefaEntity("Hidrate-se",
                    "Beba Agua",
                    PrioridadeTarefaEnum.ALTA,
                    LocalDate.now().plusDays(1));
            tarefa.setStatus(statusAtual);
            tarefa.setId(1L);

            when(tarefaRepository.buscarPorId(1L))
                    .thenReturn(Optional.of(tarefa));

            assertThrows(IllegalArgumentException.class,
                    () -> tarefaService.atualizarStatus(1L, novoStatus));
            verify(tarefaRepository, never()).salvar(any());
        }

        @Test
        @DisplayName("Deve lançar exception ao atualizar status de tarefa inexistente")
        void deveLancarExceptionAoAtualizarStatusTarefaInexistente() {
            when(tarefaRepository.buscarPorId(99L))
                    .thenReturn(Optional.empty());

            assertThrows(TarefaNaoEncontradaException.class,
                    () -> tarefaService.atualizarStatus(99L, StatusTarefaEnum.EM_ANDAMENTO));
            verify(tarefaRepository, never()).salvar(any());
        }
    }

    @Nested
    @DisplayName("Testes do método excluir")
    class Excluir {

        @Test
        @DisplayName("Deve excluir tarefa existente")
        void deveExcluirTarefaExistente() {
            var tarefa = new TarefaEntity(
                    "Tarefa",
                    "Tarefa",
                    PrioridadeTarefaEnum.ALTA,
                    LocalDate.now().plusDays(1));
            tarefa.setId(1L);

            when(tarefaRepository.buscarPorId(1L))
                    .thenReturn(Optional.of(tarefa));

            tarefaService.excluir(1L);

            verify(tarefaRepository).excluir(1L);
        }

        @Test
        @DisplayName("Deve lancar exception ao excluir tarefa inexistente")
        void deveLancarExceptionAoExcluirTarefaInexistente() {
            when(tarefaRepository.buscarPorId(99L))
                    .thenReturn(Optional.empty());

            assertThrows(TarefaNaoEncontradaException.class,
                    () -> tarefaService.excluir(99L));

            verify(tarefaRepository, never()).excluir(anyLong());
        }

        @Test
        @DisplayName("Deve propagar exception quando repository falha ao buscar")
        void devePropagarExceptionQuandoRepositoryFalhaAoBuscar() {
            when(tarefaRepository.buscarPorId(99L))
                    .thenThrow(new RuntimeException());

            assertThrows(RuntimeException.class,
                    () -> tarefaService.excluir(99L));

            verify(tarefaRepository).buscarPorId(99L);
        }

        @Test
        @DisplayName("Deve propagar exception quando repository falha ao excluir")
        void devePropagarExceptionQuandoRepositoryFalhaAoExcluir() {
            var tarefa = new TarefaEntity(
                    "Tarefa",
                    "Tarefa",
                    PrioridadeTarefaEnum.ALTA,
                    LocalDate.now().plusDays(1));
            tarefa.setId(1L);

            when(tarefaRepository.buscarPorId(1L))
                    .thenReturn(Optional.of(tarefa));
            doThrow(new RuntimeException("Falha ao excluir"))
                    .when(tarefaRepository).excluir(1L);

            assertThrows(RuntimeException.class,
                    () -> tarefaService.excluir(1L));

            verify(tarefaRepository).buscarPorId(1L);
            verify(tarefaRepository).excluir(1L);
        }
    }
}