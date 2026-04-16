# Digital Wallet

## 📌 Sobre o Projeto

Este sistema é uma plataforma de pagamentos simplificada que permite a transferência de valores entre usuários. O foco principal é a **consistência financeira**, garantindo que as operações de débito e crédito sejam atômicas, seguras e validadas por serviços externos.

O projeto foi desenhado sob os princípios de **Invariantes de Negócio**, assegurando que regras críticas nunca sejam violadas.


## 🏗️ Arquitetura e Design

O projeto utiliza uma **Arquitetura Modular e Orientada a Eventos (ADR 001)** para garantir o desacoplamento entre os domínios de Identidade, Finanças e Notificações.

## 📄 Decisões Arquiteturais (ADRs)

Para entender o "porquê" das nossas escolhas técnicas, acesse nossa pasta de **[Architecture Decision Records]()**.


### Principais Tecnologias

  * **Linguagem:** Java
  * **Banco de Dados:** Postgres para garantir transações ACID.
  * **Comunicação:** Assíncrona para notificações de terceiros.
  * **Segurança:** Hashing de senhas (BCrypt) e Blindagem de PII.


## 🛠️ Regras de Negócio (Destaques)

  * **RN03 (Restrição de Origem):** Lojistas (`Merchants`) só podem receber pagamentos, nunca realizá-los.
  * **RN05 (Atomicidade):** O débito no pagador e crédito no beneficiário ocorrem em uma única transação (Rollback em caso de falha).
  * **RN04 (Autorização Externa):** Toda transação é validada por um serviço de autorização externo antes de ser concluída.


## 🚀 Como Executar

### Pré-requisitos

  * Docker & Docker Compose

### Instalação

1.  Clone o repositório:
    ```bash
    git clone https://github.com/FlavioMatias/digital-wallet.git
    ```
2.  Suba o ambiente via Docker:
    ```bash
    docker-compose up -d
    ```
3.  O sistema estará disponível em `http://localhost:8080`.
