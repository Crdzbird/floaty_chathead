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

  final header = FloatyHeadHeader(
    title: FloatyHeadText(
      text: "Outgoing Call",
      fontSize: 10,
      textColor: Colors.black45,
      fontWeight: FontWeight.normal,
      padding: FloatyHeadPadding(
        bottom: 4,
        left: 5,
        right: 5,
        top: 5,
      ),
    ),
    padding: FloatyHeadPadding.setSymmetricPadding(12, 12),
    subTitle: FloatyHeadText(
      text: "8989898989",
      fontSize: 14,
      fontWeight: FontWeight.bold,
      padding: FloatyHeadPadding(
        bottom: 4,
        left: 5,
        right: 5,
        top: 5,
      ),
      textColor: Colors.black87,
    ),
    decoration: FloatyHeadDecoration(startColor: Colors.grey[100]),
    button: FloatyHeadButton(
        text: FloatyHeadText(
          fontWeight: FontWeight.bold,
          text: "Personal",
          fontSize: 10,
          textColor: Colors.black45,
          padding: FloatyHeadPadding(
            bottom: 4,
            left: 5,
            right: 5,
            top: 5,
          ),
        ),
        tag: "personal_btn"),
  );

  final body = FloatyHeadBody(
    rows: [
      EachRow(
        columns: [
          EachColumn(
            text: FloatyHeadText(
              fontWeight: FontWeight.bold,
              text: "Updated body",
              fontSize: 12,
              textColor: Colors.black45,
              padding: FloatyHeadPadding(
                bottom: 4,
                left: 5,
                right: 5,
                top: 5,
              ),
            ),
          ),
        ],
        gravity: ContentGravity.center,
      ),
      EachRow(columns: [
        EachColumn(
          text: FloatyHeadText(
            text: "Updated long data of the body",
            fontSize: 12,
            textColor: Colors.black87,
            fontWeight: FontWeight.bold,
            padding: FloatyHeadPadding(
              bottom: 4,
              left: 5,
              right: 5,
              top: 5,
            ),
          ),
          padding: FloatyHeadPadding.setSymmetricPadding(6, 8),
          decoration: FloatyHeadDecoration(
              startColor: Colors.black12, borderRadius: 25.0),
          margin: FloatyHeadMargin(top: 4),
        ),
      ], gravity: ContentGravity.center),
      EachRow(
        columns: [
          EachColumn(
            text: FloatyHeadText(
              text: "Notes",
              fontSize: 10,
              textColor: Colors.black45,
              fontWeight: FontWeight.normal,
              padding: FloatyHeadPadding(
                bottom: 4,
                left: 5,
                right: 5,
                top: 5,
              ),
            ),
          ),
        ],
        gravity: ContentGravity.left,
        margin: FloatyHeadMargin(top: 8),
      ),
      EachRow(
        columns: [
          EachColumn(
            text: FloatyHeadText(
              text: "Updated random notes.",
              fontSize: 13,
              textColor: Colors.black54,
              fontWeight: FontWeight.bold,
              padding: FloatyHeadPadding(
                bottom: 4,
                left: 5,
                right: 5,
                top: 5,
              ),
            ),
          ),
        ],
        gravity: ContentGravity.left,
      ),
    ],
    padding: FloatyHeadPadding(left: 16, right: 16, bottom: 12, top: 12),
  );

  final footer = FloatyHeadFooter(
    buttons: [
      FloatyHeadButton(
        text: FloatyHeadText(
          text: "Simple button",
          fontSize: 12,
          textColor: Color.fromRGBO(250, 139, 97, 1),
          padding: FloatyHeadPadding(
            bottom: 4,
            left: 5,
            right: 5,
            top: 5,
          ),
          fontWeight: FontWeight.normal,
        ),
        tag: "simple_button",
        padding: FloatyHeadPadding(left: 10, right: 10, bottom: 10, top: 10),
        width: 0,
        height: FloatyHeadButton.WRAP_CONTENT,
        decoration: FloatyHeadDecoration(
            startColor: Colors.white,
            endColor: Colors.white,
            borderWidth: 0,
            borderRadius: 0.0),
      ),
      FloatyHeadButton(
        text: FloatyHeadText(
          fontWeight: FontWeight.normal,
          padding: FloatyHeadPadding(
            bottom: 4,
            left: 5,
            right: 5,
            top: 5,
          ),
          text: "Focus button",
          fontSize: 12,
          textColor: Colors.white,
        ),
        tag: "focus_button",
        width: 0,
        padding: FloatyHeadPadding(left: 10, right: 10, bottom: 10, top: 10),
        height: FloatyHeadButton.WRAP_CONTENT,
        decoration: FloatyHeadDecoration(
            startColor: Color.fromRGBO(250, 139, 97, 1),
            endColor: Color.fromRGBO(247, 28, 88, 1),
            borderWidth: 0,
            borderRadius: 30.0),
      )
    ],
    padding: FloatyHeadPadding(left: 16, right: 16, bottom: 12),
    decoration: FloatyHeadDecoration(startColor: Colors.white),
    buttonsPosition: ButtonPosition.center,
  );

  bool alternateColor = false;

  @override
  void initState() {
    super.initState();
    FloatyHead.registerOnClickListener(callBack);
  }

  @override
  Widget build(BuildContext context) => Scaffold(
        appBar: AppBar(title: Text('Floaty Chathead')),
        body: SingleChildScrollView(
          padding: EdgeInsets.all(50),
          child: Column(
            mainAxisAlignment: MainAxisAlignment.center,
            crossAxisAlignment: CrossAxisAlignment.stretch,
            children: [
              ElevatedButton(
                  child: Text('Open Floaty Chathead'),
                  onPressed: () => floatyHead.openBubble()),
              ElevatedButton(
                  child: Text('Close Floaty Chathead'),
                  onPressed: () => closeFloatyHead()),
              ElevatedButton(
                  child: Text('Set icon Floaty Chathead'),
                  onPressed: () => setIcon()),
              ElevatedButton(
                  child: Text('Set close icon Floaty Chathead'),
                  onPressed: () => setCloseIcon()),
              ElevatedButton(
                  child: Text('Set close background Icon Floaty Chathead'),
                  onPressed: () => setCloseIconBackground()),
              ElevatedButton(
                  child: Text(
                      'Set notification title to: OH MY GOD! THEY KILL KENNY!!! Floaty Chathead'),
                  onPressed: () => setNotificationTitle()),
              ElevatedButton(
                  child: Text('Set notification Icon Floaty Chathead'),
                  onPressed: () => setNotificationIcon()),
              ElevatedButton(
                  child: Text('Set Custom Header into Floaty Chathead'),
                  onPressed: () => setCustomHeader()),
            ],
          ),
        ),
      );

  void setCustomHeader() {
    floatyHead.updateFloatyHeadContent(
      header: header,
      body: body,
      footer: footer,
    );
  }

  void closeFloatyHead() {
    if (floatyHead.isOpen) {
      floatyHead.closeHead();
    }
  }

  Future<void> setNotificationTitle() async {
    String result;
    try {
      result = await floatyHead
          .setNotificationTitle("OH MY GOD! THEY KILL KENNY!!!");
    } on PlatformException {
      result = 'Failed to get icon.';
    }
    print('result: $result');
    if (!mounted) return;
  }

  Future<void> setNotificationIcon() async {
    String result;
    String assetPath = "assets/notificationIcon.png";
    try {
      result = await floatyHead.setNotificationIcon(assetPath);
      print(result);
    } on PlatformException {
      result = 'Failed to get icon.';
      print("failed: $result");
    }
    if (!mounted) return;
  }

  Future<void> setIcon() async {
    String result;
    String assetPath = "assets/chatheadIcon.png";
    try {
      result = await floatyHead.setIcon(assetPath);
      print('result: $result');
    } on PlatformException {
      result = 'Failed to get icon.';
    }
    if (!mounted) return;
  }

  Future<void> setCloseIcon() async {
    String assetPath = "assets/close.png";
    try {
      await floatyHead.setCloseIcon(assetPath);
    } on PlatformException {
      return;
    }
    if (!mounted) return;
  }

  Future<void> setCloseIconBackground() async {
    String assetPath = "assets/closeBg.png";
    try {
      await floatyHead.setCloseBackgroundIcon(assetPath);
    } on PlatformException {
      return;
    }
    if (!mounted) return;
  }
}

void callBack(String tag) {
  print('CALLBACK FROM FRAGMENT BUILDED: $tag');
  switch (tag) {
    case "simple_button":
    case "updated_simple_button":
      break;
    case "focus_button":
      print("Focus button has been called");
      break;
    default:
      print("OnClick event of $tag");
  }
}
