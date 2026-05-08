import '../database/database_helper.dart';
import '../models/transaction.dart';

class TransactionRepository {
  final DatabaseHelper _db;
  TransactionRepository(this._db);

  // Executa débito + crédito + insert atomicamente via db.transaction
  Future<Transaction> executeTransfer({
    required int payerId,
    required int payeeId,
    required double value,
  }) async {
    final db = await _db.database;
    late int insertedId;

    await db.transaction((txn) async {
      final payerRows = await txn.query(
        'users',
        where: 'id = ?',
        whereArgs: [payerId],
      );
      final payeeRows = await txn.query(
        'users',
        where: 'id = ?',
        whereArgs: [payeeId],
      );

      final payerBalance = (payerRows.first['balance'] as num).toDouble();
      final payeeBalance = (payeeRows.first['balance'] as num).toDouble();

      await txn.update(
        'users',
        {'balance': payerBalance - value},
        where: 'id = ?',
        whereArgs: [payerId],
      );

      await txn.update(
        'users',
        {'balance': payeeBalance + value},
        where: 'id = ?',
        whereArgs: [payeeId],
      );

      insertedId = await txn.insert('transactions', {
        'payer_id': payerId,
        'payee_id': payeeId,
        'value': value,
        'status': 'completed',
        'created_at': DateTime.now().toIso8601String(),
      });
    });

    return Transaction(
      id: insertedId,
      payerId: payerId,
      payeeId: payeeId,
      value: value,
      status: 'completed',
      createdAt: DateTime.now().toIso8601String(),
    );
  }

  Future<List<Transaction>> getRecentForUser(int userId, {int limit = 4}) async {
    final db = await _db.database;
    final rows = await db.rawQuery('''
      SELECT t.*,
             u1.name AS payer_name,
             u2.name AS payee_name
      FROM transactions t
      JOIN users u1 ON t.payer_id = u1.id
      JOIN users u2 ON t.payee_id = u2.id
      WHERE t.payer_id = ? OR t.payee_id = ?
      ORDER BY t.created_at DESC
      LIMIT ?
    ''', [userId, userId, limit]);
    return rows.map(Transaction.fromMap).toList();
  }
}
