import 'dart:async';

import 'package:floaty_head/floaty_head.dart';
import 'package:flutter/material.dart';
import 'package:flutter/services.dart';

Future<void> main() async {
  runApp(MaterialApp(home: Home()));
}

class Home extends StatefulWidget {
  _Home createState() => _Home();
}

class _Home extends State<Home> {
  final FloatyHead floatyHead = FloatyHead();
  bool alternateColor = false;

  @override
  Widget build(BuildContext context) => Scaffold(
        appBar: AppBar(title: Text('Floaty Chathead')),
        body: SingleChildScrollView(
          padding: EdgeInsets.all(50),
          child: Column(
            mainAxisAlignment: MainAxisAlignment.center,
            crossAxisAlignment: CrossAxisAlignment.stretch,
            children: [
              RaisedButton(
                  child: Text('Open Floaty Chathead'),
                  onPressed: () => floatyHead.openBubble()),
              RaisedButton(
                  child: Text('Close Floaty Chathead'),
                  onPressed: () => closeFloatyHead()),
              RaisedButton(
                  child: Text('Set icon Floaty Chathead'),
                  onPressed: () => setIconFromAsset()),
            ],
          ),
        ),
      );

  void closeFloatyHead() {
    if (floatyHead.isOpen) {
      floatyHead.closeHead();
    }
  }

  Future<void> setIconFromAsset() async {
    String result;
    String assetPath = "assets/tmp1.jpg";
    // Platform messages may fail, so we use a try/catch PlatformException.
    try {
      result = await floatyHead.setIconFromAsset(assetPath);
      print(result);
    } on PlatformException {
      result = 'Failed to get wallpaper.';
    }

    print(result);
    // If the widget was removed from the tree while the asynchronous platform
    // message was in flight, we want to discard the reply rather than calling
    // setState to update our non-existent appearance.
    if (!mounted) return;
  }
}
