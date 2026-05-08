import 'package:flutter/material.dart';
import '../../../core/theme/app_colors.dart';

class AmountKeypad extends StatelessWidget {
  final ValueChanged<String> onKey;

  const AmountKeypad({super.key, required this.onKey});

  @override
  Widget build(BuildContext context) {
    const keys = [
      '1', '2', '3',
      '4', '5', '6',
      '7', '8', '9',
      '.', '0', '⌫',
    ];

    return GridView.count(
      shrinkWrap: true,
      crossAxisCount: 3,
      childAspectRatio: 2.2,
      mainAxisSpacing: 4,
      crossAxisSpacing: 4,
      physics: const NeverScrollableScrollPhysics(),
      children: keys.map((k) => _KeyButton(label: k, onTap: () => onKey(k))).toList(),
    );
  }
}

class _KeyButton extends StatelessWidget {
  final String label;
  final VoidCallback onTap;
  const _KeyButton({required this.label, required this.onTap});

  @override
  Widget build(BuildContext context) {
    return InkWell(
      onTap: onTap,
      borderRadius: BorderRadius.circular(12),
      child: Container(
        decoration: BoxDecoration(
          color: const Color(0xFFF0F0F0),
          borderRadius: BorderRadius.circular(12),
        ),
        alignment: Alignment.center,
        child: Text(
          label,
          style: const TextStyle(
            fontSize: 20,
            fontWeight: FontWeight.w500,
            color: textPrimary,
          ),
        ),
      ),
    );
  }
}
