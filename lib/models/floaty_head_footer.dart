import 'package:floaty_head/floaty_head.dart';

/// This class is used to build the [Footer Content] inside the [Body] that is gonna be displayed
/// when the chathead is tapped.
class FloatyHeadFooter {
  FloatyHeadText? text;
  FloatyHeadPadding? padding;
  List<FloatyHeadButton>? buttons;
  ButtonPosition? buttonsPosition;
  FloatyHeadDecoration? decoration;

  FloatyHeadFooter({
    this.text,
    this.padding,
    this.buttons,
    this.buttonsPosition,
    this.decoration,
  });

  Map<String, dynamic> getMap() {
    final Map<String, dynamic> map = <String, dynamic>{
      'isShowFooter': (text != null || (buttons != null && buttons!.length > 0)),
      'text': text?.getMap(),
      'buttons': (buttons == null)
          ? null
          : List<Map<String, dynamic>>.from(
              buttons!.map((button) => button.getMap())),
      'buttonsPosition': Commons.getPosition(buttonsPosition),
      'padding': padding?.getMap(),
      'decoration': decoration?.getMap()
    };
    return map;
  }
}
