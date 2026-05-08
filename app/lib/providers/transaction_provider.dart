import 'package:flutter/material.dart';
import '../data/models/transaction.dart';
import '../data/models/user.dart';
import '../data/repositories/transaction_repository.dart';
import '../data/repositories/user_repository.dart';
import '../data/services/mock_api_service.dart';

enum TransferState { idle, loading, authorized, denied, error }

class TransactionProvider extends ChangeNotifier {
  final TransactionRepository _txRepo;
  final UserRepository _userRepo;
  final MockApiService _api;

  TransactionProvider(this._txRepo, this._userRepo, this._api);

  List<Transaction> _recent = [];
  TransferState _state = TransferState.idle;
  String? _errorMessage;

  List<Transaction> get recent => _recent;
  TransferState get state => _state;
  String? get errorMessage => _errorMessage;

  Future<void> loadRecent(int userId) async {
    try {
      _recent = await _txRepo.getRecentForUser(userId);
      notifyListeners();
    } catch (e) {
      _errorMessage = e.toString();
      notifyListeners();
    }
  }

  Future<bool> transfer({
    required User payer,
    required User payee,
    required double value,
  }) async {
    _state = TransferState.loading;
    _errorMessage = null;
    notifyListeners();

    try {
      final authorized = await _api.authorize();
      if (!authorized) {
        _state = TransferState.denied;
        notifyListeners();
        return false;
      }

      await _txRepo.executeTransfer(
        payerId: payer.id!,
        payeeId: payee.id!,
        value: value,
      );

      // RN06 — notificação resiliente, sem await no fluxo principal
      _api.sendNotification(payee.id!);

      await _userRepo.updateBalance(payer.id!, payer.balance - value);
      await loadRecent(payer.id!);

      _state = TransferState.authorized;
      notifyListeners();
      return true;
    } catch (e) {
      _state = TransferState.error;
      _errorMessage = e.toString();
      notifyListeners();
      return false;
    }
  }

  void reset() {
    _state = TransferState.idle;
    _errorMessage = null;
    notifyListeners();
  }
}
