# Especificação de Requisitos Estabelecer a "fonte da verdade" do projeto. Documentar o que o sistema faz e quais as restrições de cada perfil.

Tarefas:

    Criar docs/requirements.md com RFs e RNFs atualizados.
    Detalhar as Regras de Negócio (RN) específicas para Usuários Comuns vs Lojistas.
    Definir a política de tratamento de erros financeiros (HTTP Status Codes).


## 1. Visão Geral
O uma plataforma de pagamentos simplificada que permite a transferência de valores entre usuários. O sistema foca na consistência financeira, segurança de transações e integração com serviços externos de autorização e notificação.


## 2. Perfis de Usuário

| Atributo | Usuário Comum | Lojista (Merchant) |
| :--- | :--- | :--- |
| **Identificação** | Pessoa Física (CPF) | Pessoa Jurídica (CNPJ) |
| **Envio de Dinheiro** | Permitido para Comuns e Lojistas | **Proibido** |
| **Recebimento** | Permitido | Permitido |
| **Dados Obrigatórios** | Nome, CPF, E-mail, Senha | Nome, CNPJ, E-mail, Senha |


## 3. Regras de Negócio (RN)

* **RN01 (Unicidade de Identidade):** Não é permitido o cadastro de mais de uma conta com o mesmo CPF/CNPJ ou o mesmo endereço de E-mail.
* **RN02 (Solvência Obrigatória):** O pagador (`payer`) deve possuir saldo disponível maior ou igual ao valor da transferência no exato momento da operação.
* **RN03 (Restrição de Origem):** Lojistas estão bloqueados de iniciar qualquer operação de saída de valores (transferência).
* **RN04 (Autorização Mandatória):** Toda transferência depende de uma consulta bem-sucedida ao serviço autorizador externo (`GET`). Se o serviço negar ou estiver inacessível, a transação deve ser abortada.
* **RN05 (Atomicidade Financeira):** A operação de débito na conta do pagador e crédito na conta do beneficiário deve ocorrer dentro de uma única transação de banco de dados. Em caso de erro, o estado deve ser revertido (**Rollback**).
* **RN06 (Resiliência de Notificação):** O sucesso da transferência não depende da disponibilidade imediata do serviço de notificação. Caso o serviço de terceiro falhe, a notificação deve ser enfileirada para tentativa posterior (assincronismo).


## 4. Requisitos Funcionais (RF)
* **RF01 (Gestão de Contas):** O sistema deve permitir a criação e manutenção de usuários com suas respectivas carteiras.
* **RF02 (Processamento de Pagamento):** O sistema deve disponibilizar um endpoint para transferências seguindo o contrato:
  * `POST /transfer` (Payload: `value`, `payer`, `payee`).
* **RF03 (Consulta de Autorização):** Integrar com o Mock Externo de Autorização para validar o fluxo.
* **RF04 (Disparo de Alertas):** Notificar o beneficiário (via e-mail ou SMS) após o recebimento bem-sucedido de valores.
* **RF05 (Extrato de Transações):** Persistir o histórico de transferências para fins de auditoria e consulta.


## 5. Requisitos Não Funcionais (RNF)

* **RNF01 (Consistência):** Utilização de banco de dados relacional (PostgreSQL/MySQL) com isolamento de transação para evitar *Race Conditions* e *Double Spending*.
* **RNF02 (Desacoplamento):** O serviço de notificação deve ser processado de forma assíncrona (Event-Driven) para não impactar a latência do endpoint principal.
* **RNF03 (Segurança):** Armazenamento de senhas utilizando algoritmos de Hash fortes (BCrypt/Argon2).
* **RNF04 (Observabilidade):** Implementação de logs estruturados para rastreio de falhas em integrações externas.
* **RNF05 (Escalabilidade):** O ambiente deve ser totalmente conteinerizado via Docker.


## 6. Política de Tratamento de Erros (HTTP Status Codes)

| Status Code | Cenário de Aplicação |
| :--- | :--- |
| **201 Created** | Sucesso na criação de usuário ou processamento de transferência. |
| **400 Bad Request** | Dados de entrada inválidos (ex: valor negativo, campos ausentes). |
| **401 Unauthorized** | Falha na autenticação ou autorizador externo negou a transação. |
| **403 Forbidden** | Lojista tentando realizar transferência de saída. |
| **404 Not Found** | Payer ou Payee não encontrados no sistema. |
| **422 Unprocessable Entity** | Saldo insuficiente ou violação de regra de negócio (CPF duplicado). |
| **500 Internal Error** | Falha crítica no banco de dados ou erro inesperado. |
