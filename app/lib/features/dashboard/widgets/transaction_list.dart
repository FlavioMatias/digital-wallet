import 'package:flutter/material.dart';
import '../../../core/theme/app_colors.dart';
import '../../../core/utils/currency_formatter.dart';
import '../../../data/models/transaction.dart';

class TransactionTile extends StatelessWidget {
  final Transaction tx;
  final int currentUserId;

  const TransactionTile({
    super.key,
    required this.tx,
    required this.currentUserId,
  });

  bool get _isIncoming => tx.payeeId == currentUserId;

  @override
  Widget build(BuildContext context) {
    final name = _isIncoming ? (tx.payerName ?? 'Desconhecido') : (tx.payeeName ?? 'Desconhecido');
    final valueColor = _isIncoming ? successGreen : errorRed;
    final prefix = _isIncoming ? '+' : '-';
    final dateTime = DateTime.tryParse(tx.createdAt);
    final dateStr = dateTime != null
        ? '${dateTime.day.toString().padLeft(2, '0')}/${dateTime.month.toString().padLeft(2, '0')} ${dateTime.hour.toString().padLeft(2, '0')}:${dateTime.minute.toString().padLeft(2, '0')}'
        : '';

    return Padding(
      padding: const EdgeInsets.symmetric(vertical: 8),
      child: Row(
        children: [
          Container(
            width: 44,
            height: 44,
            decoration: BoxDecoration(
              color: const Color(0xFFF0F0F0),
              borderRadius: BorderRadius.circular(12),
            ),
            child: Center(
              child: Text(
                name.isNotEmpty ? name[0].toUpperCase() : '?',
                style: const TextStyle(
                  fontWeight: FontWeight.bold,
                  fontSize: 16,
                  color: textPrimary,
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
                  name,
                  style: const TextStyle(
                    fontWeight: FontWeight.w600,
                    fontSize: 14,
                    color: textPrimary,
                  ),
                ),
                const SizedBox(height: 2),
                Text(
                  dateStr,
                  style: const TextStyle(fontSize: 12, color: textSecondary),
                ),
              ],
            ),
          ),
          Text(
            '$prefix ${formatCurrency(tx.value)}',
            style: TextStyle(
              color: valueColor,
              fontWeight: FontWeight.bold,
              fontSize: 14,
            ),
          ),
        ],
      ),
    );
  }
}
