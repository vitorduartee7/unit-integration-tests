package com.vtduarte.unitintegrationtests.controller;

import com.vtduarte.unitintegrationtests.dto.AlterarStatusRequest;
import com.vtduarte.unitintegrationtests.dto.AtualizarTarefaRequest;
import com.vtduarte.unitintegrationtests.dto.CriarTarefaRequest;
import com.vtduarte.unitintegrationtests.model.TarefaEntity;
import com.vtduarte.unitintegrationtests.service.TarefaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tarefas")
@RequiredArgsConstructor
public class TarefaController {

    private final TarefaService service;

    @PostMapping
    public ResponseEntity<TarefaEntity> criarTarefa(@Valid @RequestBody CriarTarefaRequest request) {
        var tarefa = service.criarTarefa(
                request.titulo(),
                request.descricao(),
                request.prioridade(),
                request.dataVencimento());
        return ResponseEntity.status(HttpStatus.CREATED).body(tarefa);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TarefaEntity> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @GetMapping
    public ResponseEntity<List<TarefaEntity>> listarTodas() {
        return ResponseEntity.ok(service.listarTodas());
    }

    @PutMapping("/{id}")
    public ResponseEntity<TarefaEntity> atualizarTarefa(
            @PathVariable Long id,
            @Valid @RequestBody AtualizarTarefaRequest request) {
        var tarefa = service.atualizarTarefa(
                id,
                request.titulo(),
                request.descricao(),
                request.prioridade(),
                request.dataVencimento());
        return ResponseEntity.ok(tarefa);
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<TarefaEntity> alterarStatus(
            @PathVariable Long id,
            @RequestBody AlterarStatusRequest request) {
        var tarefa = service.alterarStatus(id, request.novoStatus());
        return ResponseEntity.ok(tarefa);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        service.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
