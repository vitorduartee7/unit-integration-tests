package com.vtduarte.unitintegrationtests.exception;

public class TarefaNaoEncontradaException extends RuntimeException {
    public TarefaNaoEncontradaException(Long id) {
        super("Tarefa nao encontrada: " + id);
    }
}
