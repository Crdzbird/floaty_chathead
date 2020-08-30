import 'dart:async';
import 'dart:io';

import 'package:flutter/services.dart';

class FloatyHead {
  bool _isOpen = false;
  Timer _callback;
  Timer _timer;
  bool get isOpen => _isOpen;
  Timer get callback => _callback;

  FloatyHead() {
    if (!Platform.isAndroid)
      throw PlatformException(
          code: 'Bubble overlay only available for Android');
  }
  static const _platform = const MethodChannel('ni.devotion/floaty_head');
  void openBubble() async {
    _platform.invokeMethod('openBubble');

    setCallback(callback);

    _timer = Timer.periodic(Duration(seconds: 1), (timer) async {
      _isOpen = await _platform?.invokeMethod('isBubbleOpen') ?? false;
      if (!_isOpen) {
        print("isnt open");
        timer?.cancel();
      }
    });
  }

  void setCallback(Timer callback) => _callback = callback;
}
