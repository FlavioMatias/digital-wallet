import 'package:sqflite/sqflite.dart';
import 'package:path/path.dart';

class DatabaseHelper {
  static final DatabaseHelper _instance = DatabaseHelper._internal();
  factory DatabaseHelper() => _instance;
  DatabaseHelper._internal();

  Database? _db;

  Future<Database> get database async {
    _db ??= await _initDatabase();
    return _db!;
  }

  Future<Database> _initDatabase() async {
    final path = join(await getDatabasesPath(), 'digital_wallet.db');
    return openDatabase(
      path,
      version: 1,
      onCreate: _onCreate,
    );
  }

  Future<void> _onCreate(Database db, int version) async {
    await db.execute('''
      CREATE TABLE users (
        id INTEGER PRIMARY KEY AUTOINCREMENT,
        name TEXT NOT NULL,
        email TEXT UNIQUE NOT NULL,
        document TEXT UNIQUE NOT NULL,
        type TEXT NOT NULL,
        balance REAL NOT NULL DEFAULT 0,
        created_at TEXT NOT NULL
      )
    ''');

    await db.execute('''
      CREATE TABLE transactions (
        id INTEGER PRIMARY KEY AUTOINCREMENT,
        payer_id INTEGER NOT NULL,
        payee_id INTEGER NOT NULL,
        value REAL NOT NULL,
        status TEXT NOT NULL,
        created_at TEXT NOT NULL,
        FOREIGN KEY (payer_id) REFERENCES users(id),
        FOREIGN KEY (payee_id) REFERENCES users(id)
      )
    ''');

    await db.execute('''
      CREATE TABLE session (
        id INTEGER PRIMARY KEY,
        user_id INTEGER NOT NULL,
        logged_at TEXT NOT NULL
      )
    ''');

    await _seedData(db);
  }

  Future<void> _seedData(Database db) async {
    final now = DateTime.now().toIso8601String();

    // Usuário logado
    await db.insert('users', {
      'name': 'Flavio Matias',
      'email': 'flavio@example.com',
      'document': '123.456.789-00',
      'type': 'common',
      'balance': 527.44,
      'created_at': now,
    });

    // Contatos
    await db.insert('users', {
      'name': 'Ana Silva',
      'email': 'ana@example.com',
      'document': '234.567.890-11',
      'type': 'common',
      'balance': 1200.00,
      'created_at': now,
    });

    await db.insert('users', {
      'name': 'Carlos Mendes',
      'email': 'carlos@example.com',
      'document': '345.678.901-22',
      'type': 'common',
      'balance': 350.00,
      'created_at': now,
    });

    await db.insert('users', {
      'name': 'Beatriz Costa',
      'email': 'bia@example.com',
      'document': '456.789.012-33',
      'type': 'common',
      'balance': 800.00,
      'created_at': now,
    });

    await db.insert('users', {
      'name': 'Padaria Bom Sabor',
      'email': 'padaria@example.com',
      'document': '12.345.678/0001-90',
      'type': 'merchant',
      'balance': 5000.00,
      'created_at': now,
    });

    await db.insert('users', {
      'name': 'Pedro Alves',
      'email': 'pedro@example.com',
      'document': '567.890.123-44',
      'type': 'common',
      'balance': 200.00,
      'created_at': now,
    });

    // Sessão: usuário 1 logado
    await db.insert('session', {
      'id': 1,
      'user_id': 1,
      'logged_at': now,
    });

    // Transações históricas
    final d1 = DateTime.now().subtract(const Duration(hours: 2)).toIso8601String();
    final d2 = DateTime.now().subtract(const Duration(hours: 5)).toIso8601String();
    final d3 = DateTime.now().subtract(const Duration(days: 1)).toIso8601String();
    final d4 = DateTime.now().subtract(const Duration(days: 2)).toIso8601String();

    await db.insert('transactions', {
      'payer_id': 2,
      'payee_id': 1,
      'value': 150.00,
      'status': 'completed',
      'created_at': d1,
    });

    await db.insert('transactions', {
      'payer_id': 1,
      'payee_id': 5,
      'value': 35.50,
      'status': 'completed',
      'created_at': d2,
    });

    await db.insert('transactions', {
      'payer_id': 3,
      'payee_id': 1,
      'value': 80.00,
      'status': 'completed',
      'created_at': d3,
    });

    await db.insert('transactions', {
      'payer_id': 1,
      'payee_id': 2,
      'value': 50.00,
      'status': 'completed',
      'created_at': d4,
    });
  }
}
