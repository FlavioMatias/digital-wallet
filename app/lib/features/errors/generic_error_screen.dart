import 'package:flutter/material.dart';
import '../../core/theme/app_colors.dart';

class GenericErrorScreen extends StatelessWidget {
  const GenericErrorScreen({super.key});

  @override
  Widget build(BuildContext context) {
    final requestId = 'REQ-${DateTime.now().millisecondsSinceEpoch}';
    return Scaffold(
      body: SafeArea(
        child: Padding(
          padding: const EdgeInsets.all(32),
          child: Column(
            mainAxisAlignment: MainAxisAlignment.center,
            children: [
              const Icon(Icons.error_outline_rounded, color: errorRed, size: 64),
              const SizedBox(height: 24),
              const Text(
                '500 — Erro interno',
                style: TextStyle(
                  fontSize: 22,
                  fontWeight: FontWeight.bold,
                  color: textPrimary,
                ),
              ),
              const SizedBox(height: 12),
              Text(
                'Algo deu errado.\nRequest ID: $requestId',
                textAlign: TextAlign.center,
                style: const TextStyle(color: textSecondary, fontSize: 14),
              ),
              const SizedBox(height: 40),
              SizedBox(
                width: double.infinity,
                height: 52,
                child: ElevatedButton(
                  onPressed: () => Navigator.pop(context),
                  style: ElevatedButton.styleFrom(
                    backgroundColor: primaryBlack,
                    shape: RoundedRectangleBorder(
                      borderRadius: BorderRadius.circular(12),
                    ),
                  ),
                  child: const Text(
                    'Tentar novamente',
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
