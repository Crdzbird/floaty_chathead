import 'package:flutter/material.dart';

/// This class is used to build the [Decoration] inside the [Body] that is gonna be displayed
/// when the chathead is tapped.
class FloatyHeadDecoration {
  Color? startColor;
  Color? endColor;
  int? borderWidth;
  double? borderRadius;
  Color? borderColor;

  FloatyHeadDecoration({
    this.startColor,
    this.endColor,
    this.borderWidth,
    this.borderRadius,
    this.borderColor,
  });

  Map<String, dynamic> getMap() {
    final Map<String, dynamic> map = <String, dynamic>{
      'startColor': startColor?.value ?? Colors.white.value,
      'endColor': endColor?.value,
      'borderWidth': borderWidth ?? 0,
      'borderRadius': borderRadius ?? 0.0,
      'borderColor': borderColor?.value ?? Colors.white.value
    };
    return map;
  }
}
