import 'dart:async';

import 'package:floaty_chathead/floaty_chathead.dart';
import 'package:flutter/material.dart';

/// Overlay widget that runs a stopwatch timer.
///
/// Demonstrates: [FloatyOverlay.resizeContent] for dynamic sizing,
/// [Timer.periodic] for persistent overlay state,
/// [FloatyOverlay.onTapped] for quick toggle, and continuous data streaming.
class TimerOverlay extends StatefulWidget {
  const TimerOverlay({super.key});

  @override
  State<TimerOverlay> createState() => _TimerOverlayState();
}

class _TimerOverlayState extends State<TimerOverlay> {
  final _stopwatch = Stopwatch();
  Timer? _ticker;
  bool _isExpanded = false;
  final _laps = <Duration>[];
  late final StreamSubscription<Object?> _dataSub;
  late final StreamSubscription<void> _tapSub;

  @override
  void initState() {
    super.initState();
    FloatyOverlay.setUp();

    _dataSub = FloatyOverlay.onData.listen((data) {
      if (data is Map && mounted) {
        switch (data['command']) {
          case 'start':
            _start();
          case 'pause':
            _pause();
          case 'reset':
            _reset();
          case 'toggleSize':
            _toggleSize();
        }
      }
    });

    _tapSub = FloatyOverlay.onTapped.listen((_) {
      _stopwatch.isRunning ? _pause() : _start();
    });
  }

  void _start() {
    if (_stopwatch.isRunning) return;
    _stopwatch.start();
    _ticker = Timer.periodic(const Duration(milliseconds: 100), (_) {
      if (mounted) setState(() {});
      FloatyOverlay.shareData({
        'elapsed': _stopwatch.elapsedMilliseconds,
        'isRunning': true,
      });
    });
  }

  void _pause() {
    _stopwatch.stop();
    _ticker?.cancel();
    if (mounted) setState(() {});
    FloatyOverlay.shareData({
      'elapsed': _stopwatch.elapsedMilliseconds,
      'isRunning': false,
    });
  }

  void _reset() {
    _stopwatch
      ..stop()
      ..reset();
    _ticker?.cancel();
    _laps.clear();
    if (mounted) setState(() {});
    FloatyOverlay.shareData({'elapsed': 0, 'isRunning': false});
  }

  void _lap() {
    if (_stopwatch.isRunning) {
      setState(() => _laps.add(_stopwatch.elapsed));
    }
  }

  void _toggleSize() {
    setState(() => _isExpanded = !_isExpanded);
    if (_isExpanded) {
      FloatyOverlay.resizeContent(280, 300);
    } else {
      FloatyOverlay.resizeContent(200, 120);
    }
  }

  String _formatDuration(Duration d) {
    final minutes = d.inMinutes.remainder(60).toString().padLeft(2, '0');
    final seconds = d.inSeconds.remainder(60).toString().padLeft(2, '0');
    final tenths = (d.inMilliseconds.remainder(1000) ~/ 100).toString();
    return '$minutes:$seconds.$tenths';
  }

  @override
  Widget build(BuildContext context) {
    final elapsed = _stopwatch.elapsed;

    return Material(
      color: Colors.transparent,
      child: Center(
        child: Card(
          margin: const EdgeInsets.all(8),
          color: Colors.blueGrey.shade800,
          child: Padding(
            padding: const EdgeInsets.all(12),
            child: Column(
              mainAxisSize: MainAxisSize.min,
              children: [
                // Timer display
                Text(
                  _formatDuration(elapsed),
                  style: const TextStyle(
                    color: Colors.white,
                    fontSize: 32,
                    fontWeight: FontWeight.w300,
                    fontFamily: 'monospace',
                  ),
                ),
                const SizedBox(height: 8),
                // Controls
                Row(
                  mainAxisSize: MainAxisSize.min,
                  children: [
                    _TimerButton(
                      icon: _stopwatch.isRunning
                          ? Icons.pause
                          : Icons.play_arrow,
                      onTap: _stopwatch.isRunning ? _pause : _start,
                    ),
                    const SizedBox(width: 12),
                    _TimerButton(icon: Icons.stop, onTap: _reset),
                    const SizedBox(width: 12),
                    _TimerButton(icon: Icons.flag, onTap: _lap),
                    const SizedBox(width: 12),
                    _TimerButton(
                      icon: _isExpanded
                          ? Icons.unfold_less
                          : Icons.unfold_more,
                      onTap: _toggleSize,
                    ),
                  ],
                ),
                // Laps (only in expanded mode)
                if (_isExpanded && _laps.isNotEmpty) ...[
                  const SizedBox(height: 8),
                  const Divider(color: Colors.white24),
                  SizedBox(
                    height: 120,
                    child: ListView.builder(
                      itemCount: _laps.length,
                      itemBuilder: (_, i) => Padding(
                        padding: const EdgeInsets.symmetric(vertical: 2),
                        child: Row(
                          mainAxisSize: MainAxisSize.min,
                          children: [
                            Text(
                              'Lap ${i + 1}',
                              style: TextStyle(
                                color: Colors.grey.shade400,
                                fontSize: 12,
                              ),
                            ),
                            const SizedBox(width: 12),
                            Text(
                              _formatDuration(_laps[i]),
                              style: const TextStyle(
                                color: Colors.white,
                                fontSize: 12,
                                fontFamily: 'monospace',
                              ),
                            ),
                          ],
                        ),
                      ),
                    ),
                  ),
                ],
                const SizedBox(height: 4),
                GestureDetector(
                  onTap: FloatyOverlay.closeOverlay,
                  child: Text(
                    'Close',
                    style: TextStyle(
                      color: Colors.grey.shade500,
                      fontSize: 11,
                    ),
                  ),
                ),
              ],
            ),
          ),
        ),
      ),
    );
  }

  @override
  void dispose() {
    _ticker?.cancel();
    _dataSub.cancel();
    _tapSub.cancel();
    FloatyOverlay.dispose();
    super.dispose();
  }
}

class _TimerButton extends StatelessWidget {
  const _TimerButton({required this.icon, required this.onTap});

  final IconData icon;
  final VoidCallback onTap;

  @override
  Widget build(BuildContext context) {
    return GestureDetector(
      onTap: onTap,
      child: Container(
        width: 36,
        height: 36,
        decoration: BoxDecoration(
          color: Colors.white.withValues(alpha: 0.15),
          shape: BoxShape.circle,
        ),
        child: Icon(icon, color: Colors.white, size: 18),
      ),
    );
  }
}
