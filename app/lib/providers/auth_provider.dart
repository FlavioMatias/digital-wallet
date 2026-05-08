import 'package:flutter/material.dart';
import '../data/models/user.dart';
import '../data/repositories/user_repository.dart';

class AuthProvider extends ChangeNotifier {
  final UserRepository _repo;
  AuthProvider(this._repo);

  User? _user;
  bool _loading = false;

  User? get user => _user;
  bool get loading => _loading;
  bool get isLoggedIn => _user != null;

  Future<void> loadSession() async {
    _loading = true;
    notifyListeners();
    try {
      _user = await _repo.getLoggedUser();
    } finally {
      _loading = false;
      notifyListeners();
    }
  }

  void refreshBalance(double newBalance) {
    if (_user == null) return;
    _user = _user!.copyWith(balance: newBalance);
    notifyListeners();
  }

  Future<bool> login(String email, String password) async {
    _loading = true;
    notifyListeners();
    try {
      // Simulação: qualquer email/senha aceito se o usuário existir no seed
      final allContacts = await _repo.getAllContacts(-1);
      final match = allContacts.cast<User?>().firstWhere(
        (u) => u!.email == email,
        orElse: () => null,
      );
      if (match != null) {
        _user = match;
        notifyListeners();
        return true;
      }
      // Fallback: carrega o usuário logado da sessão
      _user = await _repo.getLoggedUser();
      notifyListeners();
      return _user != null;
    } finally {
      _loading = false;
      notifyListeners();
    }
  }
}
