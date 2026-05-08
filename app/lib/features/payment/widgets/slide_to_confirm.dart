import 'package:flutter/material.dart';
import '../../../core/theme/app_colors.dart';

class SlideToConfirm extends StatefulWidget {
  final VoidCallback onConfirmed;
  final bool loading;

  const SlideToConfirm({
    super.key,
    required this.onConfirmed,
    this.loading = false,
  });

  @override
  State<SlideToConfirm> createState() => _SlideToConfirmState();
}

class _SlideToConfirmState extends State<SlideToConfirm> {
  double _dx = 0;
  static const double _maxDx = 220;
  static const double _thumbSize = 52;

  void _onDragUpdate(DragUpdateDetails d) {
    if (widget.loading) return;
    setState(() {
      _dx = (_dx + d.delta.dx).clamp(0, _maxDx);
    });
  }

  void _onDragEnd(DragEndDetails _) {
    if (_dx >= _maxDx * 0.85) {
      widget.onConfirmed();
    } else {
      setState(() => _dx = 0);
    }
  }

  @override
  Widget build(BuildContext context) {
    return Container(
      height: 60,
      decoration: BoxDecoration(
        color: accentGreen.withValues(alpha: 0.15),
        borderRadius: BorderRadius.circular(30),
      ),
      child: Stack(
        alignment: Alignment.center,
        children: [
          const Text(
            'Deslize para confirmar',
            style: TextStyle(
              color: accentGreen,
              fontWeight: FontWeight.w600,
              fontSize: 14,
            ),
          ),
          Positioned(
            left: _dx + 4,
            child: GestureDetector(
              onHorizontalDragUpdate: _onDragUpdate,
              onHorizontalDragEnd: _onDragEnd,
              child: Container(
                width: _thumbSize,
                height: _thumbSize,
                decoration: const BoxDecoration(
                  color: accentGreen,
                  shape: BoxShape.circle,
                ),
                child: widget.loading
                    ? const Padding(
                        padding: EdgeInsets.all(14),
                        child: CircularProgressIndicator(
                          strokeWidth: 2,
                          color: Colors.white,
                        ),
                      )
                    : const Icon(
                        Icons.arrow_forward_rounded,
                        color: Colors.white,
                      ),
              ),
            ),
          ),
        ],
      ),
    );
  }
}
