import 'dart:async';

import 'package:floaty_chathead/floaty_chathead.dart';
import 'package:flutter/material.dart';

import '../utils.dart';

/// Example: Notification counter badge.
///
/// Demonstrates reactive one-way data push to the overlay and small overlay size.
class NotificationCounterExample extends StatefulWidget {
  const NotificationCounterExample({super.key});

  @override
  State<NotificationCounterExample> createState() =>
      _NotificationCounterExampleState();
}

class _NotificationCounterExampleState
    extends State<NotificationCounterExample> {
  int _counter = 0;
  late final StreamSubscription<Object?> _sub;

  @override
  void initState() {
    super.initState();
    _sub = FloatyChathead.onData.listen((data) {
      if (data is Map && data['action'] == 'clear' && mounted) {
        setState(() => _counter = 0);
      }
    });
  }

  void _updateCounter(int value) {
    setState(() => _counter = value);
    FloatyChathead.shareData({'count': _counter});
  }

  Future<void> _launch() async {
    if (!await ensureOverlayPermission()) return;
    await FloatyChathead.showChatHead(
      entryPoint: 'counterOverlayMain',
      chatheadIconAsset: 'assets/chatheadIcon.png',
      closeIconAsset: 'assets/close.png',
      closeBackgroundAsset: 'assets/closeBg.png',
      notificationTitle: 'Counter Badge Active',
      notificationIconAsset: 'assets/notificationIcon.png',
      contentWidth: 140,
      contentHeight: 140,
    );
    // Push initial count.
    Future<void>.delayed(
      const Duration(milliseconds: 500),
      () => FloatyChathead.shareData({'count': _counter}),
    );
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: const Text('Notification Counter')),
      floatingActionButton: FloatingActionButton.extended(
        onPressed: _launch,
        icon: const Icon(Icons.notifications),
        label: const Text('Launch Badge'),
      ),
      body: Center(
        child: Column(
          mainAxisSize: MainAxisSize.min,
          children: [
            // Counter display
            Container(
              width: 120,
              height: 120,
              decoration: BoxDecoration(
                color: _counter > 0 ? Colors.red : Colors.grey.shade300,
                shape: BoxShape.circle,
              ),
              child: Center(
                child: Text(
                  '$_counter',
                  style: TextStyle(
                    fontSize: 48,
                    fontWeight: FontWeight.bold,
                    color: _counter > 0 ? Colors.white : Colors.grey,
                  ),
                ),
              ),
            ),
            const SizedBox(height: 32),
            // Controls
            Row(
              mainAxisAlignment: MainAxisAlignment.center,
              children: [
                FilledButton.tonal(
                  onPressed: () => _updateCounter(_counter - 1),
                  child: const Icon(Icons.remove),
                ),
                const SizedBox(width: 16),
                FilledButton.tonal(
                  onPressed: () => _updateCounter(0),
                  child: const Text('Reset'),
                ),
                const SizedBox(width: 16),
                FilledButton.tonal(
                  onPressed: () => _updateCounter(_counter + 1),
                  child: const Icon(Icons.add),
                ),
              ],
            ),
            const SizedBox(height: 16),
            Text(
              'Change the counter and watch the overlay badge update!',
              style: TextStyle(color: Colors.grey.shade600, fontSize: 12),
            ),
          ],
        ),
      ),
    );
  }

  @override
  void dispose() {
    _sub.cancel();
    FloatyChathead.closeChatHead();
    FloatyChathead.dispose();
    super.dispose();
  }
}
