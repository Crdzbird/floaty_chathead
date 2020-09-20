import 'package:floaty_head/floaty_head.dart';
import 'package:flutter/material.dart';

/// This class is used to build the [Header Content] inside the [Body] that is gonna be displayed
/// when the chathead is tapped.
class FloatyHeadHeader {
  @required
  FloatyHeadText title;
  FloatyHeadText subTitle;
  FloatyHeadButton button;
  ButtonPosition buttonsPosition;
  FloatyHeadPadding padding;
  FloatyHeadDecoration decoration;

  FloatyHeadHeader({
    this.title,
    this.subTitle,
    this.button,
    this.buttonsPosition,
    this.padding,
    this.decoration,
  });

  Map<String, dynamic> getMap() {
    final Map<String, dynamic> map = <String, dynamic>{
      'title': title?.getMap(),
      'subTitle': subTitle?.getMap(),
      'button': button?.getMap(),
      'padding': padding?.getMap(),
      'buttonPosition': Commons.getPosition(buttonsPosition),
      'decoration': decoration?.getMap()
    };
    return map;
  }
}
