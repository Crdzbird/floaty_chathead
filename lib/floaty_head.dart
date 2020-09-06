import 'dart:async';
import 'dart:io';
import 'dart:ui';

export 'models/floaty_head_body.dart';
export 'models/floaty_head_button.dart';
export 'models/floaty_head_decoration.dart';
export 'models/floaty_head_footer.dart';
export 'models/floaty_head_header.dart';
export 'models/floaty_head_margin.dart';
export 'models/floaty_head_padding.dart';
export 'models/floaty_head_text.dart';
export 'utils/commons.dart';

import 'package:floaty_head/models/floaty_head_body.dart';
import 'package:floaty_head/models/floaty_head_footer.dart';
import 'package:floaty_head/models/floaty_head_header.dart';
import 'package:floaty_head/models/floaty_head_margin.dart';
import 'package:flutter/services.dart';
import 'package:flutter/widgets.dart';

enum FloatyHeadGravity {
  top,
  bottom,
  center,
}

enum ContentGravity {
  left,
  right,
  center,
}

enum ButtonPosition {
  trailing,
  leading,
  center,
}

enum FontWeight {
  normal,
  bold,
  italic,
  bold_italic,
}

typedef void OnClickListener(String tag);

class FloatyHead {
  bool _isOpen = false;
  Timer _callback;
  Timer _timer;
  bool get isOpen => _isOpen;
  Timer get callback => _callback;
  static const _platform = const MethodChannel('ni.devotion/floaty_head');

  FloatyHead() {
    if (!Platform.isAndroid)
      throw PlatformException(code: 'Floaty Head only available for Android');
  }

  void openBubble() async {
    _platform.invokeMethod('start');
    _timer = Timer.periodic(Duration(seconds: 1), (timer) async {
      _isOpen = await _platform?.invokeMethod('isOpen') ?? false;
      if (!_isOpen) {
        timer?.cancel();
      }
    });
  }

  static Future<bool> registerOnClickListener(
      OnClickListener callBackFunction) async {
    final callBackDispatcher =
        PluginUtilities.getCallbackHandle(callbackDispatcher);
    final callBack = PluginUtilities.getCallbackHandle(callBackFunction);
    _platform.setMethodCallHandler((MethodCall call) {
      print("Got callback: ${call.method}");
      switch (call.method) {
        case "callBack":
          dynamic arguments = call.arguments;
          print(arguments);
          if (arguments is List) {
            final type = arguments[0];
            if (type == "onClick") {
              print("registerOnClickListener onClick:  ${arguments[1]}");
              final tag = arguments[1];
              callBackFunction(tag);
            }
          }
      }
      return null;
    });
    await _platform.invokeMethod("registerCallBackHandler",
        <dynamic>[callBackDispatcher.toRawHandle(), callBack.toRawHandle()]);
    return true;
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

  Future<bool> updateFloatyHeadContent({
    @required FloatyHeadHeader header,
    FloatyHeadBody body,
    FloatyHeadFooter footer,
    FloatyHeadMargin margin,
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
    return await _platform.invokeMethod('setFloatyHeadContent', params);
  }
}

void callbackDispatcher() {
  const MethodChannel _backgroundChannel =
      const MethodChannel('ni.devotion/floaty_head');
  WidgetsFlutterBinding.ensureInitialized();
  _backgroundChannel.setMethodCallHandler((MethodCall call) async {
    final args = call.arguments;
    final Function callback = PluginUtilities.getCallbackFromHandle(
        CallbackHandle.fromRawHandle(args[0]));
    assert(callback != null);
    final type = args[1];
    if (type == "onClick") {
      final tag = args[2];
      callback(tag);
    }
  });
}
