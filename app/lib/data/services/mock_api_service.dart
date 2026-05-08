import 'dart:convert';
import 'package:flutter/services.dart';

// Altere para true para forçar resposta negada (útil para demonstrar tela 401)
const bool kForceAuthDenied = false;

class MockApiService {
  Future<bool> authorize() async {
    await Future.delayed(const Duration(milliseconds: 800));

    if (kForceAuthDenied) {
      final raw = await rootBundle.loadString('assets/mocks/authorizer_denied.json');
      final json = jsonDecode(raw) as Map<String, dynamic>;
      return json['authorized'] as bool;
    }

    final raw = await rootBundle.loadString('assets/mocks/authorizer_success.json');
    final json = jsonDecode(raw) as Map<String, dynamic>;
    return json['authorized'] as bool;
  }

  Future<void> sendNotification(int payeeId) async {
    try {
      await Future.delayed(const Duration(milliseconds: 500));
      await rootBundle.loadString('assets/mocks/notification_response.json');
    } catch (e) {
      // notificação resiliente: falha silenciosa
    }
  }
}
