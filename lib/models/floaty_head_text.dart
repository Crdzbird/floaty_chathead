import 'package:floaty_head/floaty_head.dart';
import 'package:flutter/material.dart';

class FloatyHeadText {
  String text;
  double fontSize;
  Color textColor;
  FontWeight fontWeight;
  FloatyHeadPadding padding;

  FloatyHeadText(
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
      'fontWeight': Commons.getFontWeight(fontWeight),
      'textColor': textColor?.value ?? Colors.black.value,
      'padding': padding?.getMap(),
    };
    return map;
  }
}
