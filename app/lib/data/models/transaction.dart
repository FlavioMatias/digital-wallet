class Transaction {
  final int? id;
  final int payerId;
  final int payeeId;
  final double value;
  final String status; // 'pending' | 'completed' | 'failed'
  final String createdAt;

  // Campos denormalizados para exibição (não persistidos)
  final String? payerName;
  final String? payeeName;

  const Transaction({
    this.id,
    required this.payerId,
    required this.payeeId,
    required this.value,
    required this.status,
    required this.createdAt,
    this.payerName,
    this.payeeName,
  });

  Map<String, dynamic> toMap() => {
    'id': id,
    'payer_id': payerId,
    'payee_id': payeeId,
    'value': value,
    'status': status,
    'created_at': createdAt,
  };

  factory Transaction.fromMap(Map<String, dynamic> map) => Transaction(
    id: map['id'] as int?,
    payerId: map['payer_id'] as int,
    payeeId: map['payee_id'] as int,
    value: (map['value'] as num).toDouble(),
    status: map['status'] as String,
    createdAt: map['created_at'] as String,
    payerName: map['payer_name'] as String?,
    payeeName: map['payee_name'] as String?,
  );
}
