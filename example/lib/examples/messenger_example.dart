import 'dart:async';

import 'package:floaty_chathead/floaty_chathead.dart';
import 'package:flutter/material.dart';

import '../utils.dart';

/// Example: Messenger-style chat bubble.
///
/// Shows bidirectional messaging between the main app and overlay.
class MessengerExample extends StatefulWidget {
  const MessengerExample({super.key});

  @override
  State<MessengerExample> createState() => _MessengerExampleState();
}

class _MessengerExampleState extends State<MessengerExample> {
  final _controller = TextEditingController();
  final _messages = <_Msg>[];
  late final StreamSubscription<Object?> _sub;

  @override
  void initState() {
    super.initState();
    _sub = FloatyChathead.onData.listen((data) {
      if (data is Map && mounted) {
        setState(() {
          _messages.add(_Msg(
            sender: '${data['sender'] ?? 'overlay'}',
            text: '${data['text'] ?? ''}',
          ));
        });
      }
    });
  }

  Future<void> _launch() async {
    if (!await ensureOverlayPermission()) return;
    await FloatyChathead.showChatHead(
      entryPoint: 'messengerOverlayMain',
      chatheadIconAsset: 'assets/chatheadIcon.png',
      closeIconAsset: 'assets/close.png',
      closeBackgroundAsset: 'assets/closeBg.png',
      notificationTitle: 'Messenger Active',
      notificationIconAsset: 'assets/notificationIcon.png',
      contentWidth: 300,
      contentHeight: 400,
    );
  }

  void _send() {
    final text = _controller.text.trim();
    if (text.isEmpty) return;
    _controller.clear();
    setState(() => _messages.add(_Msg(sender: 'app', text: text)));
    FloatyChathead.shareData({'sender': 'app', 'text': text});
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: const Text('Messenger Chat')),
      floatingActionButton: FloatingActionButton.extended(
        onPressed: _launch,
        icon: const Icon(Icons.chat_bubble),
        label: const Text('Launch'),
      ),
      body: Column(
        children: [
          Expanded(
            child: _messages.isEmpty
                ? const Center(
                    child: Text(
                      'Launch the chathead and start chatting!',
                      style: TextStyle(color: Colors.grey),
                    ),
                  )
                : ListView.builder(
                    padding: const EdgeInsets.all(16),
                    itemCount: _messages.length,
                    itemBuilder: (_, i) {
                      final msg = _messages[i];
                      final isApp = msg.sender == 'app';
                      return Align(
                        alignment: isApp
                            ? Alignment.centerRight
                            : Alignment.centerLeft,
                        child: Container(
                          margin: const EdgeInsets.only(bottom: 8),
                          padding: const EdgeInsets.symmetric(
                            horizontal: 14,
                            vertical: 10,
                          ),
                          decoration: BoxDecoration(
                            color: isApp
                                ? Colors.indigo
                                : Colors.grey.shade200,
                            borderRadius: BorderRadius.circular(16),
                          ),
                          child: Column(
                            crossAxisAlignment: CrossAxisAlignment.start,
                            mainAxisSize: MainAxisSize.min,
                            children: [
                              Text(
                                isApp ? 'You' : 'Overlay',
                                style: TextStyle(
                                  fontSize: 10,
                                  color: isApp
                                      ? Colors.white70
                                      : Colors.black45,
                                ),
                              ),
                              Text(
                                msg.text,
                                style: TextStyle(
                                  color: isApp
                                      ? Colors.white
                                      : Colors.black87,
                                ),
                              ),
                            ],
                          ),
                        ),
                      );
                    },
                  ),
          ),
          // Input bar
          Container(
            padding: const EdgeInsets.all(12),
            decoration: BoxDecoration(
              color: Colors.grey.shade100,
              border: Border(
                top: BorderSide(color: Colors.grey.shade300),
              ),
            ),
            child: Row(
              children: [
                Expanded(
                  child: TextField(
                    controller: _controller,
                    decoration: const InputDecoration(
                      hintText: 'Type a message...',
                      border: OutlineInputBorder(),
                      isDense: true,
                      contentPadding: EdgeInsets.symmetric(
                        horizontal: 12,
                        vertical: 10,
                      ),
                    ),
                    onSubmitted: (_) => _send(),
                  ),
                ),
                const SizedBox(width: 8),
                IconButton(
                  icon: const Icon(Icons.send),
                  onPressed: _send,
                ),
              ],
            ),
          ),
        ],
      ),
    );
  }

  @override
  void dispose() {
    _sub.cancel();
    _controller.dispose();
    FloatyChathead.closeChatHead();
    FloatyChathead.dispose();
    super.dispose();
  }
}

class _Msg {
  _Msg({required this.sender, required this.text});
  final String sender;
  final String text;
}
