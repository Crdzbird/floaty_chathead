import 'package:flutter/material.dart';

class SystemWindowDecoration {
  Color startColor;
  Color endColor;
  int borderWidth;
  double borderRadius;
  Color borderColor;

  SystemWindowDecoration(
      {this.startColor,
      this.endColor,
      this.borderWidth,
      this.borderRadius,
      this.borderColor});

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
