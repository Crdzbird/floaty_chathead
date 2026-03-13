import 'dart:async';

import 'package:floaty_chathead/floaty_chathead.dart';
import 'package:flutter/material.dart';

import '../utils.dart';

/// Example: Mini player overlay with transport controls.
///
/// Demonstrates bidirectional state sync and [onTapped] stream.
class MiniPlayerExample extends StatefulWidget {
  const MiniPlayerExample({super.key});

  @override
  State<MiniPlayerExample> createState() => _MiniPlayerExampleState();
}

class _MiniPlayerExampleState extends State<MiniPlayerExample> {
  static const _playlist = [
    {'title': 'Sunset Drive', 'artist': 'Lo-Fi Beats'},
    {'title': 'Ocean Waves', 'artist': 'Nature Sounds'},
    {'title': 'City Lights', 'artist': 'Synthwave FM'},
    {'title': 'Mountain Air', 'artist': 'Ambient Works'},
    {'title': 'Rainy Day', 'artist': 'Jazz Cafe'},
  ];

  int _currentIndex = 0;
  bool _isPlaying = false;
  late final StreamSubscription<Object?> _sub;

  Map<String, String> get _currentTrack => _playlist[_currentIndex];

  @override
  void initState() {
    super.initState();
    _sub = FloatyChathead.onData.listen((data) {
      if (data is Map && mounted) {
        switch (data['action']) {
          case 'play':
            setState(() => _isPlaying = true);
          case 'pause':
            setState(() => _isPlaying = false);
          case 'next':
            setState(() {
              _currentIndex = (_currentIndex + 1) % _playlist.length;
              _isPlaying = true;
            });
          case 'prev':
            setState(() {
              _currentIndex =
                  (_currentIndex - 1 + _playlist.length) % _playlist.length;
              _isPlaying = true;
            });
        }
        _pushState();
      }
    });
  }

  void _pushState() {
    FloatyChathead.shareData({
      'title': _currentTrack['title'],
      'artist': _currentTrack['artist'],
      'isPlaying': _isPlaying,
    });
  }

  Future<void> _launch() async {
    if (!await ensureOverlayPermission()) return;
    await FloatyChathead.showChatHead(
      entryPoint: 'miniPlayerOverlayMain',
      chatheadIconAsset: 'assets/chatheadIcon.png',
      closeIconAsset: 'assets/close.png',
      closeBackgroundAsset: 'assets/closeBg.png',
      notificationTitle: 'Mini Player Active',
      notificationIconAsset: 'assets/notificationIcon.png',
      contentWidth: 260,
      contentHeight: 180,
    );
    // Send initial state after a short delay for engine to initialize.
    Future<void>.delayed(const Duration(milliseconds: 500), _pushState);
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: const Text('Mini Player')),
      floatingActionButton: FloatingActionButton.extended(
        onPressed: _launch,
        icon: const Icon(Icons.music_note),
        label: const Text('Launch Player'),
      ),
      body: Column(
        children: [
          const SizedBox(height: 32),
          // Now playing
          Icon(
            _isPlaying ? Icons.pause_circle : Icons.play_circle,
            size: 80,
            color: Colors.indigo,
          ),
          const SizedBox(height: 16),
          Text(
            _currentTrack['title']!,
            style: Theme.of(context).textTheme.headlineSmall,
          ),
          Text(
            _currentTrack['artist']!,
            style: Theme.of(context).textTheme.bodyMedium?.copyWith(
                  color: Colors.grey,
                ),
          ),
          const SizedBox(height: 24),
          // Controls
          Row(
            mainAxisAlignment: MainAxisAlignment.center,
            children: [
              IconButton(
                icon: const Icon(Icons.skip_previous, size: 36),
                onPressed: () {
                  setState(() {
                    _currentIndex =
                        (_currentIndex - 1 + _playlist.length) %
                            _playlist.length;
                    _isPlaying = true;
                  });
                  _pushState();
                },
              ),
              const SizedBox(width: 16),
              IconButton(
                icon: Icon(
                  _isPlaying ? Icons.pause_circle : Icons.play_circle,
                  size: 48,
                ),
                onPressed: () {
                  setState(() => _isPlaying = !_isPlaying);
                  _pushState();
                },
              ),
              const SizedBox(width: 16),
              IconButton(
                icon: const Icon(Icons.skip_next, size: 36),
                onPressed: () {
                  setState(() {
                    _currentIndex = (_currentIndex + 1) % _playlist.length;
                    _isPlaying = true;
                  });
                  _pushState();
                },
              ),
            ],
          ),
          const Divider(height: 32),
          // Playlist
          Expanded(
            child: ListView.builder(
              itemCount: _playlist.length,
              itemBuilder: (_, i) {
                final track = _playlist[i];
                final isCurrent = i == _currentIndex;
                return ListTile(
                  leading: Icon(
                    isCurrent && _isPlaying
                        ? Icons.equalizer
                        : Icons.music_note,
                    color: isCurrent ? Colors.indigo : Colors.grey,
                  ),
                  title: Text(
                    track['title']!,
                    style: TextStyle(
                      fontWeight:
                          isCurrent ? FontWeight.bold : FontWeight.normal,
                    ),
                  ),
                  subtitle: Text(track['artist']!),
                  onTap: () {
                    setState(() {
                      _currentIndex = i;
                      _isPlaying = true;
                    });
                    _pushState();
                  },
                );
              },
            ),
          ),
        ],
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
