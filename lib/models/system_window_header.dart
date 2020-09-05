import 'package:floaty_head/models/system_window_button.dart';
import 'package:floaty_head/models/system_window_decoration.dart';
import 'package:floaty_head/models/system_window_padding.dart';
import 'package:floaty_head/models/system_window_text.dart';
import 'package:flutter/material.dart';

class SystemWindowHeader {
  @required
  SystemWindowText title;
  SystemWindowText subTitle;
  SystemWindowButton button;
  SystemWindowPadding padding;
  SystemWindowDecoration decoration;

  SystemWindowHeader(
      {this.title, this.subTitle, this.button, this.padding, this.decoration});

  Map<String, dynamic> getMap() {
    final Map<String, dynamic> map = <String, dynamic>{
      'title': title?.getMap(),
      'subTitle': subTitle?.getMap(),
      'button': button?.getMap(),
      'padding': padding?.getMap(),
      'buttonPosition': "leading",
      'decoration': decoration?.getMap()
    };
    return map;
  }
}
