package com.vtduarte.unitintegrationtests.handler;

import com.vtduarte.unitintegrationtests.exception.TarefaNaoEncontradaException;
import com.vtduarte.unitintegrationtests.exception.TransicaoStatusInvalidaException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(TarefaNaoEncontradaException.class)
    public ResponseEntity<String> handleNaoEncontrada(TarefaNaoEncontradaException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    @ExceptionHandler(TransicaoStatusInvalidaException.class)
    public ResponseEntity<String> handleTransicaoStatusInvalida(TransicaoStatusInvalidaException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
    }
}
