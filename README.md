# Java Testing Study — JUnit 5 + Mockito

Projeto de estudo focado em testes automatizados no ecossistema Java/Spring, construído incrementalmente ao longo de um plano de estudos diário.

O ponto de partida é um `TarefaService` — a camada de lógica de negócio de um gerenciador de tarefas — escrita em **Java puro, sem nenhuma dependência de Spring ou banco de dados**. A ideia é isolar a lógica de negócio para que ela seja testável por si só, sem depender de infraestrutura externa.

## O que o projeto cobre até agora

- **JUnit 5**: estrutura AAA (Arrange, Act, Assert), `@Nested` para organização por contexto, `@ParameterizedTest` com `@EnumSource`, `@CsvSource` e `@NullSource`
- **Mockito**: isolamento de dependências com `@Mock`/`@InjectMocks`, simulação de sucesso (`thenReturn`/`thenAnswer`) e falha (`thenThrow`/`doThrow`), verificação de interações (`verify`, `verify(never())`) e captura de argumentos construídos internamente (`ArgumentCaptor`)
- **Regras de negócio testadas**: validação de dados, máquina de estados de transição (`PENDENTE ↔ EM_ANDAMENTO ↔ CONCLUIDA`), propagação de exceções de domínio e de infraestrutura
## Funcionalidades do `TarefaService`

`criar`, `buscarPorId`, `listarPendentes`, `alterarStatus`, `atualizarPrioridade`, `excluir`, `marcarComoConcluida` — todas cobertas por testes unitários.

## Próximos passos

Camada REST com Spring Web + MockMvc, testes de integração com `@DataJpaTest`, e testes de contexto completo com `@SpringBootTest`.