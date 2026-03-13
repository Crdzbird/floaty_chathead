import 'dart:async';

import 'package:flutter/services.dart';

import 'generated/floaty_chathead_api.g.dart';

/// Main app API for controlling the floating chathead.
///
/// Use this from the main app isolate to show/close the chathead
/// and exchange data with the overlay.
class FloatyChathead {
  FloatyChathead._();

  static final FloatyHostApi _hostApi = FloatyHostApi();

  static const BasicMessageChannel<Object?> _messenger =
      BasicMessageChannel<Object?>(
    'ni.devotion.floaty_head/messenger',
    JSONMessageCodec(),
  );

  static final StreamController<Object?> _dataController =
      StreamController<Object?>.broadcast();

  static bool _isListening = false;

  /// Stream of messages sent from the overlay isolate.
  static Stream<Object?> get onData {
    if (!_isListening) {
      _messenger.setMessageHandler((Object? message) async {
        _dataController.add(message);
        return message;
      });
      _isListening = true;
    }
    return _dataController.stream;
  }

  /// Checks whether overlay permission is granted.
  static Future<bool> checkPermission() => _hostApi.checkPermission();

  /// Opens the system overlay permission settings screen.
  ///
  /// Returns `true` if the user granted permission.
  static Future<bool> requestPermission() => _hostApi.requestPermission();

  /// Shows the chathead with the given configuration.
  ///
  /// [entryPoint] is the name of the Dart function annotated with
  /// `@pragma("vm:entry-point")` that will be executed in the overlay
  /// isolate. Defaults to `"overlayMain"`.
  static Future<void> showChatHead({
    String entryPoint = 'overlayMain',
    int? contentWidth,
    int? contentHeight,
    String? chatheadIconAsset,
    String? closeIconAsset,
    String? closeBackgroundAsset,
    String? notificationTitle,
    String? notificationIconAsset,
    OverlayFlagMessage flag = OverlayFlagMessage.defaultFlag,
    bool enableDrag = true,
    NotificationVisibilityMessage notificationVisibility =
        NotificationVisibilityMessage.visibilityPublic,
  }) {
    return _hostApi.showChatHead(
      ChatHeadConfig(
        entryPoint: entryPoint,
        contentWidth: contentWidth,
        contentHeight: contentHeight,
        chatheadIconAsset: chatheadIconAsset,
        closeIconAsset: closeIconAsset,
        closeBackgroundAsset: closeBackgroundAsset,
        notificationTitle: notificationTitle,
        notificationIconAsset: notificationIconAsset,
        flag: flag,
        enableDrag: enableDrag,
        notificationVisibility: notificationVisibility,
      ),
    );
  }

  /// Closes the chathead and stops the overlay service.
  static Future<void> closeChatHead() => _hostApi.closeChatHead();

  /// Whether the chathead overlay is currently active.
  static Future<bool> isActive() => _hostApi.isChatHeadActive();

  /// Sends data from the main app to the overlay isolate.
  static Future<void> shareData(Object? data) => _messenger.send(data);

  /// Releases resources. Call when no longer needed.
  static void dispose() {
    _messenger.setMessageHandler(null);
    _isListening = false;
    _dataController.close();
  }
}
