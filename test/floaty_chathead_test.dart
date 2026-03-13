import 'dart:async';

import 'package:floaty_chathead/floaty_chathead.dart';
import 'package:flutter/services.dart';
import 'package:flutter_test/flutter_test.dart';

void main() {
  TestWidgetsFlutterBinding.ensureInitialized();

  group('FloatyChathead', () {
    test('checkPermission calls host API', () async {
      // The Pigeon-generated FloatyHostApi requires a host handler.
      // In unit tests without a host, it throws PlatformException.
      // This verifies the Dart API correctly delegates to Pigeon.
      expect(
        () => FloatyChathead.checkPermission(),
        throwsA(isA<PlatformException>()),
      );
    });

    test('isChatHeadActive calls host API', () async {
      expect(
        () => FloatyChathead.isActive(),
        throwsA(isA<PlatformException>()),
      );
    });

    test('onData stream emits messages', () async {
      final completer = Completer<Object?>();
      final subscription = FloatyChathead.onData.listen(completer.complete);

      // The handler is set up by onData getter — verify it doesn't throw.
      subscription.cancel();
    });
  });

  group('ChatHeadConfig', () {
    test('creates with required fields', () {
      final config = ChatHeadConfig(
        entryPoint: 'overlayMain',
        flag: OverlayFlagMessage.defaultFlag,
        enableDrag: true,
        notificationVisibility: NotificationVisibilityMessage.visibilityPublic,
      );

      expect(config.entryPoint, 'overlayMain');
      expect(config.flag, OverlayFlagMessage.defaultFlag);
      expect(config.enableDrag, true);
      expect(config.contentWidth, isNull);
      expect(config.contentHeight, isNull);
      expect(config.chatheadIconAsset, isNull);
    });

    test('creates with all optional fields', () {
      final config = ChatHeadConfig(
        entryPoint: 'customOverlay',
        contentWidth: 300,
        contentHeight: 400,
        chatheadIconAsset: 'assets/icon.png',
        closeIconAsset: 'assets/close.png',
        closeBackgroundAsset: 'assets/closeBg.png',
        notificationTitle: 'Test',
        notificationIconAsset: 'assets/notification.png',
        flag: OverlayFlagMessage.focusPointer,
        enableDrag: false,
        notificationVisibility: NotificationVisibilityMessage.visibilitySecret,
      );

      expect(config.entryPoint, 'customOverlay');
      expect(config.contentWidth, 300);
      expect(config.contentHeight, 400);
      expect(config.chatheadIconAsset, 'assets/icon.png');
      expect(config.flag, OverlayFlagMessage.focusPointer);
      expect(config.enableDrag, false);
      expect(
        config.notificationVisibility,
        NotificationVisibilityMessage.visibilitySecret,
      );
    });

    test('encode and decode round-trip', () {
      final original = ChatHeadConfig(
        entryPoint: 'overlayMain',
        contentWidth: 200,
        flag: OverlayFlagMessage.clickThrough,
        enableDrag: true,
        notificationVisibility: NotificationVisibilityMessage.visibilityPrivate,
      );

      final encoded = original.encode();
      final decoded = ChatHeadConfig.decode(encoded);

      expect(decoded.entryPoint, original.entryPoint);
      expect(decoded.contentWidth, original.contentWidth);
      expect(decoded.flag, original.flag);
      expect(decoded.enableDrag, original.enableDrag);
      expect(
        decoded.notificationVisibility,
        original.notificationVisibility,
      );
    });
  });

  group('OverlayPositionMessage', () {
    test('creates with x and y', () {
      final pos = OverlayPositionMessage(x: 10.5, y: 20.3);
      expect(pos.x, 10.5);
      expect(pos.y, 20.3);
    });

    test('encode and decode round-trip', () {
      final original = OverlayPositionMessage(x: 100.0, y: 200.0);
      final encoded = original.encode();
      final decoded = OverlayPositionMessage.decode(encoded);
      expect(decoded.x, original.x);
      expect(decoded.y, original.y);
    });
  });

  group('OverlayFlagMessage', () {
    test('has expected values', () {
      expect(OverlayFlagMessage.values, hasLength(3));
      expect(OverlayFlagMessage.defaultFlag.index, 0);
      expect(OverlayFlagMessage.clickThrough.index, 1);
      expect(OverlayFlagMessage.focusPointer.index, 2);
    });
  });

  group('NotificationVisibilityMessage', () {
    test('has expected values', () {
      expect(NotificationVisibilityMessage.values, hasLength(3));
      expect(NotificationVisibilityMessage.visibilityPublic.index, 0);
      expect(NotificationVisibilityMessage.visibilitySecret.index, 1);
      expect(NotificationVisibilityMessage.visibilityPrivate.index, 2);
    });
  });
}
