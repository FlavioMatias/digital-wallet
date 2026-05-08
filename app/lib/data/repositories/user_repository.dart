import '../database/database_helper.dart';
import '../models/user.dart';

class UserRepository {
  final DatabaseHelper _db;
  UserRepository(this._db);

  Future<User?> getLoggedUser() async {
    final db = await _db.database;
    final sessionRows = await db.query('session', where: 'id = 1');
    if (sessionRows.isEmpty) return null;

    final userId = sessionRows.first['user_id'] as int;
    final rows = await db.query('users', where: 'id = ?', whereArgs: [userId]);
    if (rows.isEmpty) return null;
    return User.fromMap(rows.first);
  }

  Future<List<User>> getAllContacts(int excludeUserId) async {
    final db = await _db.database;
    final rows = await db.query(
      'users',
      where: 'id != ?',
      whereArgs: [excludeUserId],
    );
    return rows.map(User.fromMap).toList();
  }

  Future<User?> getUserById(int id) async {
    final db = await _db.database;
    final rows = await db.query('users', where: 'id = ?', whereArgs: [id]);
    if (rows.isEmpty) return null;
    return User.fromMap(rows.first);
  }

  Future<void> updateBalance(int userId, double newBalance) async {
    final db = await _db.database;
    await db.update(
      'users',
      {'balance': newBalance},
      where: 'id = ?',
      whereArgs: [userId],
    );
  }
}
