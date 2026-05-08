import 'package:flutter/material.dart';
import '../../core/theme/app_colors.dart';

class UnauthorizedScreen extends StatelessWidget {
  const UnauthorizedScreen({super.key});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: SafeArea(
        child: Padding(
          padding: const EdgeInsets.all(32),
          child: Column(
            mainAxisAlignment: MainAxisAlignment.center,
            children: [
              const Icon(Icons.shield_outlined, color: errorRed, size: 64),
              const SizedBox(height: 24),
              const Text(
                '401 — Transação negada',
                style: TextStyle(
                  fontSize: 22,
                  fontWeight: FontWeight.bold,
                  color: textPrimary,
                ),
              ),
              const SizedBox(height: 12),
              const Text(
                'O autorizador externo negou esta transação. Tente novamente ou entre em contato com o suporte.',
                textAlign: TextAlign.center,
                style: TextStyle(color: textSecondary, fontSize: 15),
              ),
              const SizedBox(height: 40),
              SizedBox(
                width: double.infinity,
                height: 52,
                child: ElevatedButton(
                  onPressed: () => Navigator.pushNamedAndRemoveUntil(
                    context,
                    '/dashboard',
                    (route) => false,
                  ),
                  style: ElevatedButton.styleFrom(
                    backgroundColor: primaryBlack,
                    shape: RoundedRectangleBorder(
                      borderRadius: BorderRadius.circular(12),
                    ),
                  ),
                  child: const Text(
                    'Voltar ao início',
                    style: TextStyle(color: Colors.white, fontSize: 16),
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
