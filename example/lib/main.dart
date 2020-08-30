import 'dart:async';
import 'dart:developer';

import 'package:floaty_head/floaty_head.dart';
import 'package:flutter/material.dart';

Future<void> main() async {
  runApp(MaterialApp(home: Home()));
}

class Home extends StatefulWidget {
  _Home createState() => _Home();
}

class _Home extends State<Home> {
  final FloatyHead bubbleOverlay = FloatyHead();
  bool alternateColor = false;

  @override
  Widget build(BuildContext context) => Scaffold(
        appBar: AppBar(title: Text('Bubble Overlay')),
        body: SingleChildScrollView(
          padding: EdgeInsets.all(50),
          child: Column(
            mainAxisAlignment: MainAxisAlignment.center,
            crossAxisAlignment: CrossAxisAlignment.stretch,
            children: [
              RaisedButton(
                  child: Text('Open Bubble Overlay'),
                  onPressed: () => bubbleOverlay.openBubble()),
            ],
          ),
        ),
      );
}
