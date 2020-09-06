import 'package:floaty_head/floaty_head.dart';

class FloatyHeadBody {
  List<EachRow> rows;
  FloatyHeadPadding padding;
  FloatyHeadDecoration decoration;

  FloatyHeadBody({
    this.rows,
    this.padding,
    this.decoration,
  });

  Map<String, dynamic> getMap() {
    final Map<String, dynamic> map = <String, dynamic>{
      'rows': (rows == null)
          ? null
          : List<dynamic>.from(rows.map((x) => x?.getMap())),
      'padding': padding?.getMap(),
      'decoration': decoration?.getMap()
    };
    return map;
  }
}

class EachRow {
  List<EachColumn> columns;
  FloatyHeadPadding padding;
  FloatyHeadMargin margin;
  ContentGravity gravity;
  FloatyHeadDecoration decoration;

  EachRow({
    this.columns,
    this.padding,
    this.margin,
    this.gravity,
    this.decoration,
  });

  Map<String, dynamic> getMap() {
    final Map<String, dynamic> map = <String, dynamic>{
      'columns': (columns == null)
          ? null
          : List<dynamic>.from(columns.map((x) => x?.getMap())),
      'padding': padding?.getMap(),
      'margin': margin?.getMap(),
      'gravity': Commons.getContentGravity(gravity),
      'decoration': decoration?.getMap(),
    };
    return map;
  }
}

class EachColumn {
  FloatyHeadText text;
  FloatyHeadPadding padding;
  FloatyHeadMargin margin;
  FloatyHeadDecoration decoration;

  EachColumn({
    this.text,
    this.padding,
    this.margin,
    this.decoration,
  });

  Map<String, dynamic> getMap() {
    final Map<String, dynamic> map = <String, dynamic>{
      'text': text?.getMap(),
      'padding': padding?.getMap(),
      'margin': margin?.getMap(),
      'decoration': decoration?.getMap()
    };
    return map;
  }
}
