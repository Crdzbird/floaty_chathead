import 'package:floaty_head/floaty_head.dart';
import 'package:flutter/material.dart';

/// This class is used to build any [Text] inside the [Body] that is gonna be displayed
/// when the chathead is tapped.
class FloatyHeadText {
  String text;
  double fontSize;
  Color textColor;
  FontWeight fontWeight;
  FloatyHeadPadding padding;

  FloatyHeadText(
      {required this.text,
      /*required*/ required this.fontSize,
      /*required*/ required this.fontWeight,
      /*required*/ required this.textColor,
      /*required*/ required this.padding});

  Map<String, dynamic> getMap() {
    final Map<String, dynamic> map = <String, dynamic>{
      'text': text,
      'fontSize': fontSize,
      'fontWeight': Commons.getFontWeight(fontWeight),
      'textColor': textColor.value,
      'padding': padding.getMap(),
    };
    return map;
  }
}
