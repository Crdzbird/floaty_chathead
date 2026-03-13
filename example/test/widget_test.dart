import 'package:flutter/material.dart';
import 'package:flutter_test/flutter_test.dart';

import 'package:floaty_chathead_example/main.dart';

void main() {
  testWidgets('GalleryPage renders all example entries', (
    WidgetTester tester,
  ) async {
    await tester.pumpWidget(const MaterialApp(home: GalleryPage()));

    expect(find.text('Floaty Chathead Examples'), findsOneWidget);
    expect(find.text('Basic (Original)'), findsOneWidget);
    expect(find.text('Messenger Chat'), findsOneWidget);
    expect(find.text('Mini Player'), findsOneWidget);
    expect(find.text('Quick Actions'), findsOneWidget);
    expect(find.text('Notification Counter'), findsOneWidget);
    expect(find.text('Timer / Stopwatch'), findsOneWidget);
  });

  testWidgets('HomePage renders correctly', (WidgetTester tester) async {
    await tester.pumpWidget(const MaterialApp(home: HomePage()));

    expect(find.text('Basic Chathead'), findsOneWidget);
    expect(find.text('Show Chathead'), findsOneWidget);
    expect(find.text('Close Chathead'), findsOneWidget);
    expect(find.text('Send Data to Overlay'), findsOneWidget);
  });
}
