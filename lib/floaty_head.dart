import 'dart:async';
import 'dart:io';
import 'dart:ui';

export 'models/system_window_body.dart';
export 'models/system_window_button.dart';
export 'models/system_window_decoration.dart';
export 'models/system_window_footer.dart';
export 'models/system_window_header.dart';
export 'models/system_window_margin.dart';
export 'models/system_window_padding.dart';
export 'models/system_window_text.dart';

import 'package:floaty_head/models/system_window_body.dart';
import 'package:floaty_head/models/system_window_footer.dart';
import 'package:floaty_head/models/system_window_header.dart';
import 'package:floaty_head/models/system_window_margin.dart';
import 'package:flutter/services.dart';
import 'package:flutter/widgets.dart';

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
    _timer = Timer.periodic(Duration(seconds: 1), (timer) async {
      _isOpen = await _platform?.invokeMethod('isOpen') ?? false;
      if (!_isOpen) {
        timer?.cancel();
      }
    });
  }

  Future<String> setIcon(String assetPath) async {
    final int result = await _platform.invokeMethod('setIcon', assetPath);
    return result > 0 ? "Icon set" : "There was an error.";
  }

  Future<String> setNotificationTitle(String title) async {
    final int result =
        await _platform.invokeMethod('setNotificationTitle', title);
    return result > 0 ? "Notification Title set" : "There was an error.";
  }

  Future<String> setNotificationIcon(String assetPath) async {
    final int result =
        await _platform.invokeMethod('setNotificationIcon', assetPath);
    return result > 0 ? "NotificationIcon set" : "There was an error.";
  }

  Future<String> setCloseIcon(String assetPath) async {
    final int result = await _platform.invokeMethod('setCloseIcon', assetPath);
    return result > 0 ? "Close Icon set" : "There was an error.";
  }

  Future<String> setCloseBackgroundIcon(String assetPath) async {
    final int result =
        await _platform.invokeMethod('setBackgroundCloseIcon', assetPath);
    return result > 0 ? "Close Icon Background set" : "There was an error.";
  }

  void closeHead() {
    if (_isOpen) {
      _platform.invokeMethod('close');
      _timer.cancel();
      _isOpen = false;
    } else
      throw Exception('Floaty Head not running');
  }

  Future<bool> updateSystemWindow({
    @required SystemWindowHeader header,
    SystemWindowBody body,
    SystemWindowFooter footer,
    SystemWindowMargin margin,
    int width,
    int height,
  }) async {
    assert(header != null);
    final Map<String, dynamic> params = <String, dynamic>{
      'header': header.getMap(),
      'body': body?.getMap(),
      'footer': footer?.getMap(),
      'margin': margin?.getMap(),
      'gravity': 1.0,
      'width': width ?? -1,
      'height': height ?? -2
    };
    return await _platform.invokeMethod('updateSystemWindow', params);
  }
}
