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

/// Set the [gravity] orientation for the header of the chathead
///
/// use [top] to position the content of the header
/// to the upper side of the container.
///
/// use [bottom] to position the content of the header
/// to the bottom side of the container.
///
/// use [center] to position the content of the header
/// to the bottom side of the container.
enum FloatyHeadGravity {
  top,
  bottom,
  center,
}

/// Set the [gravity] orientation for the body of the chathead
///
/// use [left] to position the content of the body
/// to the Start side of the container.
///
/// use [right] to position the content of the body
/// to the End side of the container.
///
/// use [center] to position the content of the body
/// to the center of the container.
enum ContentGravity {
  left,
  right,
  center,
}

/// Set the [position] for the buttons of the chathead
///
/// use [trailing] to position the button
/// at the End of the container.
///
/// use [leading] to position the button
/// at the Start of the container.
///
/// use [center] to position the button
/// at the center of the container.
enum ButtonPosition {
  trailing,
  leading,
  center,
}

/// Set the [Weight] for the text inside the chathead
///
/// use [normal] for w500 font.
///
/// use [bold] for w900 font.
///
/// use [italic] for a stylished font.
///
/// use [bold_italic] for a w900 font with stylished.
enum FontWeight {
  normal,
  bold,
  italic,
  bold_italic,
}

/// This is called when a button is tapped, the return is gonna be.
/// ```dart
/// OnClickListener(String tag) => 'btn_ok';
/// ```
typedef void OnClickListener(String tag);

class FloatyHead {
  bool _isOpen = false;
  Timer _callback;
  Timer _timer;

  /// Return the [state] of the chathead
  /// ```dart
  /// bool get isOpen => true or false;
  /// ```
  bool get isOpen => _isOpen;

  /// The timer is used when an action is needed to perform after x time has passed.
  Timer get callback => _callback;
  static const _platform = const MethodChannel('ni.devotion/floaty_head');

  FloatyHead() {
    if (!Platform.isAndroid)
      throw PlatformException(code: 'Floaty Head only available for Android');
  }

  /// Start the chathead.
  /// also check constantly the current state of it.
  ///
  /// for that please use the method [isOpen].
  void openBubble() async {
    _platform.invokeMethod('start');
    _timer = Timer.periodic(Duration(seconds: 1), (timer) async {
      _isOpen = await _platform?.invokeMethod('isOpen') ?? false;
      if (!_isOpen) {
        timer?.cancel();
      }
    });
  }

  /// If a [widget] is [pressed] check the [type] of [tap].
  /// and returns to the client-dart the component that has been pressed as a
  /// [string] with his tag.
  static Future<bool> registerOnClickListener(
      OnClickListener callBackFunction) async {
    final callBackDispatcher =
        PluginUtilities.getCallbackHandle(callbackDispatcher);
    final callBack = PluginUtilities.getCallbackHandle(callBackFunction);
    _platform.setMethodCallHandler((MethodCall call) {
      switch (call.method) {
        case "callBack":
          dynamic arguments = call.arguments;
          if (arguments is List) {
            final type = arguments[0];
            if (type == "onClick") {
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

  ///Set a custom [icon] for the chathead.
  Future<String> setIcon(String assetPath) async {
    final int result = await _platform.invokeMethod('setIcon', assetPath);
    return result > 0 ? "Icon set" : "There was an error.";
  }

  ///Set a custom [Title] to be displayed in the notification bar for the chathead.
  Future<String> setNotificationTitle(String title) async {
    final int result =
        await _platform.invokeMethod('setNotificationTitle', title);
    return result > 0 ? "Notification Title set" : "There was an error.";
  }

  /// Set a custom [IconTitle] to be displayed in the notification bar for the chathead.
  /// Please note that in some cases, this is gonna ignore any asset given, and instead
  /// use the default icon launcher.
  Future<String> setNotificationIcon(String assetPath) async {
    final int result =
        await _platform.invokeMethod('setNotificationIcon', assetPath);
    return result > 0 ? "NotificationIcon set" : "There was an error.";
  }

  /// Set a custom [Close Icon] to be displayed when the chathead is dragged.
  Future<String> setCloseIcon(String assetPath) async {
    final int result = await _platform.invokeMethod('setCloseIcon', assetPath);
    return result > 0 ? "Close Icon set" : "There was an error.";
  }

  /// Set a custom [Close Background] to be displayed behind the [Close Icon].
  Future<String> setCloseBackgroundIcon(String assetPath) async {
    final int result =
        await _platform.invokeMethod('setBackgroundCloseIcon', assetPath);
    return result > 0 ? "Close Icon Background set" : "There was an error.";
  }

  /// Close the [chathead].
  void closeHead() {
    if (_isOpen) {
      _platform.invokeMethod('close');
      _timer.cancel();
      _isOpen = false;
    } else
      throw Exception('Floaty Head not running');
  }

  /// This functions updates all the UI that is builded in the custom layout
  /// that the chathead uses.
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

/// Notify to the sender/caller that a widget has been pressed.
void callbackDispatcher() {
  const MethodChannel _backgroundChannel =
      const MethodChannel('ni.devotion.floaty_head/background');
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
