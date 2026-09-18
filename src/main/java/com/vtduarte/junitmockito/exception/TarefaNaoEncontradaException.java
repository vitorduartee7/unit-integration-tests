package com.vtduarte.junitmockito.exception;

public class TarefaNaoEncontradaException extends RuntimeException {
    public TarefaNaoEncontradaException(Long id) {
        super("Tarefa nao encontrada: " + id);
    }
}
