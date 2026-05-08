import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import '../../core/theme/app_colors.dart';
import '../../providers/auth_provider.dart';
import '../../providers/transaction_provider.dart';
import '../../shared/widgets/app_bottom_nav.dart';
import 'widgets/balance_card.dart';
import 'widgets/quick_actions.dart';
import 'widgets/transaction_list.dart';

class DashboardScreen extends StatefulWidget {
  const DashboardScreen({super.key});

  @override
  State<DashboardScreen> createState() => _DashboardScreenState();
}

class _DashboardScreenState extends State<DashboardScreen> {
  int _navIndex = 0;

  @override
  void initState() {
    super.initState();
    WidgetsBinding.instance.addPostFrameCallback((_) {
      final user = context.read<AuthProvider>().user;
      if (user != null) {
        context.read<TransactionProvider>().loadRecent(user.id!);
      }
    });
  }

  void _onNavTap(int index) {
    if (index == 1) {
      _navigateToPay();
      return;
    }
    setState(() => _navIndex = index);
  }

  void _navigateToPay() {
    final user = context.read<AuthProvider>().user;
    if (user == null) return;

    // RN03 — lojista não pode enviar
    if (user.isMerchant) {
      Navigator.pushNamed(context, '/error/merchant-blocked');
      return;
    }
    Navigator.pushNamed(context, '/payment/recipient');
  }

  @override
  Widget build(BuildContext context) {
    final user = context.watch<AuthProvider>().user;
    if (user == null) return const SizedBox.shrink();

    return Scaffold(
      body: LayoutBuilder(
        builder: (context, constraints) {
          if (constraints.maxWidth >= 600) {
            return _TabletLayout(user: user, onPay: _navigateToPay);
          }
          return _MobileLayout(user: user, onPay: _navigateToPay);
        },
      ),
      bottomNavigationBar: AppBottomNav(
        currentIndex: _navIndex,
        onTap: _onNavTap,
      ),
    );
  }
}

// ── Layout mobile (< 600px) — vertical ────────────────────────────────────────
class _MobileLayout extends StatelessWidget {
  final dynamic user;
  final VoidCallback onPay;

  const _MobileLayout({required this.user, required this.onPay});

  @override
  Widget build(BuildContext context) {
    final txProvider = context.watch<TransactionProvider>();

    return SafeArea(
      child: SingleChildScrollView(
        padding: const EdgeInsets.symmetric(horizontal: 24),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            const SizedBox(height: 20),
            _Header(user: user),
            const SizedBox(height: 24),
            BalanceCard(balance: user.balance),
            const SizedBox(height: 28),
            QuickActionsRow(onPay: onPay),
            const SizedBox(height: 28),
            _RecentHeader(),
            const SizedBox(height: 12),
            _TransactionListSection(
              txProvider: txProvider,
              userId: user.id!,
            ),
            const SizedBox(height: 20),
          ],
        ),
      ),
    );
  }
}

// ── Layout tablet/desktop (>= 600px) — duas colunas ──────────────────────────
class _TabletLayout extends StatelessWidget {
  final dynamic user;
  final VoidCallback onPay;

  const _TabletLayout({required this.user, required this.onPay});

  @override
  Widget build(BuildContext context) {
    final txProvider = context.watch<TransactionProvider>();

    return SafeArea(
      child: Padding(
        padding: const EdgeInsets.symmetric(horizontal: 32, vertical: 20),
        child: Column(
          children: [
            _Header(user: user),
            const SizedBox(height: 24),
            Expanded(
              child: Row(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  // Coluna esquerda: card + atalhos
                  Expanded(
                    flex: 2,
                    child: Column(
                      children: [
                        BalanceCard(balance: user.balance),
                        const SizedBox(height: 24),
                        QuickActionsRow(onPay: onPay),
                      ],
                    ),
                  ),
                  const SizedBox(width: 32),
                  // Coluna direita: transações
                  Expanded(
                    flex: 3,
                    child: Column(
                      crossAxisAlignment: CrossAxisAlignment.start,
                      children: [
                        _RecentHeader(),
                        const SizedBox(height: 12),
                        Expanded(
                          child: _TransactionListSection(
                            txProvider: txProvider,
                            userId: user.id!,
                            scrollable: true,
                          ),
                        ),
                      ],
                    ),
                  ),
                ],
              ),
            ),
          ],
        ),
      ),
    );
  }
}

class _Header extends StatelessWidget {
  final dynamic user;
  const _Header({required this.user});

  String _greeting() {
    final h = DateTime.now().hour;
    if (h < 12) return 'Bom dia';
    if (h < 18) return 'Boa tarde';
    return 'Boa noite';
  }

  @override
  Widget build(BuildContext context) {
    return Row(
      children: [
        Container(
          width: 44,
          height: 44,
          decoration: const BoxDecoration(
            color: accentGreen,
            shape: BoxShape.circle,
          ),
          child: Center(
            child: Text(
              user.name.isNotEmpty ? user.name[0].toUpperCase() : 'U',
              style: const TextStyle(
                color: Colors.white,
                fontWeight: FontWeight.bold,
                fontSize: 18,
              ),
            ),
          ),
        ),
        const SizedBox(width: 12),
        Expanded(
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              Text(
                _greeting(),
                style: const TextStyle(fontSize: 12, color: textSecondary),
              ),
              Text(
                user.name.split(' ').first,
                style: const TextStyle(
                  fontSize: 16,
                  fontWeight: FontWeight.bold,
                  color: textPrimary,
                ),
              ),
            ],
          ),
        ),
        Stack(
          children: [
            const Icon(Icons.notifications_none_rounded, size: 28, color: textPrimary),
            Positioned(
              right: 0,
              top: 0,
              child: Container(
                width: 8,
                height: 8,
                decoration: const BoxDecoration(
                  color: accentGreen,
                  shape: BoxShape.circle,
                ),
              ),
            ),
          ],
        ),
      ],
    );
  }
}

class _RecentHeader extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return Row(
      mainAxisAlignment: MainAxisAlignment.spaceBetween,
      children: [
        const Text(
          'Atividade recente',
          style: TextStyle(
            fontSize: 16,
            fontWeight: FontWeight.bold,
            color: textPrimary,
          ),
        ),
        TextButton(
          onPressed: () {},
          child: const Text(
            'Ver tudo',
            style: TextStyle(
              color: accentGreen,
              fontWeight: FontWeight.w600,
            ),
          ),
        ),
      ],
    );
  }
}

class _TransactionListSection extends StatelessWidget {
  final TransactionProvider txProvider;
  final int userId;
  final bool scrollable;

  const _TransactionListSection({
    required this.txProvider,
    required this.userId,
    this.scrollable = false,
  });

  @override
  Widget build(BuildContext context) {
    if (txProvider.recent.isEmpty) {
      return const Center(
        child: Padding(
          padding: EdgeInsets.all(24),
          child: Text(
            'Nenhuma transação ainda',
            style: TextStyle(color: textSecondary),
          ),
        ),
      );
    }

    final list = Column(
      children: txProvider.recent
          .map((tx) => TransactionTile(tx: tx, currentUserId: userId))
          .toList(),
    );

    if (scrollable) {
      return SingleChildScrollView(child: list);
    }
    return list;
  }
}
