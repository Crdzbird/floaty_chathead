import 'package:floaty_head/models/system_window_padding.dart';
import 'package:flutter/material.dart';

class SystemWindowText {
  String text;
  double fontSize;
  Color textColor;
  FontWeight fontWeight;
  SystemWindowPadding padding;

  SystemWindowText(
      {@required this.text,
      this.fontSize,
      this.fontWeight,
      this.textColor,
      this.padding})
      : assert(text != null);

  Map<String, dynamic> getMap() {
    final Map<String, dynamic> map = <String, dynamic>{
      'text': text,
      'fontSize': fontSize ?? 14.0,
      'fontWeight': "bold",
      'textColor': textColor?.value ?? Colors.black.value,
      'padding': padding?.getMap(),
    };
    return map;
  }
}
