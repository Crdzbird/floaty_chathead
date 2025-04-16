import 'dart:async';
import 'dart:io';
import 'dart:ui';

import 'package:flutter/services.dart';
import 'package:flutter/widgets.dart';

/// This is called when a button is tapped, the return is gonna be.
/// ```dart
/// OnClickListener(String tag) => 'btn_ok';
/// ```
typedef void OnClickListener(String tag);

final class FloatyHead {
  bool _isOpen = false;

  /// Return the [state] of the chathead
  /// ```dart
  /// bool get isOpen => true or false;
  /// ```
  bool get isOpen => _isOpen;

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
    _isOpen = true;
    _platform.invokeMethod('start');
  }

  /// If a [widget] is [pressed] check the [type] of [tap].
  /// and returns to the client-dart the component that has been pressed as a
  /// [string] with his tag.
  static Future<bool> registerOnClickListener(
    OnClickListener callBackFunction,
  ) async {
    final callBackDispatcher =
        PluginUtilities.getCallbackHandle(_callbackDispatcher)!;
    final callBack = PluginUtilities.getCallbackHandle(callBackFunction)!;
    _platform.setMethodCallHandler(
      (MethodCall call) {
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
          }
          as Future<dynamic> Function(MethodCall)?,
    );
    await _platform.invokeMethod("registerCallBackHandler", <dynamic>[
      callBackDispatcher.toRawHandle(),
      callBack.toRawHandle(),
    ]);
    return true;
  }

  ///Set a custom [icon] for the chathead.
  Future<String> setIcon(String assetPath) async {
    final int result =
        await (_platform.invokeMethod('setIcon', assetPath) as FutureOr<int>);
    return result > 0 ? "Icon set" : "There was an error.";
  }

  ///Set a custom [Title] to be displayed in the notification bar for the chathead.
  Future<String> setNotificationTitle(String title) async {
    final int result =
        await (_platform.invokeMethod('setNotificationTitle', title)
            as FutureOr<int>);
    return result > 0 ? "Notification Title set" : "There was an error.";
  }

  /// Set a custom [IconTitle] to be displayed in the notification bar for the chathead.
  /// Please note that in some cases, this is gonna ignore any asset given, and instead
  /// use the default icon launcher.
  Future<String> setNotificationIcon(String assetPath) async {
    final int result =
        await (_platform.invokeMethod('setNotificationIcon', assetPath)
            as FutureOr<int>);
    return result > 0 ? "NotificationIcon set" : "There was an error.";
  }

  /// Set a custom [Close Icon] to be displayed when the chathead is dragged.
  Future<String> setCloseIcon(String assetPath) async {
    final int result =
        await (_platform.invokeMethod('setCloseIcon', assetPath)
            as FutureOr<int>);
    return result > 0 ? "Close Icon set" : "There was an error.";
  }

  /// Set a custom [Close Background] to be displayed behind the [Close Icon].
  Future<String> setCloseBackgroundIcon(String assetPath) async {
    final int result =
        await (_platform.invokeMethod('setBackgroundCloseIcon', assetPath)
            as FutureOr<int>);
    return result > 0 ? "Close Icon Background set" : "There was an error.";
  }

  /// Close the [chathead].
  void closeHead() {
    if (!_isOpen) return;
    _isOpen = false;
    _platform.invokeMethod('close');
  }
}

/// Notify to the sender/caller that a widget has been pressed.
void _callbackDispatcher() {
  const MethodChannel _backgroundChannel = const MethodChannel(
    'ni.devotion.floaty_head/background',
  );
  WidgetsFlutterBinding.ensureInitialized();
  _backgroundChannel.setMethodCallHandler((MethodCall call) async {
    final args = call.arguments;
    final Function callback =
        PluginUtilities.getCallbackFromHandle(
          CallbackHandle.fromRawHandle(args[0]),
        )!;
    final type = args[1];
    if (type == "onClick") {
      final tag = args[2];
      callback(tag);
    }
  });
}
