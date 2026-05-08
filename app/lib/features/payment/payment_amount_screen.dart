import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import '../../core/theme/app_colors.dart';
import '../../core/utils/currency_formatter.dart';
import '../../data/models/user.dart';
import '../../providers/auth_provider.dart';
import 'widgets/amount_keypad.dart';

// ── Tarefa 3: Tela de Valor — todos os eventos obrigatórios ──────────────────
// Eventos implementados:
//   onChanged (TextField de mensagem), onPressed (Continuar + Trocar),
//   onTap e onLongPress no avatar do destinatário (GestureDetector),
//   SnackBar para erros e AlertDialog para confirmação de remoção,
//   Encadeamento: Continuar → valida saldo → navega para confirmação

class PaymentAmountScreen extends StatefulWidget {
  const PaymentAmountScreen({super.key});

  @override
  State<PaymentAmountScreen> createState() => _PaymentAmountScreenState();
}

class _PaymentAmountScreenState extends State<PaymentAmountScreen> {
  User? _recipient;
  String _rawAmount = '0';
  final _messageCtrl = TextEditingController();
  String _messageError = '';

  @override
  void didChangeDependencies() {
    super.didChangeDependencies();
    _recipient ??= ModalRoute.of(context)!.settings.arguments as User?;
  }

  @override
  void dispose() {
    _messageCtrl.dispose();
    super.dispose();
  }

  double get _amount {
    final v = double.tryParse(_rawAmount) ?? 0;
    return v;
  }

  void _onKey(String key) {
    setState(() {
      if (key == '⌫') {
        if (_rawAmount.length > 1) {
          _rawAmount = _rawAmount.substring(0, _rawAmount.length - 1);
        } else {
          _rawAmount = '0';
        }
      } else if (key == '.') {
        if (!_rawAmount.contains('.')) _rawAmount += '.';
      } else {
        if (_rawAmount == '0') {
          _rawAmount = key;
        } else {
          // Limita 2 casas decimais
          final parts = _rawAmount.split('.');
          if (parts.length == 2 && parts[1].length >= 2) return;
          _rawAmount += key;
        }
      }
    });
  }

  void _addQuick(double v) {
    setState(() {
      final current = double.tryParse(_rawAmount) ?? 0;
      _rawAmount = (current + v).toString();
    });
  }

  void _setAll(double balance) {
    setState(() => _rawAmount = balance.toStringAsFixed(2));
  }

  // onChanged: valida tamanho e loga em tempo real
  void _onMessageChanged(String v) {
    // ignore: avoid_print — exibir input em tempo real é requisito da Tarefa 3
    print('[Mensagem digitada]: $v');
    if (v.length > 100) {
      setState(() => _messageError = 'Máximo de 100 caracteres');
    } else {
      setState(() => _messageError = '');
    }
  }

  void _showSnackBar(String msg) {
    ScaffoldMessenger.of(context).showSnackBar(
      SnackBar(content: Text(msg), behavior: SnackBarBehavior.floating),
    );
  }

  // Botão Continuar: onPressed — valida → navega (encadeamento de eventos)
  void _handleContinue() {
    if (_recipient == null) return;
    final balance = context.read<AuthProvider>().user?.balance ?? 0;

    if (_amount <= 0) {
      _showSnackBar('Informe um valor maior que zero');
      return;
    }
    if (_amount > balance) {
      // RN02 — saldo insuficiente
      Navigator.pushNamed(context, '/error/insufficient-balance');
      return;
    }

    // Encadeamento: navega para tela de confirmação
    Navigator.pushNamed(
      context,
      '/payment/confirm',
      arguments: {'recipient': _recipient, 'value': _amount},
    );
  }

  // Botão Trocar destinatário: comportamento diferente — volta e limpa estado
  void _handleChangeRecipient() {
    setState(() {
      _recipient = null;
      _rawAmount = '0';
      _messageCtrl.clear();
    });
    Navigator.pop(context);
  }

  // GestureDetector onTap — exibe SnackBar com dados do destinatário
  void _onRecipientTap() {
    if (_recipient == null) return;
    _showSnackBar('${_recipient!.name} • ${_recipient!.email}');
  }

  // GestureDetector onLongPress — exibe AlertDialog de confirmação
  Future<void> _onRecipientLongPress() async {
    final remove = await showDialog<bool>(
      context: context,
      builder: (ctx) => AlertDialog(
        title: const Text('Remover destinatário?'),
        content: Text('Deseja remover ${_recipient!.name} desta transferência?'),
        actions: [
          TextButton(
            onPressed: () => Navigator.pop(ctx, false),
            child: const Text('Cancelar'),
          ),
          TextButton(
            onPressed: () => Navigator.pop(ctx, true),
            child: const Text(
              'Remover',
              style: TextStyle(color: errorRed),
            ),
          ),
        ],
      ),
    );
    if (remove == true) _handleChangeRecipient();
  }

  @override
  Widget build(BuildContext context) {
    final user = context.watch<AuthProvider>().user;
    final balance = user?.balance ?? 0;
    final canContinue = _amount > 0 && _amount <= balance;

    return Scaffold(
      appBar: AppBar(
        title: const Text('Quanto enviar?'),
        leading: const BackButton(),
      ),
      body: SafeArea(
        child: SingleChildScrollView(
          padding: const EdgeInsets.all(24),
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              // Destinatário com GestureDetector (onTap + onLongPress)
              if (_recipient != null)
                Row(
                  children: [
                    GestureDetector(
                      onTap: _onRecipientTap,
                      onLongPress: _onRecipientLongPress,
                      child: CircleAvatar(
                        radius: 24,
                        backgroundColor: accentGreen.withValues(alpha: 0.2),
                        child: Text(
                          _recipient!.name[0].toUpperCase(),
                          style: const TextStyle(
                            color: primaryBlack,
                            fontWeight: FontWeight.bold,
                            fontSize: 18,
                          ),
                        ),
                      ),
                    ),
                    const SizedBox(width: 12),
                    Expanded(
                      child: Column(
                        crossAxisAlignment: CrossAxisAlignment.start,
                        children: [
                          Text(
                            _recipient!.name,
                            style: const TextStyle(
                              fontWeight: FontWeight.bold,
                              fontSize: 16,
                            ),
                          ),
                          Text(
                            _recipient!.email,
                            style: const TextStyle(
                              color: textSecondary,
                              fontSize: 13,
                            ),
                          ),
                        ],
                      ),
                    ),
                    // Botão Trocar destinatário — comportamento diferente
                    TextButton(
                      onPressed: _handleChangeRecipient,
                      child: const Text(
                        'Trocar',
                        style: TextStyle(color: accentGreen),
                      ),
                    ),
                  ],
                ),
              const SizedBox(height: 32),

              // Valor digitado
              Center(
                child: Text(
                  formatCurrency(_amount),
                  style: const TextStyle(
                    fontSize: 42,
                    fontWeight: FontWeight.bold,
                    color: textPrimary,
                  ),
                ),
              ),
              const SizedBox(height: 8),
              Center(
                child: Text(
                  'Saldo disponível: ${formatCurrency(balance)}',
                  style: const TextStyle(color: textSecondary, fontSize: 13),
                ),
              ),
              const SizedBox(height: 20),

              // Chips de valores rápidos
              Row(
                mainAxisAlignment: MainAxisAlignment.center,
                children: [
                  _QuickChip('+R\$10', () => _addQuick(10)),
                  const SizedBox(width: 8),
                  _QuickChip('+R\$50', () => _addQuick(50)),
                  const SizedBox(width: 8),
                  _QuickChip('+R\$100', () => _addQuick(100)),
                  const SizedBox(width: 8),
                  _QuickChip('Tudo', () => _setAll(balance)),
                ],
              ),
              const SizedBox(height: 20),

              // Teclado numérico customizado
              AmountKeypad(onKey: _onKey),
              const SizedBox(height: 20),

              // TextField de mensagem — onChanged (Tarefa 3)
              TextField(
                controller: _messageCtrl,
                onChanged: _onMessageChanged,
                maxLength: 110, // impede digitação mas o lógico bloqueia em 100
                decoration: InputDecoration(
                  labelText: 'Adicionar mensagem (opcional)',
                  errorText: _messageError.isNotEmpty ? _messageError : null,
                  prefixIcon: const Icon(Icons.message_outlined),
                ),
              ),
              const SizedBox(height: 24),

              // Botão Continuar — onPressed com condição
              SizedBox(
                width: double.infinity,
                height: 52,
                child: ElevatedButton(
                  onPressed: canContinue ? _handleContinue : null,
                  style: ElevatedButton.styleFrom(
                    backgroundColor: canContinue ? accentGreen : const Color(0xFFD1D5DB),
                    foregroundColor: Colors.white,
                    shape: RoundedRectangleBorder(
                      borderRadius: BorderRadius.circular(12),
                    ),
                  ),
                  child: const Text(
                    'Continuar',
                    style: TextStyle(fontSize: 16, fontWeight: FontWeight.w600),
                  ),
                ),
              ),
            ],
          ),
        ),
      ),
    );
  }
}

class _QuickChip extends StatelessWidget {
  final String label;
  final VoidCallback onTap;
  const _QuickChip(this.label, this.onTap);

  @override
  Widget build(BuildContext context) {
    return InkWell(
      onTap: onTap,
      borderRadius: BorderRadius.circular(100),
      child: Container(
        padding: const EdgeInsets.symmetric(horizontal: 12, vertical: 6),
        decoration: BoxDecoration(
          border: Border.all(color: accentGreen),
          borderRadius: BorderRadius.circular(100),
        ),
        child: Text(
          label,
          style: const TextStyle(
            color: accentGreen,
            fontWeight: FontWeight.w600,
            fontSize: 12,
          ),
        ),
      ),
    );
  }
}
