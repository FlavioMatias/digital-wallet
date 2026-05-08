import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import '../../core/theme/app_colors.dart';
import '../../data/models/user.dart';
import '../../providers/auth_provider.dart';
import '../../providers/user_provider.dart';

class PaymentRecipientScreen extends StatefulWidget {
  const PaymentRecipientScreen({super.key});

  @override
  State<PaymentRecipientScreen> createState() => _PaymentRecipientScreenState();
}

class _PaymentRecipientScreenState extends State<PaymentRecipientScreen> {
  final _searchCtrl = TextEditingController();
  String _filter = '';

  @override
  void initState() {
    super.initState();
    WidgetsBinding.instance.addPostFrameCallback((_) {
      final user = context.read<AuthProvider>().user;
      if (user != null) {
        context.read<UserProvider>().loadContacts(user.id!);
      }
    });
  }

  @override
  void dispose() {
    _searchCtrl.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    final contacts = context.watch<UserProvider>().contacts;
    final filtered = _filter.isEmpty
        ? contacts
        : contacts.where((u) {
            final q = _filter.toLowerCase();
            return u.name.toLowerCase().contains(q) ||
                u.email.toLowerCase().contains(q);
          }).toList();

    return Scaffold(
      appBar: AppBar(
        title: const Text('Para quem pagar?'),
        leading: const BackButton(),
      ),
      body: Column(
        children: [
          Padding(
            padding: const EdgeInsets.all(16),
            child: TextField(
              controller: _searchCtrl,
              onChanged: (v) => setState(() => _filter = v),
              decoration: const InputDecoration(
                hintText: 'Buscar por nome ou e-mail',
                prefixIcon: Icon(Icons.search, color: textSecondary),
              ),
            ),
          ),
          const Padding(
            padding: EdgeInsets.symmetric(horizontal: 16, vertical: 4),
            child: Align(
              alignment: Alignment.centerLeft,
              child: Text(
                'Recentes',
                style: TextStyle(
                  fontWeight: FontWeight.bold,
                  fontSize: 14,
                  color: textSecondary,
                ),
              ),
            ),
          ),
          Expanded(
            child: ListView.builder(
              itemCount: filtered.length,
              itemBuilder: (ctx, i) => _RecipientTile(user: filtered[i]),
            ),
          ),
        ],
      ),
    );
  }
}

class _RecipientTile extends StatelessWidget {
  final User user;
  const _RecipientTile({required this.user});

  @override
  Widget build(BuildContext context) {
    return ListTile(
      leading: CircleAvatar(
        backgroundColor: accentGreen.withValues(alpha: 0.2),
        child: Text(
          user.name[0].toUpperCase(),
          style: const TextStyle(
            color: primaryBlack,
            fontWeight: FontWeight.bold,
          ),
        ),
      ),
      title: Row(
        children: [
          Text(user.name, style: const TextStyle(fontWeight: FontWeight.w600)),
          if (user.isMerchant) ...[
            const SizedBox(width: 8),
            Container(
              padding: const EdgeInsets.symmetric(horizontal: 8, vertical: 2),
              decoration: BoxDecoration(
                color: mintGreen,
                borderRadius: BorderRadius.circular(100),
              ),
              child: const Text(
                'Lojista',
                style: TextStyle(fontSize: 11, color: primaryBlack),
              ),
            ),
          ],
        ],
      ),
      subtitle: Text(user.email),
      onTap: () {
        Navigator.pushNamed(context, '/payment/amount', arguments: user);
      },
    );
  }
}
