import 'package:floaty_head/models/system_window_decoration.dart';
import 'package:floaty_head/models/system_window_margin.dart';
import 'package:floaty_head/models/system_window_padding.dart';
import 'package:floaty_head/models/system_window_text.dart';
import 'package:flutter/material.dart';

class SystemWindowButton {
  static const int MATCH_PARENT = -1;
  static const int WRAP_CONTENT = -2;

  SystemWindowText text;
  SystemWindowPadding padding;
  SystemWindowMargin margin;
  int width;
  int height;
  String tag;
  SystemWindowDecoration decoration;

  SystemWindowButton(
      {@required this.text,
      @required this.tag,
      this.padding,
      this.margin,
      this.width,
      this.height,
      this.decoration})
      : assert(text != null, tag != null);

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
