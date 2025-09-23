import 'package:flutter/material.dart';

import '../../utils/text/text_utils.dart';

class DirectionAwareTextField extends StatefulWidget {
  final TextEditingController? controller;
  final InputDecoration? decoration;
  final TextInputType? keyboardType;
  final TextInputAction? textInputAction;
  final TextCapitalization textCapitalization;
  final TextStyle? style;
  final TextAlign textAlign;
  final TextAlignVertical? textAlignVertical;
  final TextDirection? textDirection;
  final bool autofocus;
  final String obscuringCharacter;
  final bool obscureText;
  final int? maxLines;
  final int? minLines;
  final int? maxLength;
  final bool expands;
  final bool readOnly;
  final ToolbarOptions? toolbarOptions;
  final bool? showCursor;
  final double cursorWidth;
  final double? cursorHeight;
  final Color? cursorColor;
  final ValueChanged<String>? onChanged;
  final VoidCallback? onEditingComplete;
  final ValueChanged<String>? onSubmitted;
  final GestureTapCallback? onTap;
  final InputCounterWidgetBuilder? buildCounter;
  final ScrollPhysics? scrollPhysics;
  final ScrollController? scrollController;
  final bool? enabled;
  final void Function(TextDirection)? onDirectionChanged;

  const DirectionAwareTextField({
    this.controller,
    this.decoration,
    this.keyboardType,
    this.textInputAction,
    this.textCapitalization = TextCapitalization.sentences,
    this.style,
    this.textAlign = TextAlign.start,
    this.textAlignVertical,
    this.textDirection,
    this.autofocus = false,
    this.obscuringCharacter = '•',
    this.obscureText = false,
    this.maxLines,
    this.minLines,
    this.maxLength,
    this.expands = false,
    this.readOnly = false,
    this.toolbarOptions,
    this.showCursor,
    this.cursorWidth = 2.0,
    this.cursorHeight,
    this.cursorColor,
    this.onChanged,
    this.onEditingComplete,
    this.onSubmitted,
    this.onTap,
    this.buildCounter,
    this.scrollPhysics,
    this.scrollController,
    this.enabled,
    this.onDirectionChanged,
    super.key,
  }) : super();

  @override
  State<DirectionAwareTextField> createState() =>
      _DirectionAwareTextFieldState();
}

class _DirectionAwareTextFieldState extends State<DirectionAwareTextField>  {
  TextDirection? _finalTextDirection;

  @override
  void initState() {
    super.initState();
    _finalTextDirection = widget.textDirection;

    if (widget.controller?.text.isNotEmpty == true) {
      _adjustDirection(widget.controller!.text);
    }
  }

  @override
  Widget build(BuildContext context) {
    return TextField(
      controller: widget.controller,
      decoration: widget.decoration,
      keyboardType: widget.keyboardType,
      textInputAction: widget.textInputAction,
      textCapitalization: widget.textCapitalization,
      style: widget.style,
      textAlign: widget.textAlign,
      textAlignVertical: widget.textAlignVertical,
      textDirection: _finalTextDirection,
      autofocus: widget.autofocus,
      obscuringCharacter: widget.obscuringCharacter,
      obscureText: widget.obscureText,
      maxLines: widget.maxLines,
      minLines: widget.minLines,
      maxLength: widget.maxLength,
      expands: widget.expands,
      readOnly: widget.readOnly,
      toolbarOptions: widget.toolbarOptions,
      showCursor: widget.showCursor,
      cursorWidth: widget.cursorWidth,
      cursorHeight: widget.cursorHeight,
      cursorColor: widget.cursorColor,
      onChanged: (text) {
        widget.onChanged?.call(text);
        _adjustDirection(text);
      },
      onEditingComplete: widget.onEditingComplete,
      onSubmitted: widget.onSubmitted,
      onTap: widget.onTap,
      buildCounter: widget.buildCounter,
      scrollPhysics: widget.scrollPhysics,
      scrollController: widget.scrollController,
      enabled: widget.enabled,
    );
  }

  void _adjustDirection(String text) {
    if (widget.textDirection == null) {
      if (text.length == 1) {
        var e = TextUtils().isEnglishLetter(text);
        if (e != null) {
          if (e) {
            if (_finalTextDirection != TextDirection.ltr) {
              setState(() {
                _finalTextDirection = TextDirection.ltr;
                widget.onDirectionChanged?.call(_finalTextDirection!);
              });
            }
          } else {
            if (_finalTextDirection != TextDirection.rtl) {
              setState(() {
                _finalTextDirection = TextDirection.rtl;
                widget.onDirectionChanged?.call(_finalTextDirection!);
              });
            }
          }
        }
      }
    }
  }
}
