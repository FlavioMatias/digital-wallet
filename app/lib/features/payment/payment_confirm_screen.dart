import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import '../../core/theme/app_colors.dart';
import '../../core/utils/currency_formatter.dart';
import '../../data/models/user.dart';
import '../../providers/auth_provider.dart';
import '../../providers/transaction_provider.dart';
import 'widgets/slide_to_confirm.dart';

class PaymentConfirmScreen extends StatefulWidget {
  const PaymentConfirmScreen({super.key});

  @override
  State<PaymentConfirmScreen> createState() => _PaymentConfirmScreenState();
}

class _PaymentConfirmScreenState extends State<PaymentConfirmScreen> {
  bool _processing = false;

  Future<void> _handleConfirm(User payer, User recipient, double value) async {
    setState(() => _processing = true);
    final txProvider = context.read<TransactionProvider>();

    final ok = await txProvider.transfer(
      payer: payer,
      payee: recipient,
      value: value,
    );

    if (!mounted) return;
    setState(() => _processing = false);

    if (ok) {
      context.read<AuthProvider>().refreshBalance(payer.balance - value);
      Navigator.pushNamedAndRemoveUntil(
        context,
        '/payment/success',
        (route) => route.settings.name == '/dashboard',
        arguments: {'recipient': recipient, 'value': value},
      );
    } else {
      if (txProvider.state == TransferState.denied) {
        Navigator.pushNamed(context, '/error/unauthorized');
      } else {
        Navigator.pushNamed(context, '/error/generic');
      }
    }
  }

  @override
  Widget build(BuildContext context) {
    final args = ModalRoute.of(context)!.settings.arguments as Map<String, dynamic>;
    final recipient = args['recipient'] as User;
    final value = args['value'] as double;
    final payer = context.watch<AuthProvider>().user!;

    final now = DateTime.now();
    final txId = 'TXN${now.millisecondsSinceEpoch}';

    return Scaffold(
      appBar: AppBar(
        title: const Text('Confirmar pagamento'),
        leading: const BackButton(),
      ),
      body: SafeArea(
        child: Padding(
          padding: const EdgeInsets.all(24),
          child: Column(
            children: [
              Container(
                padding: const EdgeInsets.symmetric(horizontal: 12, vertical: 6),
                decoration: BoxDecoration(
                  color: mintGreen,
                  borderRadius: BorderRadius.circular(100),
                ),
                child: const Text(
                  'Transação autorizada pelo sistema',
                  style: TextStyle(
                    color: primaryBlack,
                    fontWeight: FontWeight.w600,
                    fontSize: 12,
                  ),
                ),
              ),
              const SizedBox(height: 32),

              // Avatares pagador → beneficiário
              Row(
                mainAxisAlignment: MainAxisAlignment.center,
                children: [
                  _Avatar(name: payer.name),
                  Padding(
                    padding: const EdgeInsets.symmetric(horizontal: 12),
                    child: const Icon(
                      Icons.arrow_forward_rounded,
                      color: accentGreen,
                      size: 28,
                    ),
                  ),
                  _Avatar(name: recipient.name),
                ],
              ),
              const SizedBox(height: 24),

              Text(
                formatCurrency(value),
                style: const TextStyle(
                  fontSize: 40,
                  fontWeight: FontWeight.bold,
                  color: textPrimary,
                ),
              ),
              const SizedBox(height: 32),

              // Detalhes
              _DetailRow(label: 'De', value: payer.name),
              const Divider(height: 24),
              _DetailRow(label: 'Para', value: recipient.name),
              const Divider(height: 24),
              _DetailRow(
                label: 'Data',
                value: '${now.day.toString().padLeft(2, '0')}/${now.month.toString().padLeft(2, '0')}/${now.year}',
              ),
              const Divider(height: 24),
              const _DetailRow(label: 'Tarifa', value: 'Grátis'),
              const Divider(height: 24),
              _DetailRow(label: 'ID da transação', value: txId),

              const Spacer(),
              SlideToConfirm(
                loading: _processing,
                onConfirmed: () => _handleConfirm(payer, recipient, value),
              ),
              const SizedBox(height: 16),
            ],
          ),
        ),
      ),
    );
  }
}

class _Avatar extends StatelessWidget {
  final String name;
  const _Avatar({required this.name});

  @override
  Widget build(BuildContext context) {
    return Column(
      children: [
        CircleAvatar(
          radius: 32,
          backgroundColor: accentGreen.withValues(alpha: 0.2),
          child: Text(
            name[0].toUpperCase(),
            style: const TextStyle(
              color: primaryBlack,
              fontWeight: FontWeight.bold,
              fontSize: 22,
            ),
          ),
        ),
        const SizedBox(height: 6),
        Text(
          name.split(' ').first,
          style: const TextStyle(fontSize: 12, color: textSecondary),
        ),
      ],
    );
  }
}

class _DetailRow extends StatelessWidget {
  final String label;
  final String value;
  const _DetailRow({required this.label, required this.value});

  @override
  Widget build(BuildContext context) {
    return Row(
      mainAxisAlignment: MainAxisAlignment.spaceBetween,
      children: [
        Text(label, style: const TextStyle(color: textSecondary, fontSize: 14)),
        Text(
          value,
          style: const TextStyle(
            color: textPrimary,
            fontWeight: FontWeight.w600,
            fontSize: 14,
          ),
        ),
      ],
    );
  }
}
