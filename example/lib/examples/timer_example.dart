import 'dart:async';

import 'package:floaty_chathead/floaty_chathead.dart';
import 'package:flutter/material.dart';

import '../utils.dart';

/// Example: Timer / Stopwatch in the overlay.
///
/// Demonstrates [resizeContent] for dynamic sizing, persistent overlay state,
/// and continuous bidirectional data streaming.
class TimerExample extends StatefulWidget {
  const TimerExample({super.key});

  @override
  State<TimerExample> createState() => _TimerExampleState();
}

class _TimerExampleState extends State<TimerExample> {
  int _elapsedMs = 0;
  bool _isRunning = false;
  late final StreamSubscription<Object?> _sub;

  @override
  void initState() {
    super.initState();
    _sub = FloatyChathead.onData.listen((data) {
      if (data is Map && mounted) {
        setState(() {
          if (data['elapsed'] is int) _elapsedMs = data['elapsed'] as int;
          if (data['isRunning'] is bool) _isRunning = data['isRunning'] as bool;
        });
      }
    });
  }

  void _sendCommand(String command) {
    FloatyChathead.shareData({'command': command});
  }

  Future<void> _launch() async {
    if (!await ensureOverlayPermission()) return;
    await FloatyChathead.showChatHead(
      entryPoint: 'timerOverlayMain',
      chatheadIconAsset: 'assets/chatheadIcon.png',
      closeIconAsset: 'assets/close.png',
      closeBackgroundAsset: 'assets/closeBg.png',
      notificationTitle: 'Timer Active',
      notificationIconAsset: 'assets/notificationIcon.png',
      contentWidth: 200,
      contentHeight: 120,
    );
  }

  String _formatDuration(int ms) {
    final d = Duration(milliseconds: ms);
    final minutes = d.inMinutes.remainder(60).toString().padLeft(2, '0');
    final seconds = d.inSeconds.remainder(60).toString().padLeft(2, '0');
    final tenths = (d.inMilliseconds.remainder(1000) ~/ 100).toString();
    return '$minutes:$seconds.$tenths';
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: const Text('Timer / Stopwatch')),
      floatingActionButton: FloatingActionButton.extended(
        onPressed: _launch,
        icon: const Icon(Icons.timer),
        label: const Text('Launch Timer'),
      ),
      body: Center(
        child: Column(
          mainAxisSize: MainAxisSize.min,
          children: [
            // Timer display (synced from overlay)
            Text(
              _formatDuration(_elapsedMs),
              style: const TextStyle(
                fontSize: 56,
                fontWeight: FontWeight.w300,
                fontFamily: 'monospace',
              ),
            ),
            const SizedBox(height: 8),
            Text(
              _isRunning ? 'Running' : 'Stopped',
              style: TextStyle(
                color: _isRunning ? Colors.green : Colors.grey,
                fontSize: 14,
              ),
            ),
            const SizedBox(height: 32),
            // Remote controls
            Wrap(
              spacing: 12,
              runSpacing: 12,
              alignment: WrapAlignment.center,
              children: [
                FilledButton.icon(
                  onPressed: () =>
                      _sendCommand(_isRunning ? 'pause' : 'start'),
                  icon: Icon(_isRunning ? Icons.pause : Icons.play_arrow),
                  label: Text(_isRunning ? 'Pause' : 'Start'),
                ),
                OutlinedButton.icon(
                  onPressed: () => _sendCommand('reset'),
                  icon: const Icon(Icons.stop),
                  label: const Text('Reset'),
                ),
                OutlinedButton.icon(
                  onPressed: () => _sendCommand('toggleSize'),
                  icon: const Icon(Icons.aspect_ratio),
                  label: const Text('Toggle Size'),
                ),
              ],
            ),
            const SizedBox(height: 24),
            Padding(
              padding: const EdgeInsets.symmetric(horizontal: 32),
              child: Text(
                'The timer runs inside the overlay. '
                'Use these buttons to control it remotely, or '
                'tap the chathead bubble to toggle start/pause.',
                textAlign: TextAlign.center,
                style: TextStyle(color: Colors.grey.shade600, fontSize: 12),
              ),
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
