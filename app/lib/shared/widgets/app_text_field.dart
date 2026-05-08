import 'package:flutter/material.dart';
import '../../core/theme/app_colors.dart';

class AppTextField extends StatefulWidget {
  final String label;
  final TextEditingController? controller;
  final bool isPassword;
  final TextInputType keyboardType;
  final String? errorText;
  final ValueChanged<String>? onChanged;
  final IconData? prefixIcon;
  final int? maxLength;

  const AppTextField({
    super.key,
    required this.label,
    this.controller,
    this.isPassword = false,
    this.keyboardType = TextInputType.text,
    this.errorText,
    this.onChanged,
    this.prefixIcon,
    this.maxLength,
  });

  @override
  State<AppTextField> createState() => _AppTextFieldState();
}

class _AppTextFieldState extends State<AppTextField> {
  bool _obscure = true;

  @override
  Widget build(BuildContext context) {
    return TextField(
      controller: widget.controller,
      obscureText: widget.isPassword && _obscure,
      keyboardType: widget.keyboardType,
      onChanged: widget.onChanged,
      maxLength: widget.maxLength,
      decoration: InputDecoration(
        labelText: widget.label,
        labelStyle: const TextStyle(color: textSecondary),
        errorText: widget.errorText,
        prefixIcon: widget.prefixIcon != null
            ? Icon(widget.prefixIcon, color: textSecondary)
            : null,
        suffixIcon: widget.isPassword
            ? IconButton(
                icon: Icon(
                  _obscure ? Icons.visibility_off : Icons.visibility,
                  color: textSecondary,
                ),
                onPressed: () => setState(() => _obscure = !_obscure),
              )
            : null,
      ),
    );
  }
}
