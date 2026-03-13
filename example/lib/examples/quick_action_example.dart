import 'dart:async';

import 'package:floaty_chathead/floaty_chathead.dart';
import 'package:flutter/material.dart';

import '../utils.dart';

/// Example: Quick-action floating buttons with click-through.
///
/// Demonstrates [OverlayFlagMessage.clickThrough], [resizeContent], and action logging.
class QuickActionExample extends StatefulWidget {
  const QuickActionExample({super.key});

  @override
  State<QuickActionExample> createState() => _QuickActionExampleState();
}

class _QuickActionExampleState extends State<QuickActionExample> {
  final _log = <_LogEntry>[];
  late final StreamSubscription<Object?> _sub;

  @override
  void initState() {
    super.initState();
    _sub = FloatyChathead.onData.listen((data) {
      if (data is Map && mounted) {
        final action = data['action'];
        if (action is String) {
          setState(() {
            _log.insert(
              0,
              _LogEntry(action: action, time: DateTime.now()),
            );
          });
        }
      }
    });
  }

  Future<void> _launch() async {
    if (!await ensureOverlayPermission()) return;
    await FloatyChathead.showChatHead(
      entryPoint: 'quickActionOverlayMain',
      chatheadIconAsset: 'assets/chatheadIcon.png',
      closeIconAsset: 'assets/close.png',
      closeBackgroundAsset: 'assets/closeBg.png',
      notificationTitle: 'Quick Actions Active',
      notificationIconAsset: 'assets/notificationIcon.png',
      contentWidth: 200,
      contentHeight: 260,
      flag: OverlayFlagMessage.clickThrough,
    );
  }

  String _formatTime(DateTime t) =>
      '${t.hour.toString().padLeft(2, '0')}:'
      '${t.minute.toString().padLeft(2, '0')}:'
      '${t.second.toString().padLeft(2, '0')}';

  IconData _iconForAction(String action) {
    return switch (action) {
      'screenshot' => Icons.camera_alt,
      'bookmark' => Icons.bookmark,
      'share' => Icons.share,
      'settings' => Icons.settings,
      _ => Icons.touch_app,
    };
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: const Text('Quick Actions')),
      floatingActionButton: FloatingActionButton.extended(
        onPressed: _launch,
        icon: const Icon(Icons.bolt),
        label: const Text('Launch'),
      ),
      body: _log.isEmpty
          ? const Center(
              child: Padding(
                padding: EdgeInsets.all(32),
                child: Column(
                  mainAxisSize: MainAxisSize.min,
                  children: [
                    Icon(Icons.touch_app, size: 64, color: Colors.grey),
                    SizedBox(height: 16),
                    Text(
                      'Launch the chathead, then tap the action buttons '
                      'in the overlay. Actions will be logged here.',
                      textAlign: TextAlign.center,
                      style: TextStyle(color: Colors.grey),
                    ),
                    SizedBox(height: 8),
                    Text(
                      'Click-through mode lets you interact with '
                      'apps behind the overlay!',
                      textAlign: TextAlign.center,
                      style: TextStyle(
                        color: Colors.grey,
                        fontStyle: FontStyle.italic,
                        fontSize: 12,
                      ),
                    ),
                  ],
                ),
              ),
            )
          : ListView.builder(
              padding: const EdgeInsets.all(16),
              itemCount: _log.length,
              itemBuilder: (_, i) {
                final entry = _log[i];
                return Card(
                  child: ListTile(
                    leading: Icon(_iconForAction(entry.action)),
                    title: Text(entry.action.toUpperCase()),
                    subtitle: Text(_formatTime(entry.time)),
                  ),
                );
              },
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

class _LogEntry {
  _LogEntry({required this.action, required this.time});
  final String action;
  final DateTime time;
}
