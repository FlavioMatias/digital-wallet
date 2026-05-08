import 'package:flutter/material.dart';
import '../../core/theme/app_colors.dart';

class MerchantBlockedScreen extends StatelessWidget {
  const MerchantBlockedScreen({super.key});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: SafeArea(
        child: Padding(
          padding: const EdgeInsets.all(32),
          child: Column(
            mainAxisAlignment: MainAxisAlignment.center,
            children: [
              const Icon(Icons.block_rounded, color: errorRed, size: 64),
              const SizedBox(height: 24),
              const Text(
                '403 — Operação não permitida',
                style: TextStyle(
                  fontSize: 22,
                  fontWeight: FontWeight.bold,
                  color: textPrimary,
                ),
              ),
              const SizedBox(height: 12),
              const Text(
                'Lojistas não podem enviar transferências. Use o QR Code para receber pagamentos.',
                textAlign: TextAlign.center,
                style: TextStyle(color: textSecondary, fontSize: 15),
              ),
              const SizedBox(height: 40),
              SizedBox(
                width: double.infinity,
                height: 52,
                child: ElevatedButton(
                  onPressed: () => Navigator.pop(context),
                  style: ElevatedButton.styleFrom(
                    backgroundColor: accentGreen,
                    shape: RoundedRectangleBorder(
                      borderRadius: BorderRadius.circular(12),
                    ),
                  ),
                  child: const Text(
                    'Gerar QR Code para receber',
                    style: TextStyle(color: Colors.white, fontSize: 15),
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
