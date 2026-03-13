import 'dart:async';

import 'package:floaty_chathead/floaty_chathead.dart';
import 'package:flutter/material.dart';

/// Overlay widget that renders compact media-player transport controls.
///
/// Demonstrates: [FloatyOverlay.onTapped] to toggle play/pause,
/// bidirectional state sync via [FloatyOverlay.shareData] / [FloatyOverlay.onData].
class MiniPlayerOverlay extends StatefulWidget {
  const MiniPlayerOverlay({super.key});

  @override
  State<MiniPlayerOverlay> createState() => _MiniPlayerOverlayState();
}

class _MiniPlayerOverlayState extends State<MiniPlayerOverlay> {
  String _title = 'No track';
  String _artist = '';
  bool _isPlaying = false;
  late final StreamSubscription<Object?> _dataSub;
  late final StreamSubscription<void> _tapSub;

  @override
  void initState() {
    super.initState();
    FloatyOverlay.setUp();

    _dataSub = FloatyOverlay.onData.listen((data) {
      if (data is Map && mounted) {
        setState(() {
          _title = '${data['title'] ?? _title}';
          _artist = '${data['artist'] ?? _artist}';
          _isPlaying = data['isPlaying'] == true;
        });
      }
    });

    // Tap on the chathead bubble toggles play/pause.
    _tapSub = FloatyOverlay.onTapped.listen((_) => _togglePlay());
  }

  void _sendAction(String action) {
    FloatyOverlay.shareData({'action': action});
  }

  void _togglePlay() => _sendAction(_isPlaying ? 'pause' : 'play');

  @override
  Widget build(BuildContext context) {
    return Material(
      color: Colors.transparent,
      child: Center(
        child: Card(
          margin: const EdgeInsets.all(8),
          color: Colors.grey.shade900,
          child: SizedBox(
            width: 240,
            child: Padding(
              padding: const EdgeInsets.all(12),
              child: Column(
                mainAxisSize: MainAxisSize.min,
                children: [
                  // Track info
                  Text(
                    _title,
                    style: const TextStyle(
                      color: Colors.white,
                      fontWeight: FontWeight.bold,
                      fontSize: 14,
                    ),
                    maxLines: 1,
                    overflow: TextOverflow.ellipsis,
                  ),
                  if (_artist.isNotEmpty)
                    Text(
                      _artist,
                      style: TextStyle(
                        color: Colors.grey.shade400,
                        fontSize: 12,
                      ),
                      maxLines: 1,
                      overflow: TextOverflow.ellipsis,
                    ),
                  const SizedBox(height: 8),
                  // Transport controls
                  Row(
                    mainAxisAlignment: MainAxisAlignment.center,
                    children: [
                      _ControlButton(
                        icon: Icons.skip_previous,
                        onTap: () => _sendAction('prev'),
                      ),
                      const SizedBox(width: 16),
                      _ControlButton(
                        icon: _isPlaying
                            ? Icons.pause_circle_filled
                            : Icons.play_circle_filled,
                        size: 36,
                        onTap: _togglePlay,
                      ),
                      const SizedBox(width: 16),
                      _ControlButton(
                        icon: Icons.skip_next,
                        onTap: () => _sendAction('next'),
                      ),
                    ],
                  ),
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
      ),
    );
  }

  @override
  void dispose() {
    _dataSub.cancel();
    _tapSub.cancel();
    FloatyOverlay.dispose();
    super.dispose();
  }
}

class _ControlButton extends StatelessWidget {
  const _ControlButton({
    required this.icon,
    required this.onTap,
    this.size = 24,
  });

  final IconData icon;
  final VoidCallback onTap;
  final double size;

  @override
  Widget build(BuildContext context) {
    return GestureDetector(
      onTap: onTap,
      child: Icon(icon, color: Colors.white, size: size),
    );
  }
}
