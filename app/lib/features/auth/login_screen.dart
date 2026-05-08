import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import '../../core/theme/app_colors.dart';
import '../../providers/auth_provider.dart';
import '../../shared/widgets/app_button.dart';
import '../../shared/widgets/app_text_field.dart';

class LoginScreen extends StatefulWidget {
  const LoginScreen({super.key});

  @override
  State<LoginScreen> createState() => _LoginScreenState();
}

class _LoginScreenState extends State<LoginScreen> {
  final _emailCtrl = TextEditingController();
  final _passCtrl = TextEditingController();
  String? _emailError;

  @override
  void dispose() {
    _emailCtrl.dispose();
    _passCtrl.dispose();
    super.dispose();
  }

  bool _isValidEmail(String v) => v.contains('@') && v.contains('.');

  Future<void> _handleLogin() async {
    final email = _emailCtrl.text.trim();
    if (!_isValidEmail(email)) {
      setState(() => _emailError = 'E-mail inválido');
      return;
    }
    setState(() => _emailError = null);

    final auth = context.read<AuthProvider>();
    final ok = await auth.login(email, _passCtrl.text);
    if (!mounted) return;

    if (ok) {
      Navigator.pushReplacementNamed(context, '/dashboard');
    } else {
      ScaffoldMessenger.of(context).showSnackBar(
        const SnackBar(content: Text('Usuário não encontrado')),
      );
    }
  }

  @override
  Widget build(BuildContext context) {
    final auth = context.watch<AuthProvider>();
    return Scaffold(
      body: SafeArea(
        child: SingleChildScrollView(
          padding: const EdgeInsets.symmetric(horizontal: 24, vertical: 32),
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              const SizedBox(height: 24),
              Container(
                width: 56,
                height: 56,
                decoration: BoxDecoration(
                  color: primaryBlack,
                  borderRadius: BorderRadius.circular(16),
                ),
                child: const Icon(Icons.bolt, color: accentGreen, size: 32),
              ),
              const SizedBox(height: 32),
              const Text(
                'Bem-vindo\nde volta.',
                style: TextStyle(
                  fontSize: 32,
                  fontWeight: FontWeight.bold,
                  color: textPrimary,
                  height: 1.2,
                ),
              ),
              const SizedBox(height: 8),
              const Text(
                'Acesse sua carteira digital',
                style: TextStyle(fontSize: 15, color: textSecondary),
              ),
              const SizedBox(height: 40),
              AppTextField(
                label: 'E-mail',
                controller: _emailCtrl,
                keyboardType: TextInputType.emailAddress,
                prefixIcon: Icons.email_outlined,
                errorText: _emailError,
                onChanged: (_) => setState(() => _emailError = null),
              ),
              const SizedBox(height: 16),
              AppTextField(
                label: 'Senha',
                controller: _passCtrl,
                isPassword: true,
                prefixIcon: Icons.lock_outline,
              ),
              const SizedBox(height: 12),
              Align(
                alignment: Alignment.centerRight,
                child: TextButton(
                  onPressed: () {},
                  child: const Text(
                    'Esqueceu a senha?',
                    style: TextStyle(color: textSecondary),
                  ),
                ),
              ),
              const SizedBox(height: 24),
              AppButton(
                label: 'Entrar',
                onPressed: _handleLogin,
                loading: auth.loading,
              ),
              const SizedBox(height: 24),
              Row(
                children: [
                  const Expanded(child: Divider()),
                  Padding(
                    padding: const EdgeInsets.symmetric(horizontal: 16),
                    child: Text(
                      'ou',
                      style: TextStyle(color: textSecondary),
                    ),
                  ),
                  const Expanded(child: Divider()),
                ],
              ),
              const SizedBox(height: 24),
              OutlinedButton.icon(
                onPressed: () {},
                icon: const Icon(Icons.face, color: primaryBlack),
                label: const Text(
                  'Entrar com Face ID',
                  style: TextStyle(color: primaryBlack, fontWeight: FontWeight.w600),
                ),
                style: OutlinedButton.styleFrom(
                  minimumSize: const Size(double.infinity, 52),
                  side: const BorderSide(color: dividerColor),
                  shape: RoundedRectangleBorder(
                    borderRadius: BorderRadius.circular(12),
                  ),
                ),
              ),
              const SizedBox(height: 32),
              Center(
                child: Row(
                  mainAxisSize: MainAxisSize.min,
                  children: [
                    const Text(
                      'Não tem conta?',
                      style: TextStyle(color: textSecondary),
                    ),
                    TextButton(
                      onPressed: () {},
                      child: const Text(
                        'Criar agora',
                        style: TextStyle(
                          color: primaryBlack,
                          fontWeight: FontWeight.w600,
                        ),
                      ),
                    ),
                  ],
                ),
              ),
            ],
          ),
        ),
      ),
    );
  }
}
