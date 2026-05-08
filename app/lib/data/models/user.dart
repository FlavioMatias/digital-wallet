class User {
  final int? id;
  final String name;
  final String email;
  final String document;
  final String type; // 'common' | 'merchant'
  final double balance;
  final String createdAt;

  const User({
    this.id,
    required this.name,
    required this.email,
    required this.document,
    required this.type,
    required this.balance,
    required this.createdAt,
  });

  bool get isMerchant => type == 'merchant';

  Map<String, dynamic> toMap() => {
    'id': id,
    'name': name,
    'email': email,
    'document': document,
    'type': type,
    'balance': balance,
    'created_at': createdAt,
  };

  factory User.fromMap(Map<String, dynamic> map) => User(
    id: map['id'] as int?,
    name: map['name'] as String,
    email: map['email'] as String,
    document: map['document'] as String,
    type: map['type'] as String,
    balance: (map['balance'] as num).toDouble(),
    createdAt: map['created_at'] as String,
  );

  User copyWith({double? balance}) => User(
    id: id,
    name: name,
    email: email,
    document: document,
    type: type,
    balance: balance ?? this.balance,
    createdAt: createdAt,
  );
}
