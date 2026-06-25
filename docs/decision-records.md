
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

## 📑 ADR 001: Estilo Arquitetural Modular e Orientado a Eventos

### **1. Contexto**

O sistema precisa processar transações financeiras críticas que dependem de validações e notificações de terceiros.

  * O modelo tradicional de "monolito acoplado" geraria gargalos: se o sistema de notificações falhar ou ficar lento, a transação principal do usuário seria afetada, violando a disponibilidade do core financeiro.
  * É necessário isolar as regras de **Identidade** (quem é o usuário) das regras de **Movimentação** (como o dinheiro flui).

### **2. Decisão**

Adotaremos uma **Arquitetura Modular** com **Comunicação Orientada a Eventos**.

  * **Modularidade Funcional:** O código será organizado em módulos independentes (`Identity`, `Finance`, `Notification`). A comunicação entre eles será restrita a contratos de interfaces e troca de Identificadores (UUID), sem compartilhamento direto de tabelas ou objetos complexos de domínio.
  * **Core Baseado em Eventos:** Mudanças de estado (ex: "Pagamento Confirmado") gerarão eventos de domínio. Componentes secundários reagirão a esses eventos de forma assíncrona.

### **3. Consequências**

**Ganhos:**

  * **Desacoplamento Temporal:** O sistema não exige que o serviço de notificação esteja online para concluir um pagamento com sucesso.
  * **Isolamento de Falhas:** Um erro no módulo de avisos não interrompe o fluxo de depósitos ou transferências.
  * **Prontidão para Microserviços:** A estrutura modular permite que qualquer parte do sistema seja extraída para um serviço independente no futuro com esforço mínimo.

**Perdas/Custos (Contras):**

  * **Complexidade de Design:** Exige maior rigor na definição de fronteiras entre os módulos desde o primeiro dia.
  * **Consistência Eventual:** O usuário pode visualizar o saldo atualizado antes de receber a notificação formal, exigindo que o sistema lide com esse pequeno atraso de sincronia.

### **4. Alternativas Consideradas**

  * **Chamadas Síncronas (HTTP/Direct Call):** Descartadas por criarem dependência crítica; se um serviço falha, o fluxo inteiro para.
  * **Microsserviços Distribuídos:** Descartados para o MVP inicial para evitar o custo excessivo de infraestrutura e latência de rede desnecessária nesta fase.

-----

## 📑 ADR 002: Outbox Pattern para Garantia de Entrega de Eventos

### **1. Contexto**

O fluxo de transferência precisa publicar um evento no RabbitMQ após a conclusão bem-sucedida da transação. O problema é que publicar diretamente no broker dentro do fluxo de negócio cria uma janela de inconsistência: se a transação for confirmada no banco mas o processo morrer antes de publicar no Rabbit, o evento se perde permanentemente — nenhum consumer vai ser notificado, nenhuma notificação vai ser enviada, e não há como recuperar.

### **2. Decisão**

Adotaremos o **Outbox Pattern**: o evento de domínio é persistido na tabela `outbox_events` dentro da **mesma transação de banco** que confirma a transferência. Um componente separado (`OutboxDispatcher`) lê os eventos pendentes periodicamente e os publica no broker.

O `OutboxService.saveEvent()` é anotado com `@Transactional(propagation = MANDATORY)`, garantindo que nunca seja chamado fora de uma transação ativa — se a transação principal fizer rollback, o evento também não é salvo.

### **3. Consequências**

**Ganhos:**

  * **Atomicidade entre banco e broker:** Ou a transferência e o evento são persistidos juntos, ou nenhum dos dois é. Elimina a janela de inconsistência.
  * **Resiliência a crash:** Se o processo morrer após salvar no banco mas antes de publicar no Rabbit, o dispatcher vai reprocessar o evento ao subir novamente.
  * **Auditoria natural:** A tabela `outbox_events` funciona como log de todos os eventos de domínio emitidos.

**Perdas/Custos (Contras):**

  * **Latência eventual:** O dispatcher roda a cada 10 segundos (`@Scheduled(fixedDelay = 10000)`). Eventos não são publicados instantaneamente após a transação.
  * **Carga extra no banco:** A cada 10 segundos há uma query de leitura em `outbox_events` e updates para marcar eventos como processados.
  * **Complexidade operacional:** A tabela de outbox precisa de atenção — eventos que falham repetidamente precisam de estratégia de dead letter no próprio banco.

### **4. Alternativas Consideradas**

  * **Publicação direta no RabbitMQ dentro da transação:** Descartada pela janela de inconsistência descrita acima.
  * **Spring Events (`ApplicationEventPublisher`):** Descartada por ser in-memory — eventos são perdidos se o processo morrer antes de processá-los.
  * **Transactional Outbox via CDC (Debezium):** Mais robusto e sem latência do scheduler, mas adiciona complexidade operacional significativa (Kafka Connect, monitoramento do log do Postgres). Fora do escopo do MVP.

-----

## 📑 ADR 003: RabbitMQ como Broker com Filas Duráveis e Dead Letter Queue

### **1. Contexto**

Com o Outbox Pattern garantindo que os eventos chegam ao broker, ainda existe uma segunda janela de falha: entre o Rabbit entregar a mensagem ao consumer e o consumer processá-la com sucesso. Se o consumer morrer nesse intervalo sem confirmar o processamento, o comportamento depende inteiramente da configuração do broker.

Além disso, eventos que falham repetidamente no consumer precisam de um destino controlado — sem isso, entram em loop infinito de retry ou são silenciosamente descartados.

### **2. Decisão**

Adotaremos RabbitMQ com **filas duráveis** e **Dead Letter Queue (DLQ)** por módulo.

  * Filas declaradas com `durable = true` — sobrevivem a restart do broker.
  * Cada fila principal tem uma DLQ associada via `x-dead-letter-exchange` e `x-dead-letter-routing-key`.
  * Retry configurado com 3 tentativas e intervalo de 2 segundos (`initial-interval: 2000ms`) antes de encaminhar para a DLQ.
  * `default-requeue-rejected: false` — mensagens rejeitadas não voltam para a fila principal, vão direto para a DLQ.

### **3. Consequências**

**Ganhos:**

  * **Resiliência de entrega:** Mensagens não são perdidas se o consumer cair — o broker aguarda reconexão.
  * **Controle de falhas:** Eventos que falham repetidamente vão para a DLQ em vez de consumir recursos em loop infinito.
  * **Separação de concerns:** O Rabbit resolve a entrega; o Outbox resolve a publicação. Cada camada com sua responsabilidade.

**Perdas/Custos (Contras):**

  * **Custo operacional:** Mais um processo na infraestrutura que precisa de monitoramento, healthcheck e estratégia de backup.
  * **DLQ precisa de atenção ativa:** Mensagens que chegam na DLQ ficam paradas até alguém investigar e decidir reprocessar ou descartar. Sem monitoramento, podem acumular silenciosamente.
  * **Acknowledge mode:** A configuração atual usa `AUTO` ack — o Spring confirma o recebimento automaticamente quando o listener retorna sem exceção. Para máxima resiliência, `MANUAL` ack seria necessário, controlando explicitamente quando o evento é removido da fila.

### **4. Alternativas Consideradas**

  * **Spring Events internos:** Descartados por serem in-memory e não sobreviverem a crash do processo.
  * **Kafka:** Maior throughput e replay nativo de eventos, mas infraestrutura significativamente mais complexa para o escopo atual.

-----

## 📑 ADR 004: Domínio Rico com Value Objects e Invariantes no Agregado

### **1. Contexto**

Sistemas financeiros têm regras críticas de integridade: saldo não pode ficar negativo, transferência não pode ocorrer com wallet bloqueada, valor zero ou negativo não pode ser transferido. A abordagem mais comum — validar essas regras no Service — distribui a lógica de negócio fora do domínio, tornando-a fácil de esquecer e difícil de testar isoladamente.

### **2. Decisão**

As entidades de domínio (`Wallet`, `Transaction`) são **ricas em comportamento** e protegem suas próprias invariantes. Nenhuma regra de negócio financeira vive fora do domínio.

  * `Wallet` expõe métodos comportamentais (`deposit`, `reserveBalance`, `confirmPaymentFromBlocked`, `rollbackBlockedBalance`) que validam e aplicam as regras antes de alterar estado.
  * `Money` é um Value Object imutável com escala de 2 casas e `RoundingMode.HALF_EVEN` — operações retornam novos objetos, nunca mutam o existente.
  * `Document` valida CPF e CNPJ no construtor com algoritmo de dígito verificador — um `Document` inválido nunca é instanciado.
  * `Email` valida formato no construtor — um `Email` inválido nunca é instanciado.
  * `Transaction` controla suas próprias transições de estado (`PENDING → AUTHORIZED → COMPLETED`, `* → FAILED`) e rejeita transições inválidas com exceção de domínio.

### **3. Consequências**

**Ganhos:**

  * **Invariantes garantidas:** Impossível ter saldo negativo, documento inválido ou transição de estado ilegal — o próprio objeto rejeita.
  * **Testabilidade:** As regras de negócio mais críticas são testadas diretamente nas entidades, sem precisar montar contexto Spring ou mockar dependências.
  * **Expressividade:** O código lê como o domínio financeiro funciona, não como uma sequência de operações de banco de dados.

**Perdas/Custos (Contras):**

  * **Tensão com JPA:** Entidades ricas precisam de construtores sem argumento e setters para o ORM funcionar — isso expõe campos que deveriam ser privados. O `@Setter` do Lombok na `Wallet` é o sintoma mais visível dessa tensão.
  * **Curva de aprendizado:** Desenvolvedores acostumados com o padrão anêmico podem ter dificuldade em saber onde colocar comportamento novo.

### **4. Alternativas Consideradas**

  * **Modelo anêmico com validação no Service:** Descartado por espalhar regras de negócio fora do domínio, tornando fácil bypassar validações chamando setters diretamente.
  * **Bean Validation (`@NotNull`, `@Positive`) nas entidades:** Complementar mas insuficiente — não expressa regras comportamentais como a máquina de estados da transação.

-----

## 📑 ADR 005: `TransactionPair` como Abstração de Atomicidade no Domínio

### **1. Contexto**

Toda transferência gera obrigatoriamente dois registros: um débito na carteira de origem e um crédito na carteira de destino. Tratar esses dois registros como objetos independentes no código cria risco de inconsistência — é possível autorizar um sem o outro, completar um e falhar o outro, ou salvar apenas um no banco.

### **2. Decisão**

`TransactionPair` é um record imutável que encapsula o par débito/crédito e expõe operações que atuam **sempre nos dois simultaneamente**: `authorize()`, `fail()`, `complet()`, `isAuthorized()`.

O construtor valida que nenhum dos dois é nulo. O `toList()` retorna os dois para persistência em lote. É impossível, via API pública do `TransactionPair`, autorizar só o débito sem o crédito.

### **3. Consequências**

**Ganhos:**

  * **Atomicidade no domínio:** A consistência entre débito e crédito é imposta pela abstração, não depende de disciplina do desenvolvedor.
  * **Expressividade:** O código do `TransferAppService` lida com `transactions.authorize()` e `transactions.fail()` — deixa claro que a operação é sobre o par, não sobre registros individuais.

**Perdas/Custos (Contras):**

  * **Rigidez:** Cenários onde débito e crédito precisam ter estados independentes (ex: débito confirmado, crédito pendente de liquidação) exigiriam redesenho da abstração.

### **4. Alternativas Consideradas**

  * **Tratar debit e credit como objetos separados no Service:** Descartado pelo risco de inconsistência — requer que o desenvolvedor sempre lembre de operar nos dois.

-----

## 📑 ADR 006: Redis para Armazenamento de Refresh Tokens

### **1. Contexto**

O sistema de autenticação emite Access Tokens JWT de curta duração e Refresh Tokens para renovação. Refresh Tokens precisam de armazenamento persistente para validação e revogação. As opções são banco relacional (Postgres) ou um store chave-valor com TTL nativo.

### **2. Decisão**

Refresh Tokens são armazenados no **Redis** via `@RedisHash` com `timeToLive = 604800` (7 dias).

  * O Redis gerencia a expiração automaticamente — tokens expirados são removidos sem necessidade de job de limpeza.
  * O token anterior é invalidado no login (`deleteByUserEmail`) antes de criar o novo — previne acúmulo e garante que apenas um refresh token ativo por usuário.
  * O índice por `userEmail` (`@Indexed`) permite busca e deleção eficiente por usuário.

### **3. Consequências**

**Ganhos:**

  * **TTL automático:** Sem jobs de limpeza, sem tokens expirados acumulando no banco.
  * **Latência baixa:** Operações de validação de refresh token são sub-milissegundo.
  * **Revogação simples:** `deleteByUserEmail` invalida todos os tokens do usuário em uma operação — útil para logout ou compromisso de conta.

**Perdas/Custos (Contras):**

  * **Mais um serviço para operar:** Redis precisa de monitoramento, persistência configurada e estratégia de backup.
  * **TTL hardcoded:** O valor de 7 dias está hardcoded na anotação `@RedisHash(timeToLive = 604800)`. Alterar exige recompilação — deveria ser externalizado para o `application.yaml`.
  * **Consistência eventual em cluster Redis:** Em setup com Redis Cluster ou Sentinel, há janela mínima de replicação. Para tokens de segurança, isso é aceitável mas deve ser conhecido.

### **4. Alternativas Consideradas**

  * **Postgres para Refresh Tokens:** Funciona, mas exige job de limpeza de tokens expirados e adiciona carga desnecessária ao banco principal de negócio.
  * **Stateless com JWT de longa duração:** Descartado por não permitir revogação — um token comprometido ficaria válido até expirar naturalmente.

-----

## 📑 ADR 007: Argon2 para Hashing de Senhas

### **1. Contexto**

Senhas de usuários precisam ser armazenadas com hashing seguro e resistente a ataques de força bruta e rainbow tables. A escolha do algoritmo tem impacto direto na segurança das contas.

### **2. Decisão**

Utilizaremos **Argon2** via `Argon2PasswordEncoder.defaultsForSpringSecurity_v5_8()`.

Argon2 é o vencedor da Password Hashing Competition (2015) e é recomendado pelo OWASP como primeira escolha para hashing de senhas. É parametrizável em uso de memória, tempo de CPU e paralelismo — tornando ataques de GPU e ASIC economicamente inviáveis.

### **3. Consequências**

**Ganhos:**

  * **Resistência superior:** Argon2 é deliberadamente custoso em memória, o que dificulta ataques em hardware paralelo (GPU farms) comparado a BCrypt.
  * **Recomendação atual:** OWASP e NIST recomendam Argon2id como primeira opção para novos sistemas.

**Perdas/Custos (Contras):**

  * **Custo de CPU/memória no servidor:** Cada operação de login consome mais recursos que BCrypt. Em sistemas com milhares de logins por segundo, pode ser necessário ajustar os parâmetros.
  * **Compatibilidade:** Sistemas legados que usam BCrypt precisariam de migração para adotar Argon2.

### **4. Alternativas Consideradas**

  * **BCrypt:** Amplamente suportado e seguro, mas mais vulnerável a ataques com hardware especializado por não usar memória como fator de custo.
  * **SHA-256/MD5 simples:** Descartados completamente — não são algoritmos de hashing de senha e são trivialmente quebráveis.

-----

## 📑 ADR 008: Port e Adapter para Integração com Serviços Externos

### **1. Contexto**

O fluxo de transferência depende de um serviço externo de autorização. Acoplar o `TransferAppService` diretamente a uma implementação HTTP cria dois problemas: torna o domínio dependente de infraestrutura e dificulta testes unitários (que precisariam mockar chamadas HTTP reais).

### **2. Decisão**

Serviços externos são acessados via **Port (interface no domínio) e Adapter (implementação na infra)**.

  * `TransferAuthorization` é uma interface no pacote de domínio (`domain/port/transfer`) — o domínio sabe que precisa autorizar, mas não sabe como.
  * `TransferAuthorizationGateway` é a implementação concreta na camada de infra, usando `RestClient` para chamar `https://util.devi.tools/api/v2/authorize`.
  * Nos testes, `TransferAuthorization` é mockado via Mockito — os testes de domínio e aplicação nunca fazem chamadas HTTP reais.

### **3. Consequências**

**Ganhos:**

  * **Testabilidade:** O `TransferAppServiceTest` usa `@Mock TransferAuthorization` e controla o comportamento (`when(transferAuthorization.authorize()).thenReturn(true/false)`) sem depender de rede.
  * **Substituibilidade:** Trocar o autorizador externo por outro provedor exige apenas uma nova implementação do adapter — o domínio não muda.
  * **Isolamento:** Falhas na integração externa não propagam stack traces de HTTP para dentro do domínio.

**Perdas/Custos (Contras):**

  * **Indireção:** Para entender o fluxo completo, é necessário navegar da interface à implementação concreta.
  * **URL hardcoded no adapter:** Atualmente `https://util.devi.tools/api` está hardcoded no `TransferAuthorizationGateway` — deveria ser externalizado para o `application.yaml` para facilitar troca de ambiente.

### **4. Alternativas Consideradas**

  * **Chamar o RestClient diretamente no TransferAppService:** Descartado por acoplar lógica de negócio a detalhe de infraestrutura e tornar testes unitários dependentes de rede.
