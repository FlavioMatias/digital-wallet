
# Architecture Decision Records (ADR)

Este documento centraliza as decisões de design de software que moldam o sistema. O objetivo é fornecer visibilidade sobre o "porquê" das escolhas técnicas, facilitando a manutenção e a evolução do sistema por outros engenheiros.

## Como Documentar Novas Decisões

Para manter a consistência e a clareza, cada nova decisão arquitetural deve seguir o formato **Contexto → Decisão → Consequência**:

1.  **Título e ID:** Identificação clara e sequencial (ex: `ADR 002: Estratégia de Persistência`).
2.  **Contexto (O Problema):** Descrição objetiva da dor ou do requisito de negócio que motivou a mudança. O que acontece se não decidirmos nada?
3.  **Decisão (A Escolha):** A solução adotada. Deve ser direta, evitando "talvez" ou "poderia".
4.  **Consequências (Trade-offs):** Honestidade técnica sobre o que ganhamos (Prós) e o que perdemos ou assumimos de complexidade (Contras).
5.  **Alternativas Consideradas:** Uma breve menção a outras opções avaliadas e por que foram descartadas.

-----


##  📑 ADR 001: Estilo Arquitetural Modular e Orientado a Eventos

### **1. Contexto**

O sistema precisa processar transações financeiras críticas que dependem de validações e notificações de terceiros.

  * O modelo tradicional de "monolito acoplado" geraria gargalos: se o sistema de notificações falhar ou ficar lento, a transação principal do usuário seria afetada, violando a disponibilidade do core financeiro.
  * É necessário isolar as regras de **Identidade** (quem é o usuário) das regras de **Movimentação** (como o dinheiro flui).

### **2. Decisão**

Adotaremos uma **Arquitetura Modular** com **Comunicação Orientada a Eventos**.

  * **Modularidade Funcional:** O código será organizado em módulos independentes (`Identity`, `Finance`, `Notification`). A comunicação entre eles será restrita a contratos de interfaces e troca de Identificadores (UUID), sem compartilhamento direto de tabelas ou objetos complexos de domínio.
  * **Core Baseado em Eventos:** Mudanças de estado (ex: "Pagamento Confirmado") gerarão eventos de domínio. Componentes secundários reagirão a esses eventos de forma assíncrona.

### **3. Consequências**

** Ganhos :**

  * **Desacoplamento Temporal:** O sistema não exige que o serviço de notificação esteja online para concluir um pagamento com sucesso.
  * **Isolamento de Falhas:** Um erro no módulo de avisos não interrompe o fluxo de depósitos ou transferências.
  * **Prontidão para Microserviços:** A estrutura modular permite que qualquer parte do sistema seja extraída para um serviço independente no futuro com esforço mínimo.

** Perdas/Custos (Contras):**

  * **Complexidade de Design:** Exige maior rigor na definição de fronteiras entre os módulos desde o primeiro dia.
  * **Consistência Eventual:** O usuário pode visualizar o saldo atualizado antes de receber a notificação formal, exigindo que o sistema lide com esse pequeno atraso de sincronia.

### **4. Alternativas Consideradas**

  * **Chamadas Síncronas (HTTP/Direct Call):** Descartadas por criarem dependência crítica; se um serviço falha, o fluxo inteiro para.
  * **Microsserviços Distribuídos:** Descartados para o MVP inicial para evitar o custo excessivo de infraestrutura e latência de rede desnecessária nesta fase.
