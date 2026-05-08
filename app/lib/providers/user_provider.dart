import 'package:flutter/material.dart';
import '../data/models/user.dart';
import '../data/repositories/user_repository.dart';

class UserProvider extends ChangeNotifier {
  final UserRepository _repo;
  UserProvider(this._repo);

  List<User> _contacts = [];
  List<User> get contacts => _contacts;

  Future<void> loadContacts(int excludeUserId) async {
    try {
      _contacts = await _repo.getAllContacts(excludeUserId);
      notifyListeners();
    } catch (_) {}
  }
}
