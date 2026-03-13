import 'dart:async';

import 'package:floaty_chathead/floaty_chathead.dart';
import 'package:flutter/material.dart';

/// Overlay widget that displays a notification-style counter badge.
///
/// Demonstrates: reactive one-way data push from the main app,
/// small overlay size, and sending actions back.
class NotificationCounterOverlay extends StatefulWidget {
  const NotificationCounterOverlay({super.key});

  @override
  State<NotificationCounterOverlay> createState() =>
      _NotificationCounterOverlayState();
}

class _NotificationCounterOverlayState
    extends State<NotificationCounterOverlay> {
  int _count = 0;
  late final StreamSubscription<Object?> _sub;

  @override
  void initState() {
    super.initState();
    FloatyOverlay.setUp();
    _sub = FloatyOverlay.onData.listen((data) {
      if (data is Map && mounted) {
        final count = data['count'];
        if (count is int) {
          setState(() => _count = count);
        }
      }
    });
  }

  void _clear() {
    FloatyOverlay.shareData({'action': 'clear'});
    setState(() => _count = 0);
  }

  @override
  Widget build(BuildContext context) {
    return Material(
      color: Colors.transparent,
      child: Center(
        child: Column(
          mainAxisSize: MainAxisSize.min,
          children: [
            // Badge circle
            Container(
              width: 80,
              height: 80,
              decoration: BoxDecoration(
                color: _count > 0 ? Colors.red : Colors.grey,
                shape: BoxShape.circle,
                boxShadow: [
                  BoxShadow(
                    color: (_count > 0 ? Colors.red : Colors.grey)
                        .withValues(alpha: 0.5),
                    blurRadius: 12,
                    offset: const Offset(0, 4),
                  ),
                ],
              ),
              child: Center(
                child: Text(
                  '$_count',
                  style: const TextStyle(
                    color: Colors.white,
                    fontSize: 28,
                    fontWeight: FontWeight.bold,
                  ),
                ),
              ),
            ),
            const SizedBox(height: 8),
            // Action row
            Row(
              mainAxisSize: MainAxisSize.min,
              children: [
                GestureDetector(
                  onTap: _clear,
                  child: Container(
                    padding: const EdgeInsets.symmetric(
                      horizontal: 12,
                      vertical: 4,
                    ),
                    decoration: BoxDecoration(
                      color: Colors.white,
                      borderRadius: BorderRadius.circular(12),
                    ),
                    child: const Text(
                      'Clear',
                      style: TextStyle(fontSize: 12, color: Colors.red),
                    ),
                  ),
                ),
                const SizedBox(width: 8),
                GestureDetector(
                  onTap: FloatyOverlay.closeOverlay,
                  child: Container(
                    padding: const EdgeInsets.symmetric(
                      horizontal: 12,
                      vertical: 4,
                    ),
                    decoration: BoxDecoration(
                      color: Colors.white,
                      borderRadius: BorderRadius.circular(12),
                    ),
                    child: const Text(
                      'Close',
                      style: TextStyle(fontSize: 12, color: Colors.grey),
                    ),
                  ),
                ),
              ],
            ),
          ],
        ),
      ),
    );
  }

  @override
  void dispose() {
    _sub.cancel();
    FloatyOverlay.dispose();
    super.dispose();
  }
}
