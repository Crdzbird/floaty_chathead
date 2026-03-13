import 'package:floaty_chathead/floaty_chathead.dart';
import 'package:flutter/material.dart';

import 'examples/messenger_example.dart';
import 'examples/mini_player_example.dart';
import 'examples/notification_counter_example.dart';
import 'examples/quick_action_example.dart';
import 'examples/timer_example.dart';
import 'overlays/messenger_overlay.dart';
import 'overlays/mini_player_overlay.dart';
import 'overlays/notification_counter_overlay.dart';
import 'overlays/quick_action_overlay.dart';
import 'overlays/timer_overlay.dart';

void main() => runApp(const MaterialApp(home: GalleryPage()));

// ---------------------------------------------------------------------------
// Overlay entry points — must be top-level for AOT discoverability.
// ---------------------------------------------------------------------------

@pragma('vm:entry-point')
void overlayMain() {
  WidgetsFlutterBinding.ensureInitialized();
  runApp(
    const MaterialApp(
      debugShowCheckedModeBanner: false,
      home: OverlayContent(),
    ),
  );
}

@pragma('vm:entry-point')
void messengerOverlayMain() {
  WidgetsFlutterBinding.ensureInitialized();
  runApp(
    const MaterialApp(
      debugShowCheckedModeBanner: false,
      home: MessengerOverlay(),
    ),
  );
}

@pragma('vm:entry-point')
void miniPlayerOverlayMain() {
  WidgetsFlutterBinding.ensureInitialized();
  runApp(
    const MaterialApp(
      debugShowCheckedModeBanner: false,
      home: MiniPlayerOverlay(),
    ),
  );
}

@pragma('vm:entry-point')
void quickActionOverlayMain() {
  WidgetsFlutterBinding.ensureInitialized();
  runApp(
    const MaterialApp(
      debugShowCheckedModeBanner: false,
      home: QuickActionOverlay(),
    ),
  );
}

@pragma('vm:entry-point')
void counterOverlayMain() {
  WidgetsFlutterBinding.ensureInitialized();
  runApp(
    const MaterialApp(
      debugShowCheckedModeBanner: false,
      home: NotificationCounterOverlay(),
    ),
  );
}

@pragma('vm:entry-point')
void timerOverlayMain() {
  WidgetsFlutterBinding.ensureInitialized();
  runApp(
    const MaterialApp(
      debugShowCheckedModeBanner: false,
      home: TimerOverlay(),
    ),
  );
}

// ---------------------------------------------------------------------------
// Gallery — lists all examples
// ---------------------------------------------------------------------------

class GalleryPage extends StatelessWidget {
  const GalleryPage({super.key});

  static const _examples = <_ExampleInfo>[
    _ExampleInfo(
      title: 'Basic (Original)',
      description: 'Simple chathead with show, close, and send data buttons.',
      icon: Icons.bubble_chart,
      color: Colors.teal,
    ),
    _ExampleInfo(
      title: 'Messenger Chat',
      description: 'Bidirectional messaging between main app and overlay.',
      icon: Icons.chat,
      color: Colors.indigo,
    ),
    _ExampleInfo(
      title: 'Mini Player',
      description: 'Media transport controls with state sync.',
      icon: Icons.music_note,
      color: Colors.deepPurple,
    ),
    _ExampleInfo(
      title: 'Quick Actions',
      description: 'Click-through FAB buttons with action logging.',
      icon: Icons.bolt,
      color: Colors.orange,
    ),
    _ExampleInfo(
      title: 'Notification Counter',
      description: 'Reactive badge that updates from main app data.',
      icon: Icons.notifications,
      color: Colors.red,
    ),
    _ExampleInfo(
      title: 'Timer / Stopwatch',
      description: 'Persistent timer with dynamic resize and lap tracking.',
      icon: Icons.timer,
      color: Colors.blueGrey,
    ),
  ];

  Widget _buildRoute(int index) {
    return switch (index) {
      0 => const HomePage(),
      1 => const MessengerExample(),
      2 => const MiniPlayerExample(),
      3 => const QuickActionExample(),
      4 => const NotificationCounterExample(),
      5 => const TimerExample(),
      _ => const HomePage(),
    };
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('Floaty Chathead Examples'),
      ),
      body: ListView.separated(
        padding: const EdgeInsets.all(16),
        itemCount: _examples.length,
        separatorBuilder: (_, _) => const SizedBox(height: 12),
        itemBuilder: (context, index) {
          final info = _examples[index];
          return Card(
            clipBehavior: Clip.antiAlias,
            child: ListTile(
              leading: CircleAvatar(
                backgroundColor: info.color,
                child: Icon(info.icon, color: Colors.white),
              ),
              title: Text(info.title),
              subtitle: Text(info.description),
              trailing: const Icon(Icons.chevron_right),
              onTap: () => Navigator.of(context).push(
                MaterialPageRoute<void>(
                  builder: (_) => _buildRoute(index),
                ),
              ),
            ),
          );
        },
      ),
    );
  }
}

class _ExampleInfo {
  const _ExampleInfo({
    required this.title,
    required this.description,
    required this.icon,
    required this.color,
  });

  final String title;
  final String description;
  final IconData icon;
  final Color color;
}

// ---------------------------------------------------------------------------
// Basic example (original)
// ---------------------------------------------------------------------------

class HomePage extends StatefulWidget {
  const HomePage({super.key});

  @override
  State<HomePage> createState() => _HomePageState();
}

class _HomePageState extends State<HomePage> {
  @override
  void initState() {
    super.initState();
    FloatyChathead.onData.listen((data) {
      debugPrint('Received from overlay: $data');
    });
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: const Text('Basic Chathead')),
      body: Center(
        child: Padding(
          padding: const EdgeInsets.all(32),
          child: Column(
            mainAxisSize: MainAxisSize.min,
            crossAxisAlignment: CrossAxisAlignment.stretch,
            children: [
              ElevatedButton(
                onPressed: _showChatHead,
                child: const Text('Show Chathead'),
              ),
              const SizedBox(height: 12),
              ElevatedButton(
                onPressed: () => FloatyChathead.closeChatHead(),
                child: const Text('Close Chathead'),
              ),
              const SizedBox(height: 12),
              ElevatedButton(
                onPressed: () =>
                    FloatyChathead.shareData({'counter': 42}),
                child: const Text('Send Data to Overlay'),
              ),
            ],
          ),
        ),
      ),
    );
  }

  Future<void> _showChatHead() async {
    final granted = await FloatyChathead.checkPermission();
    if (!granted) {
      await FloatyChathead.requestPermission();
    }
    await FloatyChathead.showChatHead(
      chatheadIconAsset: 'assets/chatheadIcon.png',
      closeIconAsset: 'assets/close.png',
      closeBackgroundAsset: 'assets/closeBg.png',
      notificationTitle: 'Chathead Active',
      notificationIconAsset: 'assets/notificationIcon.png',
    );
  }

  @override
  void dispose() {
    FloatyChathead.dispose();
    super.dispose();
  }
}

// ---------------------------------------------------------------------------
// Basic overlay content (used by the "Basic" example)
// ---------------------------------------------------------------------------

class OverlayContent extends StatefulWidget {
  const OverlayContent({super.key});

  @override
  State<OverlayContent> createState() => _OverlayContentState();
}

class _OverlayContentState extends State<OverlayContent> {
  String _message = 'Waiting for data...';

  @override
  void initState() {
    super.initState();
    FloatyOverlay.setUp();
    FloatyOverlay.onData.listen((data) {
      if (mounted) {
        setState(() => _message = 'Received: $data');
      }
    });
  }

  @override
  Widget build(BuildContext context) {
    return Material(
      color: Colors.transparent,
      child: Center(
        child: Card(
          margin: const EdgeInsets.all(16),
          child: Padding(
            padding: const EdgeInsets.all(16),
            child: Column(
              mainAxisSize: MainAxisSize.min,
              children: [
                const Text(
                  'Overlay Content',
                  style: TextStyle(
                    fontSize: 18,
                    fontWeight: FontWeight.bold,
                  ),
                ),
                const SizedBox(height: 8),
                Text(_message),
                const SizedBox(height: 8),
                ElevatedButton(
                  onPressed: () => FloatyOverlay.shareData(
                    {'status': 'hello from overlay'},
                  ),
                  child: const Text('Send to Main App'),
                ),
                TextButton(
                  onPressed: () => FloatyOverlay.closeOverlay(),
                  child: const Text('Close'),
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
    FloatyOverlay.dispose();
    super.dispose();
  }
}
