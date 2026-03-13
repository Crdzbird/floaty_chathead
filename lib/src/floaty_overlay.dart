import 'dart:async';

import 'package:flutter/services.dart';

import 'generated/floaty_chathead_api.g.dart';

/// Overlay-side API for communicating from inside the overlay back
/// to the main app.
///
/// Use this within the Dart entry point function (e.g. `overlayMain`)
/// that runs in the overlay isolate.
class FloatyOverlay implements FloatyOverlayFlutterApi {
  FloatyOverlay._();

  static final FloatyOverlay _instance = FloatyOverlay._();

  static final FloatyOverlayHostApi _overlayHostApi = FloatyOverlayHostApi();

  static const BasicMessageChannel<Object?> _messenger =
      BasicMessageChannel<Object?>(
    'ni.devotion.floaty_head/messenger',
    JSONMessageCodec(),
  );

  static final StreamController<Object?> _dataController =
      StreamController<Object?>.broadcast();

  static final StreamController<void> _tapController =
      StreamController<void>.broadcast();

  static final StreamController<void> _closeController =
      StreamController<void>.broadcast();

  static bool _isSetUp = false;

  /// Call once in your overlay entry point to set up the Flutter API handler.
  static void setUp() {
    if (!_isSetUp) {
      FloatyOverlayFlutterApi.setUp(_instance);
      _messenger.setMessageHandler((Object? message) async {
        _dataController.add(message);
        return message;
      });
      _isSetUp = true;
    }
  }

  /// Stream of messages sent from the main app.
  static Stream<Object?> get onData => _dataController.stream;

  /// Stream that emits when the chathead bubble is tapped.
  static Stream<void> get onTapped => _tapController.stream;

  /// Stream that emits when the chathead is closed.
  static Stream<void> get onClosed => _closeController.stream;

  /// Resizes the overlay content panel.
  static Future<void> resizeContent(int width, int height) =>
      _overlayHostApi.resizeContent(width, height);

  /// Updates the window flag behavior.
  static Future<void> updateFlag(OverlayFlagMessage flag) =>
      _overlayHostApi.updateFlag(flag);

  /// Closes the overlay from inside the overlay isolate.
  static Future<void> closeOverlay() => _overlayHostApi.closeOverlay();

  /// Gets the current overlay position.
  static Future<OverlayPositionMessage> getOverlayPosition() =>
      _overlayHostApi.getOverlayPosition();

  /// Sends data from the overlay to the main app.
  static Future<void> shareData(Object? data) => _messenger.send(data);

  /// Releases resources.
  static void dispose() {
    _messenger.setMessageHandler(null);
    _isSetUp = false;
    _dataController.close();
    _tapController.close();
    _closeController.close();
  }

  // FloatyOverlayFlutterApi implementation
  @override
  void onChatHeadTapped() => _tapController.add(null);

  @override
  void onChatHeadClosed() => _closeController.add(null);
}
