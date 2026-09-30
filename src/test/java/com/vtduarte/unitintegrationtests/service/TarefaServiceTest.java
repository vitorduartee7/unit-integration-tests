package com.vtduarte.unitintegrationtests.service;

import com.vtduarte.unitintegrationtests.exception.TarefaNaoEncontradaException;
import com.vtduarte.unitintegrationtests.exception.TransicaoStatusInvalidaException;
import com.vtduarte.unitintegrationtests.model.PrioridadeTarefaEnum;
import com.vtduarte.unitintegrationtests.model.StatusTarefaEnum;
import com.vtduarte.unitintegrationtests.model.TarefaEntity;
import com.vtduarte.unitintegrationtests.repository.TarefaRepository;
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
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TarefaServiceTest {

    @Mock
    TarefaRepository repository;

    @InjectMocks
    TarefaService service;

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

            when(repository.save(any()))
                    .thenAnswer(inv -> inv.getArgument(0));

            var tarefa = service.criarTarefa(titulo, descricao, prioridade, dataVencimento);

            assertEquals(titulo, tarefa.getTitulo());
            verify(repository).save(tarefa);
        }

        @Test
        @DisplayName("Deve criar e salvar tarefa com campos corretos")
        void deveCriarTarefaComCamposCorretos() {
            String titulo = "Hidrate-se";
            String descricao = "Beba Agua";
            PrioridadeTarefaEnum prioridade = PrioridadeTarefaEnum.ALTA;
            LocalDate dataVencimento = LocalDate.now().plusDays(1);

            when(repository.save(any()))
                    .thenAnswer(inv -> inv.getArgument(0));

            service.criarTarefa(titulo, descricao, prioridade, dataVencimento);

            verify(repository).save(tarefaCaptor.capture());

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

            when(repository.save(any()))
                    .thenAnswer(inv -> inv.getArgument(0));

            var tarefa = service.criarTarefa(titulo, descricao, prioridade, null);

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
                    () -> service.criarTarefa(titulo, descricao, prioridade, dataVencimento));
        }

        @Test
        @DisplayName("Deve lancar exception quando validacao falha")
        void deveLancarExceptionQuandoValidacaoFalha() {
            String titulo = "";
            String descricao = "Beba Agua";
            PrioridadeTarefaEnum prioridade = PrioridadeTarefaEnum.ALTA;
            LocalDate dataVencimento = LocalDate.now().plusDays(1);

            assertThrows(IllegalArgumentException.class,
                    () -> service.criarTarefa(titulo, descricao, prioridade, dataVencimento));
            verify(repository, never()).save(any());
        }

        @ParameterizedTest
        @EnumSource(PrioridadeTarefaEnum.class)
        @DisplayName("Deve criar tarefa para qualquer prioridade")
        void deveCriarTarefaParaQualquerPrioridade(PrioridadeTarefaEnum prioridade) {
            String titulo = "Hidratar-se";
            String descricao = "Beba Agua";
            LocalDate dataVencimento = LocalDate.now().plusDays(1);

            when(repository.save(any()))
                    .thenAnswer(inv -> inv.getArgument(0));

            var tarefa = service.criarTarefa(titulo, descricao, prioridade, dataVencimento);

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
                    () -> service.criarTarefa(titulo, descricao, prioridade, dataVencimento));
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

            when(repository.findById(1L))
                    .thenReturn(Optional.of(tarefa));

            var tarefaBuscada = service.buscarPorId(1L);

            assertEquals(tarefa, tarefaBuscada);
            verify(repository).findById(1L);
        }

        @Test
        @DisplayName("Deve lancar exception quando tarefa nao for encontrada")
        void deveLancarExceptionQuandoTarefaNaoEncontrada() {
            when(repository.findById(99L))
                    .thenReturn(Optional.empty());

            assertThrows(TarefaNaoEncontradaException.class,
                    () -> service.buscarPorId(99L));
            verify(repository).findById(99L);
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

            when(repository.findById(1L))
                    .thenReturn(Optional.of(tarefa));
            when(repository.save(any()))
                    .thenReturn(tarefa);

            service.marcarComoConcluida(1L);

            verify(repository).findById(1L);
            verify(repository).save(tarefaCaptor.capture());

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

                when(repository.findById(1L))
                        .thenReturn(Optional.of(tarefa));

                assertThrows(TransicaoStatusInvalidaException.class,
                        () -> service.marcarComoConcluida(1L));

                verify(repository).findById(1L);
                verify(repository, never()).save(any());
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

            when(repository.findById(1L))
                    .thenReturn(Optional.of(tarefa));
            when(repository.save(any()))
                    .thenReturn(tarefa);

            service.atualizarPrioridade(tarefa.getId(), PrioridadeTarefaEnum.MEDIA);

            assertEquals(PrioridadeTarefaEnum.MEDIA, tarefa.getPrioridade());
            verify(repository).findById(1L);
            verify(repository).save(tarefa);
        }

        @Test
        @DisplayName("Deve propagar exception quando tarefa nao encontrada")
        void devePropagarExceptionQuandoTarefaNaoEncontrada() {
            when(repository.findById(1L))
                    .thenReturn(Optional.empty());

            assertThrows(TarefaNaoEncontradaException.class,
                    () -> service.atualizarPrioridade(1L, PrioridadeTarefaEnum.MEDIA));
            verify(repository, never()).save(any());
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

            when(repository.findById(1L))
                    .thenReturn(Optional.of(tarefa));
            when(repository.save(any()))
                    .thenThrow(new RuntimeException());

            assertThrows(RuntimeException.class,
                    () -> service.atualizarPrioridade(1L, PrioridadeTarefaEnum.MEDIA));
            verify(repository).findById(1L);
            verify(repository).save(tarefa);
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

            when(repository.findById(1L))
                    .thenReturn(Optional.of(tarefa));
            when(repository.save(any()))
                    .thenReturn(tarefa);

            service.alterarStatus(1L, novoStatus);

            assertEquals(novoStatus, tarefa.getStatus());
            verify(repository).save(tarefa);
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

            when(repository.findById(1L))
                    .thenReturn(Optional.of(tarefa));

            assertThrows(TransicaoStatusInvalidaException.class,
                    () -> service.alterarStatus(1L, novoStatus));
            verify(repository, never()).save(any());
        }

        @Test
        @DisplayName("Deve lançar exception ao atualizar status de tarefa inexistente")
        void deveLancarExceptionAoAtualizarStatusTarefaInexistente() {
            when(repository.findById(99L))
                    .thenReturn(Optional.empty());

            assertThrows(TarefaNaoEncontradaException.class,
                    () -> service.alterarStatus(99L, StatusTarefaEnum.EM_ANDAMENTO));
            verify(repository, never()).save(any());
        }

        @Test
        @DisplayName("Deve preencher concluidaEm ao alterar status para CONCLUIDA")
        void devePreencherConcluidaEmAoConcluirViaAlterarStatus() {
            var tarefa = new TarefaEntity("Hidrate-se", "Beba agua",
                    PrioridadeTarefaEnum.ALTA, LocalDate.now().plusDays(1));
            tarefa.setId(1L);
            tarefa.setStatus(StatusTarefaEnum.EM_ANDAMENTO);

            when(repository.findById(1L)).thenReturn(Optional.of(tarefa));
            when(repository.save(any())).thenReturn(tarefa);

            service.alterarStatus(1L, StatusTarefaEnum.CONCLUIDA);

            assertNotNull(tarefa.getConcluidaEm());
        }

        @Test
        @DisplayName("Deve limpar concluidaEm ao reabrir tarefa concluida")
        void deveLimparConcluidaEmAoReabrirTarefa() {
            var tarefa = new TarefaEntity("Hidrate-se", "Beba agua",
                    PrioridadeTarefaEnum.ALTA, LocalDate.now().plusDays(1));
            tarefa.setId(1L);
            tarefa.setStatus(StatusTarefaEnum.CONCLUIDA);
            tarefa.setConcluidaEm(LocalDateTime.now());

            when(repository.findById(1L)).thenReturn(Optional.of(tarefa));
            when(repository.save(any())).thenReturn(tarefa);

            service.alterarStatus(1L, StatusTarefaEnum.EM_ANDAMENTO);

            assertNull(tarefa.getConcluidaEm());
        }
    }

    @Nested
    @DisplayName("Testes do método excluir")
    class Excluir {

        @Test
        @DisplayName("Deve excluir tarefa existente")
        void deveExcluirTarefaExistente() {
            when(repository.existsById(1L))
                    .thenReturn(true);

            service.excluir(1L);

            verify(repository).deleteById(1L);
        }

        @Test
        @DisplayName("Deve lancar exception ao excluir tarefa inexistente")
        void deveLancarExceptionAoExcluirTarefaInexistente() {
            when(repository.existsById(1L))
                    .thenReturn(false);

            assertThrows(TarefaNaoEncontradaException.class,
                    () -> service.excluir(1L));

            verify(repository, never())
                    .deleteById(1L);
        }

        @Test
        @DisplayName("Deve propagar exception quando repository falha ao verificar se a tarefa existe")
        void devePropagarExceptionQuandoRepositoryFalhaAoVerificarExistencia() {
            var excecaoDoBanco = new RuntimeException("Erro no banco");
            when(repository.existsById(1L))
                    .thenThrow(excecaoDoBanco);

            var capturada = assertThrows(RuntimeException.class,
                    () -> service.excluir(1L));
            assertSame(excecaoDoBanco, capturada);

            verify(repository).existsById(1L);
            verify(repository, never()).deleteById(1L);
        }

        @Test
        @DisplayName("Deve propagar exception quando repository falha ao excluir")
        void devePropagarExceptionQuandoRepositoryFalhaAoExcluir() {
            var excecaoDoBanco = new RuntimeException("Erro no banco");
            when(repository.existsById(1L))
                    .thenReturn(true);
            doThrow(excecaoDoBanco)
                    .when(repository).deleteById(1L);

            var capturada = assertThrows(RuntimeException.class,
                    () -> service.excluir(1L));
            assertSame(excecaoDoBanco, capturada);

            verify(repository).existsById(1L);
            verify(repository).deleteById(1L);
        }
    }
}