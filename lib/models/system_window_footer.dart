import 'package:floaty_head/models/system_window_button.dart';
import 'package:floaty_head/models/system_window_decoration.dart';
import 'package:floaty_head/models/system_window_padding.dart';
import 'package:floaty_head/models/system_window_text.dart';

class SystemWindowFooter {
  SystemWindowText text;
  SystemWindowPadding padding;
  List<SystemWindowButton> buttons;
  SystemWindowDecoration decoration;

  SystemWindowFooter({this.text, this.padding, this.buttons, this.decoration});

  Map<String, dynamic> getMap() {
    final Map<String, dynamic> map = <String, dynamic>{
      'isShowFooter': (text != null || (buttons != null && buttons.length > 0)),
      'text': text?.getMap(),
      'buttons': (buttons == null)
          ? null
          : List<Map<String, dynamic>>.from(
              buttons.map((button) => button.getMap())),
      'buttonsPosition': 1,
      'padding': padding?.getMap(),
      'decoration': decoration?.getMap()
    };
    return map;
  }
}
