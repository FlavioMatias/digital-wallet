import 'package:flutter/material.dart';
import '../../../core/theme/app_colors.dart';

class QuickActionButton extends StatelessWidget {
  final IconData icon;
  final String label;
  final VoidCallback onTap;

  const QuickActionButton({
    super.key,
    required this.icon,
    required this.label,
    required this.onTap,
  });

  @override
  Widget build(BuildContext context) {
    return GestureDetector(
      onTap: onTap,
      child: Column(
        mainAxisSize: MainAxisSize.min,
        children: [
          Container(
            width: 56,
            height: 56,
            decoration: BoxDecoration(
              color: const Color(0xFFF0F0F0),
              borderRadius: BorderRadius.circular(16),
            ),
            child: Icon(icon, color: primaryBlack, size: 24),
          ),
          const SizedBox(height: 8),
          Text(
            label,
            style: const TextStyle(
              fontSize: 12,
              fontWeight: FontWeight.w500,
              color: textPrimary,
            ),
          ),
        ],
      ),
    );
  }
}

class QuickActionsRow extends StatelessWidget {
  final VoidCallback onPay;

  const QuickActionsRow({super.key, required this.onPay});

  @override
  Widget build(BuildContext context) {
    return Row(
      mainAxisAlignment: MainAxisAlignment.spaceAround,
      children: [
        QuickActionButton(icon: Icons.send_rounded, label: 'Pagar', onTap: onPay),
        QuickActionButton(
          icon: Icons.download_rounded,
          label: 'Receber',
          onTap: () {},
        ),
        QuickActionButton(
          icon: Icons.qr_code_scanner,
          label: 'QR Code',
          onTap: () {},
        ),
        QuickActionButton(
          icon: Icons.add_rounded,
          label: 'Adicionar',
          onTap: () {},
        ),
      ],
    );
  }
}
