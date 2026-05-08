import 'package:flutter/material.dart';
import '../../../core/theme/app_colors.dart';
import '../../../core/utils/currency_formatter.dart';

class BalanceCard extends StatelessWidget {
  final double balance;
  final bool compact;

  const BalanceCard({super.key, required this.balance, this.compact = false});

  @override
  Widget build(BuildContext context) {
    return Container(
      width: double.infinity,
      padding: EdgeInsets.all(compact ? 20 : 24),
      decoration: BoxDecoration(
        color: primaryBlack,
        borderRadius: BorderRadius.circular(20),
      ),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          const Text(
            'CONTA PESSOAL',
            style: TextStyle(
              color: Colors.white54,
              fontSize: 11,
              letterSpacing: 1.5,
              fontWeight: FontWeight.w600,
            ),
          ),
          const SizedBox(height: 4),
          const Text(
            'Saldo total',
            style: TextStyle(color: Colors.white70, fontSize: 13),
          ),
          const SizedBox(height: 8),
          Text(
            formatCurrency(balance),
            style: TextStyle(
              color: Colors.white,
              fontSize: compact ? 28 : 36,
              fontWeight: FontWeight.bold,
            ),
          ),
          SizedBox(height: compact ? 12 : 20),
          Row(
            mainAxisAlignment: MainAxisAlignment.spaceBetween,
            children: const [
              Text(
                'Ag 0001',
                style: TextStyle(color: Colors.white54, fontSize: 12),
              ),
              Text(
                'Conta 12345-6',
                style: TextStyle(color: Colors.white54, fontSize: 12),
              ),
            ],
          ),
        ],
      ),
    );
  }
}
