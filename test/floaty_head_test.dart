import 'package:flutter/services.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:floaty_head/floaty_head.dart';

void main() {
  const MethodChannel channel = MethodChannel('floaty_head');

  TestWidgetsFlutterBinding.ensureInitialized();

  setUp(() {
    channel.setMockMethodCallHandler((MethodCall methodCall) async {
      return '42';
    });
  });

  tearDown(() {
    channel.setMockMethodCallHandler(null);
  });

  test('getPlatformVersion', () async {
    expect(await FloatyHead.platformVersion, '42');
  });
}
