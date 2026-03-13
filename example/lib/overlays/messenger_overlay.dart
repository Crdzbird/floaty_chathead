import 'dart:async';

import 'package:floaty_chathead/floaty_chathead.dart';
import 'package:flutter/material.dart';

/// Overlay widget that renders a compact messenger chat interface.
///
/// Demonstrates: bidirectional [FloatyOverlay.shareData] / [FloatyOverlay.onData].
class MessengerOverlay extends StatefulWidget {
  const MessengerOverlay({super.key});

  @override
  State<MessengerOverlay> createState() => _MessengerOverlayState();
}

class _MessengerOverlayState extends State<MessengerOverlay> {
  final _controller = TextEditingController();
  final _messages = <_Msg>[];
  late final StreamSubscription<Object?> _sub;

  @override
  void initState() {
    super.initState();
    FloatyOverlay.setUp();
    _sub = FloatyOverlay.onData.listen((data) {
      if (data is Map && mounted) {
        setState(() {
          _messages.add(_Msg(
            sender: '${data['sender'] ?? 'app'}',
            text: '${data['text'] ?? ''}',
          ));
        });
      }
    });
  }

  void _send() {
    final text = _controller.text.trim();
    if (text.isEmpty) return;
    _controller.clear();
    setState(() => _messages.add(_Msg(sender: 'overlay', text: text)));
    FloatyOverlay.shareData({'sender': 'overlay', 'text': text});
  }

  @override
  Widget build(BuildContext context) {
    return Material(
      color: Colors.transparent,
      child: Center(
        child: Card(
          margin: const EdgeInsets.all(8),
          child: SizedBox(
            width: 280,
            height: 360,
            child: Column(
              children: [
                // Header
                Container(
                  padding: const EdgeInsets.symmetric(
                    horizontal: 12,
                    vertical: 8,
                  ),
                  decoration: const BoxDecoration(
                    color: Colors.indigo,
                    borderRadius: BorderRadius.vertical(
                      top: Radius.circular(12),
                    ),
                  ),
                  child: Row(
                    children: [
                      const Icon(Icons.chat, color: Colors.white, size: 18),
                      const SizedBox(width: 8),
                      const Expanded(
                        child: Text(
                          'Messenger',
                          style: TextStyle(
                            color: Colors.white,
                            fontWeight: FontWeight.bold,
                          ),
                        ),
                      ),
                      GestureDetector(
                        onTap: FloatyOverlay.closeOverlay,
                        child: const Icon(
                          Icons.close,
                          color: Colors.white,
                          size: 18,
                        ),
                      ),
                    ],
                  ),
                ),
                // Messages
                Expanded(
                  child: ListView.builder(
                    padding: const EdgeInsets.all(8),
                    itemCount: _messages.length,
                    itemBuilder: (_, i) {
                      final msg = _messages[i];
                      final isMe = msg.sender == 'overlay';
                      return Align(
                        alignment: isMe
                            ? Alignment.centerRight
                            : Alignment.centerLeft,
                        child: Container(
                          margin: const EdgeInsets.only(bottom: 4),
                          padding: const EdgeInsets.symmetric(
                            horizontal: 10,
                            vertical: 6,
                          ),
                          decoration: BoxDecoration(
                            color: isMe ? Colors.indigo : Colors.grey.shade200,
                            borderRadius: BorderRadius.circular(12),
                          ),
                          child: Text(
                            msg.text,
                            style: TextStyle(
                              color: isMe ? Colors.white : Colors.black87,
                              fontSize: 13,
                            ),
                          ),
                        ),
                      );
                    },
                  ),
                ),
                // Input
                Padding(
                  padding: const EdgeInsets.all(8),
                  child: Row(
                    children: [
                      Expanded(
                        child: TextField(
                          controller: _controller,
                          style: const TextStyle(fontSize: 13),
                          decoration: const InputDecoration(
                            hintText: 'Type a message...',
                            isDense: true,
                            contentPadding: EdgeInsets.symmetric(
                              horizontal: 10,
                              vertical: 8,
                            ),
                            border: OutlineInputBorder(),
                          ),
                          onSubmitted: (_) => _send(),
                        ),
                      ),
                      const SizedBox(width: 4),
                      IconButton(
                        icon: const Icon(Icons.send, size: 20),
                        onPressed: _send,
                        padding: EdgeInsets.zero,
                        constraints: const BoxConstraints(),
                      ),
                    ],
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
    _sub.cancel();
    _controller.dispose();
    FloatyOverlay.dispose();
    super.dispose();
  }
}

class _Msg {
  _Msg({required this.sender, required this.text});
  final String sender;
  final String text;
}
