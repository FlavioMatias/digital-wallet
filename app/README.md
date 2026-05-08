# Digital Wallet — App Flutter

Carteira digital / pagamentos simplificados com visual fintech brasileiro (preto + verde menta).

## Stack

| Camada | Tecnologia |
|--------|-----------|
| UI | Flutter 3.x + Material 3 |
| Estado | Provider |
| Banco local | sqflite (SQLite) |
| API | Mock JSON local (assets/mocks/) |
| Tipografia | Google Fonts (Inter) |
| Formatação | intl (pt_BR) |

## Como rodar

```bash
cd app
flutter pub get
flutter run
```

## Telas implementadas

- Login — e-mail + senha com validação, toggle de visibilidade
- Dashboard — responsivo (mobile / tablet via LayoutBuilder)
- Fluxo de Pagamento — Destinatário -> Valor -> Confirmação -> Sucesso
- Erros — 422 Saldo insuficiente | 403 Lojista bloqueado | 401 Negado | 500 Genérico

## Screenshots

> Adicione prints da tela de Login, Dashboard e tela de Valor aqui antes da entrega.

## Credenciais de teste

Login com qualquer e-mail dos dados de seed:

| Nome | E-mail | Tipo |
|------|--------|------|
| Flavio Matias | flavio@example.com | Comum |
| Ana Silva | ana@example.com | Comum |
| Padaria Bom Sabor | padaria@example.com | Lojista |
