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
                  onPressed: () => setIcon()),
              RaisedButton(
                  child: Text('Set close icon Floaty Chathead'),
                  onPressed: () => setCloseIcon()),
              RaisedButton(
                  child: Text('Set close background Icon Floaty Chathead'),
                  onPressed: () => setCloseIconBackground()),
            ],
          ),
        ),
      );

  void closeFloatyHead() {
    if (floatyHead.isOpen) {
      floatyHead.closeHead();
    }
  }

  Future<void> setIcon() async {
    String result;
    String assetPath = "assets/tmp1.jpg";
    try {
      result = await floatyHead.setIcon(assetPath);
      print(result);
    } on PlatformException {
      result = 'Failed to get icon.';
    }
    if (!mounted) return;
  }

  Future<void> setCloseIcon() async {
    String result;
    String assetPath = "assets/tmp1.jpg";
    try {
      result = await floatyHead.setCloseIcon(assetPath);
      print(result);
    } on PlatformException {
      result = 'Failed to get icon.';
    }
    if (!mounted) return;
  }

  Future<void> setCloseIconBackground() async {
    String result;
    String assetPath = "assets/tmp1.jpg";
    try {
      result = await floatyHead.setCloseBackgroundIcon(assetPath);
      print(result);
    } on PlatformException {
      result = 'Failed to get icon.';
    }
    if (!mounted) return;
  }
}
