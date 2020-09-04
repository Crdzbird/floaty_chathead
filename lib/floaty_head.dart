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
      throw PlatformException(code: 'Floaty Head only available for Android');
  }
  static const _platform = const MethodChannel('ni.devotion/floaty_head');
  void openBubble() async {
    _platform.invokeMethod('start');
    setCallback(callback);
    _timer = Timer.periodic(Duration(seconds: 1), (timer) async {
      _isOpen = await _platform?.invokeMethod('isOpen') ?? false;
      if (!_isOpen) {
        timer?.cancel();
      }
    });
  }

  void setCallback(Timer callback) => _callback = callback;

  Future<String> setIconFromAsset(String assetPath) async {
    final int result =
        await _platform.invokeMethod('setIconFromAsset', assetPath);

    /// Function returns the set String as result, use for debugging
    return result > 0 ? "Wallpaper set" : "There was an error.";
  }

  void closeHead() {
    if (_isOpen) {
      removeCallback();
      _platform.invokeMethod('close');
      _timer.cancel();
      _isOpen = false;
    } else
      throw Exception('Floaty Head not running');
  }

  void removeCallback() {
    if (_isOpen) {
      _callback?.cancel();
      _callback = null;
    } else
      throw Exception('Floaty Head not running');
  }
}
