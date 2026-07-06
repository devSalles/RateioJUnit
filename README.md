# 🧪 Projeto de Testes Unitários - Sistema de Rateio de Despesas

## 📖 Sobre o Projeto

Este projeto foi desenvolvido com o objetivo de praticar e consolidar conceitos de **testes unitários em aplicações Java utilizando Spring Boot**.

Para isso, foi implementada uma API REST de gerenciamento de despesas compartilhadas entre participantes, contendo regras de negócio financeiras e validações que servem como base para a criação de testes unitários.

Os testes foram desenvolvidos utilizando **JUnit 5** e **Mockito**, buscando validar tanto cenários de sucesso quanto regras de negócio, exceções e casos de erro.

---

# 🎯 Objetivos

- Praticar Testes Unitários com JUnit 5.
- Utilizar Mockito para criação de mocks.
- Validar regras de negócio.
- Testar exceções.
- Garantir alta cobertura dos Services.
- Aplicar boas práticas de testes.

---

# 🚀 Tecnologias Utilizadas

- Java 21
- Spring Boot
- Spring Data JPA
- Hibernate
- Bean Validation
- Lombok
- Maven
- JUnit 5
- Mockito
- Swagger/OpenAPI

---

# 🧪 Testes Unitários

O foco principal deste projeto está na camada de **Service**, onde estão concentradas todas as regras de negócio.

### Tecnologias utilizadas

- JUnit 5
- Mockito

### Camadas testadas

- ✅ ParticipanteService
- ✅ DespesaService
- ✅ SaldoService

### Estratégias utilizadas

- Mock de Repositories
- Mock de Services
- Factories para criação de objetos
- Testes positivos
- Testes negativos
- Testes de exceções
- Validação de regras de negócio
- Validação de cálculos financeiros

---

# 📊 Cobertura dos Testes

Os testes contemplam cenários como:

- Cadastro de participantes
- Atualização de participantes
- Exclusão de participantes
- Cadastro de despesas
- Atualização de despesas
- Finalização de despesas
- Cancelamento de despesas
- Cálculo de saldos
- Busca por filtros
- Validação de exceções
- Validação de regras financeiras

---

# 📁 Estrutura do Projeto

```text
RateioJUnit
├── src
│   ├── main
│   │   ├── java
│   │   │   └── RateioJUnit
│   │   │       ├── controller
│   │   │       │   ├── DespesaController.java
│   │   │       │   ├── ParticipanteController.java
│   │   │       │   └── SaldoController.java
│   │   │       │
│   │   │       ├── core
│   │   │       │   ├── exception
│   │   │       │   │   ├── despesa
│   │   │       │   │   ├── participante
│   │   │       │   │   ├── IdNaoEncontradoException.java
│   │   │       │   │   └── NenhumRegistroException.java
│   │   │       │   └── infra
│   │   │       │
│   │   │       ├── dto
│   │   │       │   ├── despesa
│   │   │       │   ├── divisao
│   │   │       │   ├── saldo
│   │   │       │   └── usuario
│   │   │       │
│   │   │       ├── entity
│   │   │       ├── enums
│   │   │       ├── repository
│   │   │       ├── service
│   │   │       └── RateioJUnitApplication.java
│   │   │
│   │   └── resources
│   │
│   └── test
│       └── java
│           └── RateioJUnit
│               ├── factory
│               │   ├── DespesaFactory.java
│               │   ├── ParticipanteFactory.java
│               │   └── SaldoFactory.java
│               │
│               ├── service
│               │   ├── DespesaServiceTest.java
│               │   ├── ParticipanteServiceTest.java
│               │   └── SaldoServiceTest.java
│               │
│               └── RateioJUnitApplicationTests.java
│
└── pom.xml
```

# 📋 Regras de Negócio 

Este documento descreve todas as regras de negócio implementadas no sistema de rateio de despesas.

---

# 👤 Participante

## Cadastro

- O participante deve possuir um **ID único**.
- O nome do participante é obrigatório.
- O e-mail do participante deve ser único no sistema.

## Relacionamentos

Um participante pode:

- Participar de diversas despesas.
- Ser o pagador de diversas despesas.

## Exclusão

Um participante **não pode ser removido** caso possua qualquer vínculo com o sistema, como:

- Despesas pagas;
- Divisões de despesas;
- Saldos como credor;
- Saldos como devedor.

Caso exista qualquer vínculo, uma exceção será lançada.

---

# 💸 Despesa

Toda despesa deve possuir obrigatoriamente:

- Descrição;
- Valor total;
- Pagador;
- Lista de participantes;
- Tipo de divisão.

## Validações

- O valor total deve ser maior que zero.
- O pagador deve obrigatoriamente estar entre os participantes da despesa.
- Não é permitido cadastrar participantes duplicados na mesma despesa.
- A soma das divisões deve ser exatamente igual ao valor total da despesa.

---

# 🧮 Tipos de Divisão

O sistema suporta dois tipos de divisão.

## Divisão Igual

Quando a divisão for igual:

- O valor total será dividido igualmente entre todos os participantes.
- Os valores serão arredondados para duas casas decimais.
- Caso exista diferença de arredondamento, os centavos restantes serão distribuídos entre os primeiros participantes da lista.
- A soma das divisões deverá permanecer exatamente igual ao valor total da despesa.

### Exemplo

Valor Total:

```
100,00
```

Participantes:

```
3
```

Resultado:

```
33,34
33,33
33,33
```

---

## Divisão Personalizada

Quando a divisão for personalizada:

- Cada participante deverá possuir um valor definido.
- O valor individual deve ser maior que zero.
- Nenhum participante pode ficar sem valor.
- A soma dos valores individuais deve ser igual ao valor total da despesa.

---

# 👥 Participantes da Despesa

- Cada divisão pertence a exatamente um participante.
- Um participante não pode aparecer mais de uma vez na mesma despesa.
- Cada participante deve possuir um valor definido após o cálculo da divisão.

---

# 💰 Pagador

O pagador da despesa:

- Deve obrigatoriamente estar presente na lista de participantes.
- É responsável pelo pagamento integral da despesa.
- Pode participar normalmente da divisão da despesa.

---

# 📊 Saldos

Após a finalização da despesa, o sistema calcula automaticamente:

- Quem deve;
- Quem recebe;
- O valor devido.

## Regras

- O pagador nunca gera dívida para si próprio.
- Cada participante diferente do pagador gera um saldo devedor.
- O valor do saldo corresponde exatamente ao valor da divisão daquele participante.

O sistema também permite consultar:

- Todos os saldos cadastrados;
- Saldos de um participante;
- Total a receber de um participante;
- Total a pagar de um participante;
- Saldo total entre dois participantes.

---

# 🔄 Atualização da Despesa

Uma despesa somente poderá ser alterada enquanto estiver com status:

- `CRIADA`

Após a finalização:

- Não é permitido alterar descrição;
- Não é permitido alterar o pagador;
- Não é permitido alterar qualquer informação da despesa.

Tentativas de alteração gerarão exceção.

---

# 📦 Finalização da Despesa

Ao finalizar uma despesa:

- Os saldos são calculados automaticamente;
- Os registros de saldo são criados;
- O status passa para **FINALIZADA**.

Uma despesa:

- Não pode ser finalizada duas vezes.
- Não pode ser finalizada caso esteja cancelada.

---

# ❌ Cancelamento da Despesa

Uma despesa somente pode ser cancelada quando estiver no status:

- `CRIADA`

Ao cancelar:

- Todos os saldos vinculados à despesa são removidos.
- O status passa para **CANCELADA**.

Não é permitido:

- Cancelar uma despesa já cancelada;
- Cancelar uma despesa finalizada.

---

# 📌 Status da Despesa

A despesa possui três estados possíveis:

| Status | Descrição |
|---------|-----------|
| `CRIADA` | Despesa cadastrada e editável |
| `FINALIZADA` | Despesa concluída e com saldos calculados |
| `CANCELADA` | Despesa cancelada e sem efeitos financeiros |

## Transições permitidas

```
CRIADA
 ├──► FINALIZADA
 └──► CANCELADA
```

Não são permitidas as seguintes transições:

- FINALIZADA → CRIADA
- CANCELADA → CRIADA
- CANCELADA → FINALIZADA

---

# ⚠️ Principais Regras Validadas

- Valor total deve ser maior que zero.
- Pagador deve estar entre os participantes.
- Participantes duplicados não são permitidos.
- Soma das divisões deve ser igual ao valor total.
- Apenas despesas criadas podem ser alteradas.
- Apenas despesas criadas podem ser canceladas.
- Despesas finalizadas não podem ser modificadas.
- Despesas canceladas não podem ser finalizadas.
- Participantes vinculados a despesas ou saldos não podem ser removidos.

---

- # 🌐 API REST

A API é composta por três controladores principais:

- Participante
- Despesa
- Saldo

---

# 👤 Participantes

Base URL:

```
/participante
```

| Método | Endpoint | Descrição |
|---------|----------|-----------|
| POST | `/adicionar-participante` | Cadastra um novo participante. |
| PUT | `/atualizar-participante/{idParticipante}` | Atualiza os dados de um participante. |
| GET | `/buscar-detalhes/{idParticipante}` | Busca um participante pelo ID. |
| GET | `/buscar-todos-participantes` | Lista todos os participantes. |
| GET | `/buscar-por-email` | Busca um participante pelo e-mail. |
| GET | `/buscar-por-nome` | Busca participantes pelo nome. |
| DELETE | `/deletar-participante/{idParticipante}` | Remove um participante, caso ele não possua vínculos com despesas ou saldos. |

### Cadastrar Participante

**Endpoint**

```http
POST /participante/adicionar-participante
```
**Request Body**

```json
{
  "nome": "Maria Oliveira",
  "email": "maria.oliveira@hotmail.com"
}
```
```json
{
  "nome": "João Silva",
  "email": "joao.silva@gmail.com"
}
```
---

# 💸 Despesas

Base URL:

```
/Despesa
```

| Método | Endpoint | Descrição |
|---------|----------|-----------|
| POST | `/adicionar-despesa` | Cadastra uma nova despesa. |
| PUT | `/atualizar-despesa/{id}` | Atualiza uma despesa que esteja com status **CRIADA**. |
| PUT | `/finalizar-despesa/{id}` | Finaliza a despesa e calcula automaticamente os saldos. |
| PUT | `/cancelar-despesa/{id}` | Cancela uma despesa criada e remove seus efeitos financeiros. |
| GET | `/buscar-todas-despesa` | Lista todas as despesas cadastradas. |
| GET | `/buscar-despesa-especifica/{id}` | Busca uma despesa pelo ID. |
| GET | `/buscar-despesa-por-status` | Lista despesas por status. |
| GET | `/buscar-despesa-por-data` | Busca despesas dentro de um intervalo de datas. |
| GET | `/buscar-despesa-por-tipo-divisao` | Lista despesas pelo tipo de divisão. |

### Cadastrar Despesa

**Endpoint**

```http
POST /Despesa/adicionar-despesa
```

**Request Body**

```json
{
  "descricao": "Almoço da equipe",
  "valorTotal": 120.00,
  "idPagador": 1,
  "participantes": [
    {
      "idParticipante": 1,
      "valor": 60.00
    },
    {
      "idParticipante": 2,
      "valor": 60.00
    }
  ],
  "tipoDivisao": "IGUAL"
}
```

### Descrição dos Campos

| Campo | Tipo | Obrigatório | Descrição |
|--------|------|-------------|-----------|
| descricao | String | Sim | Descrição da despesa. |
| valorTotal | Decimal | Sim | Valor total da despesa. |
| idPagador | Long | Sim | ID do participante que realizou o pagamento. |
| participantes | Lista | Sim | Lista de participantes da despesa. |
| participantes[].idParticipante | Long | Sim | ID do participante. |
| participantes[].valor | Decimal | Sim | Valor correspondente ao participante (utilizado na divisão personalizada). |
| tipoDivisao | Enum | Sim | Tipo de divisão (`IGUAL` ou `PERSONALIZADA`). |

---

## 📌 Valores aceitos para `tipoDivisao`

```text
IGUAL
PERSONALIZADA
```

---

## 📌 Valores aceitos para `statusDespesa`

```text
CRIADA
FINALIZADA
CANCELADA
```
---

# 💰 Saldos

Base URL:

```
/saldo
```

| Método | Endpoint | Descrição |
|---------|----------|-----------|
| GET | `/listar-todos-saldos` | Lista todos os saldos cadastrados. |
| GET | `/participante/{idParticipante}` | Lista todos os saldos de um participante. |
| GET | `/participante/{idParticipante}/total` | Consulta o total a receber e o total a pagar de um participante. |
| GET | `/entre-dois-participantes` | Consulta o saldo total existente entre dois participantes. |

---

# 📖 Documentação da API

Após iniciar a aplicação, acesse:

```
http://localhost:8080/swagger-ui/index.html
```

# 📌 Status da Despesa

Os endpoints que recebem o parâmetro `statusDespesa` aceitam os seguintes valores:

```text
CRIADA
FINALIZADA
CANCELADA
```

---

# 📌 Tipos de Divisão

Os endpoints que recebem o parâmetro `tipoDivisao` aceitam os seguintes valores:

```text
IGUAL
PERSONALIZADA
```

---

# 📅 Consulta por Período

O endpoint:

```
GET /Despesa/buscar-despesa-por-data
```

recebe dois parâmetros:

| Parâmetro | Tipo |
|-----------|------|
| inicio | LocalDate |
| fim | LocalDate |

Exemplo:

```http
GET /Despesa/buscar-despesa-por-data?inicio=2026-01-01&fim=2026-01-31
```
