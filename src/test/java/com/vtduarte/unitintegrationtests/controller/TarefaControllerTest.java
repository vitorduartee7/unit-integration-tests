package com.vtduarte.unitintegrationtests.controller;

import com.vtduarte.unitintegrationtests.dto.AlterarStatusRequest;
import com.vtduarte.unitintegrationtests.dto.AtualizarTarefaRequest;
import com.vtduarte.unitintegrationtests.dto.CriarTarefaRequest;
import com.vtduarte.unitintegrationtests.exception.TarefaNaoEncontradaException;
import com.vtduarte.unitintegrationtests.exception.TransicaoStatusInvalidaException;
import com.vtduarte.unitintegrationtests.model.PrioridadeTarefaEnum;
import com.vtduarte.unitintegrationtests.model.StatusTarefaEnum;
import com.vtduarte.unitintegrationtests.model.TarefaEntity;
import com.vtduarte.unitintegrationtests.service.TarefaService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDate;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TarefaController.class)
class TarefaControllerTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TarefaService service;

    @Nested
    @DisplayName("Testes do método criar tarefa do TarefaController")
    class CriarTarefa {

        @Test
        @DisplayName("Deve criar tarefa com sucesso")
        void deveCriarTarefaComSucesso() throws Exception {
            var request = new CriarTarefaRequest(
                    "Tarefa",
                    "Tarefa",
                    PrioridadeTarefaEnum.ALTA,
                    LocalDate.now().plusDays(1));
            var tarefa = new TarefaEntity(
                    "Tarefa",
                    "Tarefa",
                    PrioridadeTarefaEnum.ALTA,
                    LocalDate.now().plusDays(1));
            when(service.criarTarefa(
                    request.titulo(),
                    request.descricao(),
                    request.prioridade(),
                    request.dataVencimento()))
                    .thenReturn(tarefa);

            mockMvc.perform(post("/tarefas")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.titulo").value("Tarefa"));
        }

        @Test
        @DisplayName("Deve retornar 400 quando o título for vazio")
        void deveRetornar400QuandoTituloVazio() throws Exception {
            var request = new CriarTarefaRequest("", "Tarefa", PrioridadeTarefaEnum.ALTA, LocalDate.now().plusDays(1));

            mockMvc.perform(post("/tarefas")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());
            verifyNoInteractions(service);
        }

        @Test
        @DisplayName("Deve retornar 400 quando data de vencimento estiver no passado")
        void deveRetornar400QuandoDataVencimentoNoPassado() throws Exception {
            var request = new CriarTarefaRequest("Tarefa", "Tarefa", PrioridadeTarefaEnum.ALTA, LocalDate.now().minusDays(1));

            mockMvc.perform(post("/tarefas")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());
            verifyNoInteractions(service);
        }
    }

    @Nested
    @DisplayName("Testes do método buscar por id no controller")
    class BuscarPorID {

        @Test
        @DisplayName("Deve buscar tarefa por id")
        void deveBuscarTarefaPorId() throws Exception {
            var tarefa = new TarefaEntity(
                    "Estudar",
                    "Estudar por 1h",
                    PrioridadeTarefaEnum.ALTA,
                    LocalDate.now().plusDays(1));
            tarefa.setId(1L);
            when(service.buscarPorId(1L))
                    .thenReturn(tarefa);

            mockMvc.perform(get("/tarefas/1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.titulo").value("Estudar"))
                    .andExpect(jsonPath("$.status").value("PENDENTE"));
        }

        @Test
        @DisplayName("Deve retornar 404 quando não encontrar tarefa")
        void deveRetornar404QuandoTarefaNaoEncontrada() throws Exception {
            when(service.buscarPorId(99L))
                    .thenThrow(TarefaNaoEncontradaException.class);

            mockMvc.perform(get("/tarefas/99"))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("Testes do método listar todas no controller")
    class ListarTodas {

        @Test
        @DisplayName("Deve listar todas as tarefas")
        void deveListarTodasTarefas() throws Exception {
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
            var lista = List.of(tarefa1, tarefa2);
            when(service.listarTodas())
                    .thenReturn(lista);

            mockMvc.perform(get("/tarefas"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(2)));
        }

        @Test
        @DisplayName("Deve retornar uma lista vazia")
        void deveRetornarListaVazia() throws Exception {
            when(service.listarTodas())
                    .thenReturn(List.of());

            mockMvc.perform(get("/tarefas"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(0)));
        }
    }

    @Nested
    @DisplayName("Testes do método atualizar tarefa no controller")
    class AtualizarTarefa {

        @Test
        @DisplayName("Deve atualizar tarefa")
        void deveAtualizarTarefa() throws Exception {
            var tarefa = new TarefaEntity(
                    "Tarefa",
                    "Tarefa",
                    PrioridadeTarefaEnum.ALTA,
                    LocalDate.now().plusDays(1));
            tarefa.setId(1L);
            var request = new AtualizarTarefaRequest(
                    "Task",
                    "Task",
                    PrioridadeTarefaEnum.MEDIA,
                    LocalDate.now().plusDays(2));
            tarefa.setTitulo(request.titulo());
            tarefa.setDescricao(request.descricao());
            tarefa.setPrioridade(request.prioridade());
            tarefa.setDataVencimento(request.dataVencimento());
            when(service.atualizarTarefa(
                    1L,
                    request.titulo(),
                    request.descricao(),
                    request.prioridade(),
                    request.dataVencimento())
            )
                    .thenReturn(tarefa);

            mockMvc.perform(put("/tarefas/1")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.titulo").value("Task"));
        }
    }

    @Nested
    @DisplayName("Testes do método atualizar status no controller")
    class AtualizarStatus {

        @Test
        @DisplayName("Deve retornar 200 ao alterar status com sucesso")
        void deveRetornar200AoAlterarStatusComSucesso() throws Exception {
            var request = new AlterarStatusRequest(StatusTarefaEnum.EM_ANDAMENTO);
            var tarefa = new TarefaEntity(
                    "Tarefa",
                    "Tarefa",
                    PrioridadeTarefaEnum.ALTA,
                    LocalDate.now().plusDays(1));
            tarefa.setStatus(StatusTarefaEnum.EM_ANDAMENTO);
            when(service.alterarStatus(1L, request.novoStatus()))
                    .thenReturn(tarefa);

            mockMvc.perform(patch("/tarefas/1/status")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").value("EM_ANDAMENTO"));
        }

        @Test
        @DisplayName("Deve retornar 409 ao tentar transição invalida de status")
        void deveRetornar409AoTentarTransicaoInvalidaDeStatus() throws Exception {
            var request = new AlterarStatusRequest(StatusTarefaEnum.CONCLUIDA);
            when(service.alterarStatus(99L, request.novoStatus()))
                    .thenThrow(new TransicaoStatusInvalidaException("Nao e possivel fazer essa transicao"));

            mockMvc.perform(patch("/tarefas/99/status")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isConflict());
        }
    }

    @Nested
    @DisplayName("Testes do método excluir no controller")
    class ExcluirTarefa {

        @Test
        @DisplayName("Deve retornar 204 ao excluir com sucesso")
        void deveRetornar204aoExcluirComSucesso() throws Exception {
            doNothing().when(service).excluir(1L);

            mockMvc.perform(delete("/tarefas/1"))
                    .andExpect(status().isNoContent());
        }

        @Test
        @DisplayName("Deve retornar 404 ao excluir tarefa inexistente")
        void deveRetornar404aoExcluirTarefaInexistente() throws Exception {
            doThrow(new TarefaNaoEncontradaException(99L)).when(service).excluir(99L);

            mockMvc.perform(delete("/tarefas/99"))
                    .andExpect(status().isNotFound());
        }
    }
}