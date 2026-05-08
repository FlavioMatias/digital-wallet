import 'package:flutter/material.dart';
import '../../core/theme/app_colors.dart';

enum AppButtonVariant { primary, secondary, success }

class AppButton extends StatelessWidget {
  final String label;
  final VoidCallback? onPressed;
  final AppButtonVariant variant;
  final bool loading;

  const AppButton({
    super.key,
    required this.label,
    this.onPressed,
    this.variant = AppButtonVariant.primary,
    this.loading = false,
  });

  @override
  Widget build(BuildContext context) {
    final bg = switch (variant) {
      AppButtonVariant.primary => primaryBlack,
      AppButtonVariant.secondary => Colors.transparent,
      AppButtonVariant.success => accentGreen,
    };

    final fg = switch (variant) {
      AppButtonVariant.primary => Colors.white,
      AppButtonVariant.secondary => primaryBlack,
      AppButtonVariant.success => Colors.white,
    };

    final side = variant == AppButtonVariant.secondary
        ? const BorderSide(color: primaryBlack)
        : BorderSide.none;

    return SizedBox(
      width: double.infinity,
      height: 52,
      child: ElevatedButton(
        onPressed: loading ? null : onPressed,
        style: ElevatedButton.styleFrom(
          backgroundColor: bg,
          foregroundColor: fg,
          side: side,
          shape: RoundedRectangleBorder(
            borderRadius: BorderRadius.circular(12),
          ),
        ),
        child: loading
            ? const SizedBox(
                width: 20,
                height: 20,
                child: CircularProgressIndicator(
                  strokeWidth: 2,
                  color: Colors.white,
                ),
              )
            : Text(
                label,
                style: const TextStyle(
                  fontSize: 16,
                  fontWeight: FontWeight.w600,
                ),
              ),
      ),
    );
  }
}
