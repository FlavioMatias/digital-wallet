import 'package:flutter/material.dart';
import 'package:google_fonts/google_fonts.dart';
import 'app_colors.dart';

TextTheme buildTextTheme() {
  return GoogleFonts.interTextTheme(
    const TextTheme(
      displayLarge: TextStyle(
        fontSize: 32,
        fontWeight: FontWeight.bold,
        color: textPrimary,
      ),
      headlineMedium: TextStyle(
        fontSize: 24,
        fontWeight: FontWeight.bold,
        color: textPrimary,
      ),
      titleLarge: TextStyle(
        fontSize: 18,
        fontWeight: FontWeight.w600,
        color: textPrimary,
      ),
      bodyLarge: TextStyle(fontSize: 16, color: textPrimary),
      bodyMedium: TextStyle(fontSize: 14, color: textPrimary),
      bodySmall: TextStyle(fontSize: 12, color: textSecondary),
    ),
  );
}
