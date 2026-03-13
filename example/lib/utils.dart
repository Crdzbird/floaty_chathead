import 'package:floaty_chathead/floaty_chathead.dart';

/// Checks overlay permission and requests it if not granted.
/// Returns `true` if the permission is available.
Future<bool> ensureOverlayPermission() async {
  final granted = await FloatyChathead.checkPermission();
  if (!granted) {
    return FloatyChathead.requestPermission();
  }
  return true;
}
