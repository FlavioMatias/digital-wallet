## Documento de Definição: Garantia de Ativos e Blindagem de Dados

O objetivo deste documento é estabelecer as **Invariantes de Negócio** — regras que nunca podem ser quebradas para garantir a integridade do dinheiro e a privacidade do usuário.

### 1. Mapeamento de Cenários de Confiança (Casos de Teste de Domínio)

Aqui definimos o que o sistema **libera** e o que ele **bloqueia** para garantir que o dinheiro nunca "suma".

#### **A. O Fluxo de Sucesso (O Compromisso)**
* **Ação:** Usuário comum envia valor para Lojista ou outro Usuário.
* **Critério de Aceitação:** O débito na origem e o crédito no destino devem ocorrer simultaneamente. O sistema deve registrar a transação e agendar a notificação de recebimento.
* **Bloqueio:** Se qualquer uma dessas etapas falhar, o saldo original não deve ser alterado.

#### **B. Cenários de Exceção e Reversão (A Segurança)**
* **Falha no Autorizador Externo:** Caso o serviço de autorização retorne "Negado" ou esteja fora do ar (Timeout/Erro), a transação deve ser **abortada imediatamente**. O dinheiro não deve sequer "sair" da conta do pagador.
* **Tentativa de Saque de Lojista:** O sistema deve **bloquear** qualquer tentativa de envio de dinheiro onde o pagador seja um perfil "Lojista". Esta é uma restrição de categoria de conta.
* **Insuficiência de Ativos:** Bloqueio imediato se o valor da transação for superior ao saldo em conta + limites (se houver). O sistema deve retornar um erro de "Saldo Insuficiente" sem processar nada.

### 2. Controles de Proteção e Privacidade (Segurança de Negócio)

Regras para garantir que os dados estejam protegidos conforme as melhores práticas de mercado e legislação (LGPD).

#### **A. Blindagem de PII (Personally Identifiable Information)**
* **O que o sistema NÃO deve fazer:** Expor CPF, CNPJ ou E-mail em comprovantes de transferência ou logs de transação.
* **O que o sistema DEVE fazer:** Utilizar apenas identificadores únicos (IDs) para processar a lógica financeira. O nome do usuário deve ser usado apenas para fins de exibição/notificação, nunca como chave de transação.

#### **B. Integridade de Acesso e Inputs**
* **Hashing de Credenciais:** Nenhuma senha ou dado de autenticação pode ser armazenado em formato legível. O sistema deve transformar esses dados em códigos irreversíveis (Hash) no momento do cadastro.
* **Sanitização de Valores:** O sistema deve **bloquear** qualquer entrada de valor que não seja um número positivo. Valores nulos ou negativos devem ser rejeitados na "porta de entrada" da operação.
* **Proteção contra Duplicidade:** O sistema deve ignorar requisições idênticas enviadas em um curto espaço de tempo para evitar que o usuário pague duas vezes por um erro de clique ou instabilidade de rede.


### 3. Matriz de Restrições

| Operação | Lojista (Merchant) | Usuário Comum |
| :--- | :--- | :--- |
| **Realizar Pagamento** | ❌ **BLOQUEADO** | ✅ LIBERADO |
| **Receber Pagamento** | ✅ LIBERADO | ✅ LIBERADO |
| **Consultar Próprio Saldo** | ✅ LIBERADO | ✅ LIBERADO |
| **Alterar Dados de Terceiros**| ❌ **PROIBIDO** | ❌ **PROIBIDO** |
