# Entrega — Digital Wallet App

## Checklist das 3 Tarefas

### Tarefa 1 — Escopo + Protótipo
- [x] App acessa "API" mock (JSON em `assets/mocks/`)
- [x] Persistência local com SQLite (`sqflite`) — 3 tabelas: `users`, `transactions`, `session`
- [x] Dados de seed na primeira execução (Flavio Matias R$ 527,44, 5 contatos, 4 transações)
- [x] Tela de Login implementada
- [x] Tela de Dashboard implementada
- [x] Fluxo de Pagamento completo (4 sub-telas)
- [x] Telas de erro (422, 403, 401, 500)

### Tarefa 2 — Layout + Responsividade
- [x] Tela do Dashboard com 4 seções visuais distintas (header, card de saldo, atalhos, lista)
- [x] `Column`, `Row`, `Expanded`, `Padding`, `SizedBox` usados corretamente
- [x] `LayoutBuilder` no Dashboard alternando entre layouts:
  - `< 600px` → `_MobileLayout` (coluna única, scroll vertical)
  - `>= 600px` → `_TabletLayout` (card + atalhos à esquerda | transações à direita)
- [x] Nenhuma lógica de negócio misturada ao layout

### Tarefa 3 — Eventos e Interatividade
- [x] `TextField` com `onChanged` — campo de mensagem em `PaymentAmountScreen`; loga via `print` e exibe erro se > 100 chars
- [x] Botão "Continuar" (`onPressed`) — ativo apenas se valor > 0 e ≤ saldo; exibe `SnackBar` em caso de erro
- [x] Botão "Trocar destinatário" — comportamento diferente: volta para tela anterior e limpa estado
- [x] `GestureDetector` no avatar do destinatário com 2 gestos:
  - `onTap` → `SnackBar` com nome + e-mail
  - `onLongPress` → `AlertDialog` "Remover destinatário?"
- [x] Encadeamento de eventos: Continuar → valida saldo → se OK navega para Confirmação → SlideToConfirm → chama autorizador → efetiva transação → navega para Sucesso

---

## Mapeamento Requisito → Arquivo

| Requisito | Arquivo |
|-----------|---------|
| Tema, cores, tipografia | `lib/core/theme/` |
| Banco SQLite + seed | `lib/data/database/database_helper.dart` |
| Modelos de dados | `lib/data/models/user.dart`, `transaction.dart` |
| Repositórios | `lib/data/repositories/` |
| API mock (JSON) | `lib/data/services/mock_api_service.dart` + `assets/mocks/` |
| Provider de autenticação | `lib/providers/auth_provider.dart` |
| Provider de transações | `lib/providers/transaction_provider.dart` |
| Tela de Login | `lib/features/auth/login_screen.dart` |
| Dashboard + LayoutBuilder | `lib/features/dashboard/dashboard_screen.dart` |
| Widgets do Dashboard | `lib/features/dashboard/widgets/` |
| Seleção de destinatário | `lib/features/payment/payment_recipient_screen.dart` |
| **Todos os eventos (Tarefa 3)** | `lib/features/payment/payment_amount_screen.dart` |
| Confirmação + SlideToConfirm | `lib/features/payment/payment_confirm_screen.dart` |
| Tela de Sucesso | `lib/features/payment/payment_success_screen.dart` |
| Erros (422, 403, 401, 500) | `lib/features/errors/` |
| Widgets compartilhados | `lib/shared/widgets/` |
| Roteamento + providers | `lib/app.dart` |

---

## Decisões de Design

1. **Teclado numérico customizado em vez de `TextField` para valor:** evita problemas de formatação em tempo real com R$ e garante que o usuário só insira dígitos válidos, mantendo a experiência de "calculadora" típica de apps fintech.

2. **`GestureDetector` no avatar, não em `ListTile.onLongPress`:** `ListTile` não expõe `onLongPress` com semântica visual clara; `GestureDetector` permite separar explicitamente os dois gestos e seu feedback é mais preciso para o requisito da Tarefa 3.

3. **`LayoutBuilder` no Dashboard, não `MediaQuery`:** `LayoutBuilder` reage à largura disponível do widget pai (não da tela), o que torna o layout correto mesmo quando encaixado em Split View em tablets.

4. **Notificação resiliente sem `await`:** conforme RN06, `MockApiService.sendNotification()` é chamado sem `await`, de forma que falhas na notificação não bloqueiam nem aparecem para o usuário — apenas são logadas internamente.

5. **`db.transaction()` para atomicidade:** débito + crédito + insert da transação ocorrem dentro de uma única transação SQLite, garantindo que nunca haverá estado parcial (saldo debitado sem transação registrada).

---

## Como rodar

```bash
cd app
flutter pub get
flutter run
```

> Para forçar a resposta negada do autorizador (tela 401), altere `kForceAuthDenied = true` em `lib/data/services/mock_api_service.dart`.
