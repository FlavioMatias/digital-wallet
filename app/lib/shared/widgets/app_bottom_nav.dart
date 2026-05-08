import 'package:flutter/material.dart';
import '../../core/theme/app_colors.dart';

class AppBottomNav extends StatelessWidget {
  final int currentIndex;
  final ValueChanged<int> onTap;

  const AppBottomNav({
    super.key,
    required this.currentIndex,
    required this.onTap,
  });

  @override
  Widget build(BuildContext context) {
    return BottomNavigationBar(
      currentIndex: currentIndex,
      onTap: onTap,
      type: BottomNavigationBarType.fixed,
      backgroundColor: cardWhite,
      selectedItemColor: primaryBlack,
      unselectedItemColor: textSecondary,
      showUnselectedLabels: true,
      items: [
        const BottomNavigationBarItem(
          icon: Icon(Icons.home_outlined),
          activeIcon: Icon(Icons.home),
          label: 'Início',
        ),
        BottomNavigationBarItem(
          icon: Container(
            padding: const EdgeInsets.all(8),
            decoration: const BoxDecoration(
              color: accentGreen,
              shape: BoxShape.circle,
            ),
            child: const Icon(Icons.send_rounded, color: Colors.white, size: 20),
          ),
          label: 'Pagar',
        ),
        const BottomNavigationBarItem(
          icon: Icon(Icons.credit_card_outlined),
          activeIcon: Icon(Icons.credit_card),
          label: 'Cartões',
        ),
        const BottomNavigationBarItem(
          icon: Icon(Icons.person_outline),
          activeIcon: Icon(Icons.person),
          label: 'Perfil',
        ),
      ],
    );
  }
}
