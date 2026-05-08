import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import 'core/theme/app_theme.dart';
import 'data/database/database_helper.dart';
import 'data/repositories/transaction_repository.dart';
import 'data/repositories/user_repository.dart';
import 'data/services/mock_api_service.dart';
import 'features/auth/login_screen.dart';
import 'features/dashboard/dashboard_screen.dart';
import 'features/errors/generic_error_screen.dart';
import 'features/errors/insufficient_balance_screen.dart';
import 'features/errors/merchant_blocked_screen.dart';
import 'features/errors/unauthorized_screen.dart';
import 'features/payment/payment_amount_screen.dart';
import 'features/payment/payment_confirm_screen.dart';
import 'features/payment/payment_recipient_screen.dart';
import 'features/payment/payment_success_screen.dart';
import 'providers/auth_provider.dart';
import 'providers/transaction_provider.dart';
import 'providers/user_provider.dart';

class App extends StatelessWidget {
  const App({super.key});

  @override
  Widget build(BuildContext context) {
    final db = DatabaseHelper();
    final userRepo = UserRepository(db);
    final txRepo = TransactionRepository(db);
    final api = MockApiService();

    return MultiProvider(
      providers: [
        ChangeNotifierProvider(create: (_) => AuthProvider(userRepo)..loadSession()),
        ChangeNotifierProvider(create: (_) => UserProvider(userRepo)),
        ChangeNotifierProvider(
          create: (_) => TransactionProvider(txRepo, userRepo, api),
        ),
      ],
      child: MaterialApp(
        title: 'Digital Wallet',
        debugShowCheckedModeBanner: false,
        theme: buildAppTheme(),
        initialRoute: '/login',
        routes: {
          '/login': (_) => const LoginScreen(),
          '/dashboard': (_) => const DashboardScreen(),
          '/payment/recipient': (_) => const PaymentRecipientScreen(),
          '/payment/amount': (_) => const PaymentAmountScreen(),
          '/payment/confirm': (_) => const PaymentConfirmScreen(),
          '/payment/success': (_) => const PaymentSuccessScreen(),
          '/error/insufficient-balance': (_) => const InsufficientBalanceScreen(),
          '/error/merchant-blocked': (_) => const MerchantBlockedScreen(),
          '/error/unauthorized': (_) => const UnauthorizedScreen(),
          '/error/generic': (_) => const GenericErrorScreen(),
        },
      ),
    );
  }
}
