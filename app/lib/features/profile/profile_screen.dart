import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import '../../core/theme/app_colors.dart';
import '../../providers/auth_provider.dart';

class ProfileScreen extends StatelessWidget {
  const ProfileScreen({super.key});

  @override
  Widget build(BuildContext context) {
    final user = context.watch<AuthProvider>().user;

    return Scaffold(
      appBar: AppBar(title: const Text('Perfil')),
      body: Padding(
        padding: const EdgeInsets.all(24),
        child: Column(
          children: [
            const SizedBox(height: 16),
            CircleAvatar(
              radius: 48,
              backgroundColor: accentGreen,
              child: Text(
                user?.name[0].toUpperCase() ?? 'U',
                style: const TextStyle(
                  color: Colors.white,
                  fontSize: 36,
                  fontWeight: FontWeight.bold,
                ),
              ),
            ),
            const SizedBox(height: 16),
            Text(
              user?.name ?? '',
              style: const TextStyle(
                fontSize: 22,
                fontWeight: FontWeight.bold,
                color: textPrimary,
              ),
            ),
            const SizedBox(height: 4),
            Text(
              user?.email ?? '',
              style: const TextStyle(color: textSecondary),
            ),
            const SizedBox(height: 24),
            const Divider(),
            ListTile(
              leading: const Icon(Icons.badge_outlined),
              title: const Text('Documento'),
              subtitle: Text(user?.document ?? ''),
            ),
            ListTile(
              leading: const Icon(Icons.category_outlined),
              title: const Text('Tipo de conta'),
              subtitle: Text(user?.isMerchant == true ? 'Lojista' : 'Pessoa comum'),
            ),
          ],
        ),
      ),
    );
  }
}
