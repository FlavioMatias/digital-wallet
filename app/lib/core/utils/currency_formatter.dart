import 'package:intl/intl.dart';

final _currencyFormat = NumberFormat.currency(locale: 'pt_BR', symbol: 'R\$');

String formatCurrency(double value) => _currencyFormat.format(value);
