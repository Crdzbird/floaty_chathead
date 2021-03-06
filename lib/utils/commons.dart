import 'package:floaty_head/floaty_head.dart';

class Commons {
  /// Replace the [windowGravity] setted in dart-client code.
  static String getWindowGravity(FloatyHeadGravity? gravity) {
    if (gravity == null) gravity = FloatyHeadGravity.top;
    switch (gravity) {
      case FloatyHeadGravity.center:
        return "center";
      case FloatyHeadGravity.bottom:
        return "bottom";
      case FloatyHeadGravity.top:
      default:
        return "top";
    }
  }

  /// Replace the [contentGravity] setted in dart-client code.
  static String getContentGravity(ContentGravity? gravity) {
    if (gravity == null) gravity = ContentGravity.left;
    switch (gravity) {
      case ContentGravity.center:
        return "center";
      case ContentGravity.right:
        return "right";
      case ContentGravity.left:
      default:
        return "left";
    }
  }

  /// Replace the [position] setted in dart-client code.
  static String getPosition(ButtonPosition? buttonPosition) {
    if (buttonPosition == null) buttonPosition = ButtonPosition.center;
    switch (buttonPosition) {
      case ButtonPosition.leading:
        return "leading";
      case ButtonPosition.trailing:
        return "trailing";
      case ButtonPosition.center:
      default:
        return "center";
    }
  }

  /// Replace the [fontWeight] setted in dart-client code.
  static String getFontWeight(FontWeight? fontWeight) {
    if (fontWeight == null) fontWeight = FontWeight.normal;
    switch (fontWeight) {
      case FontWeight.bold:
        return "bold";
      case FontWeight.italic:
        return "italic";
      case FontWeight.bold_italic:
        return "bold_italic";
      case FontWeight.normal:
      default:
        return "normal";
    }
  }
}
