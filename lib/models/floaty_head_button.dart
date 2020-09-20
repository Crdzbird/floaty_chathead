import 'package:floaty_head/floaty_head.dart';
import 'package:flutter/material.dart';

/// This class is used to build the [Buttons] inside the [Body] that is gonna be displayed
/// when the chathead is tapped.
class FloatyHeadButton {
  static const int MATCH_PARENT = -1;
  static const int WRAP_CONTENT = -2;
  FloatyHeadText text;
  FloatyHeadPadding padding;
  FloatyHeadMargin margin;
  FloatyHeadDecoration decoration;
  int width;
  int height;
  String tag;

  FloatyHeadButton({
    @required this.text,
    @required this.tag,
    this.padding,
    this.margin,
    this.width,
    this.height,
    this.decoration,
  }) : assert(text != null, tag != null);

  Map<String, dynamic> getMap() {
    final Map<String, dynamic> map = <String, dynamic>{
      'text': text.getMap(),
      'tag': tag,
      'padding': padding?.getMap(),
      'margin': margin?.getMap(),
      'width': width ?? WRAP_CONTENT,
      'height': height ?? WRAP_CONTENT,
      'decoration': decoration?.getMap()
    };
    return map;
  }
}
