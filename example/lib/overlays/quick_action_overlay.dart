import 'dart:async';

import 'package:floaty_chathead/floaty_chathead.dart';
import 'package:flutter/material.dart';

/// Overlay widget that renders a vertical strip of quick-action buttons.
///
/// Demonstrates: [OverlayFlagMessage.clickThrough], [FloatyOverlay.updateFlag],
/// [FloatyOverlay.resizeContent], and [FloatyOverlay.onTapped].
class QuickActionOverlay extends StatefulWidget {
  const QuickActionOverlay({super.key});

  @override
  State<QuickActionOverlay> createState() => _QuickActionOverlayState();
}

class _QuickActionOverlayState extends State<QuickActionOverlay> {
  bool _expanded = true;
  late final StreamSubscription<void> _tapSub;

  static const _actions = [
    _Action(Icons.camera_alt, 'screenshot', Colors.blue),
    _Action(Icons.bookmark, 'bookmark', Colors.orange),
    _Action(Icons.share, 'share', Colors.green),
    _Action(Icons.settings, 'settings', Colors.purple),
  ];

  @override
  void initState() {
    super.initState();
    FloatyOverlay.setUp();
    FloatyOverlay.updateFlag(OverlayFlagMessage.clickThrough);

    _tapSub = FloatyOverlay.onTapped.listen((_) {
      setState(() => _expanded = !_expanded);
      if (_expanded) {
        FloatyOverlay.resizeContent(200, 260);
      } else {
        FloatyOverlay.resizeContent(60, 60);
      }
    });
  }

  void _onAction(String action) {
    FloatyOverlay.shareData({'action': action});
  }

  @override
  Widget build(BuildContext context) {
    if (!_expanded) {
      return const SizedBox.shrink();
    }

    return Material(
      color: Colors.transparent,
      child: Center(
        child: Column(
          mainAxisSize: MainAxisSize.min,
          children: [
            for (final action in _actions) ...[
              _ActionButton(
                icon: action.icon,
                color: action.color,
                onTap: () => _onAction(action.name),
              ),
              const SizedBox(height: 8),
            ],
            _ActionButton(
              icon: Icons.close,
              color: Colors.red,
              onTap: FloatyOverlay.closeOverlay,
            ),
          ],
        ),
      ),
    );
  }

  @override
  void dispose() {
    _tapSub.cancel();
    FloatyOverlay.dispose();
    super.dispose();
  }
}

class _Action {
  const _Action(this.icon, this.name, this.color);
  final IconData icon;
  final String name;
  final Color color;
}

class _ActionButton extends StatelessWidget {
  const _ActionButton({
    required this.icon,
    required this.color,
    required this.onTap,
  });

  final IconData icon;
  final Color color;
  final VoidCallback onTap;

  @override
  Widget build(BuildContext context) {
    return GestureDetector(
      onTap: onTap,
      child: Container(
        width: 48,
        height: 48,
        decoration: BoxDecoration(
          color: color,
          shape: BoxShape.circle,
          boxShadow: [
            BoxShadow(
              color: color.withValues(alpha: 0.4),
              blurRadius: 8,
              offset: const Offset(0, 2),
            ),
          ],
        ),
        child: Icon(icon, color: Colors.white, size: 22),
      ),
    );
  }
}
