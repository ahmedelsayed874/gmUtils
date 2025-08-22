import 'package:flutter/material.dart';

import '../../resources/app_theme.dart';

class Widgets {
  static Widget title(
    String text, {
    Color? textColor,
    double? fontSize,
    TextAlign? textAlign,
  }) {
    return Text(
      text,
      textAlign: textAlign,
      style: AppTheme.textStyleOfSectionTitle (
        textColor: textColor,
        textSize: fontSize,
      ),
    );
  }

  static Widget inputBox(
    String hint, {
    TextEditingController? controller,
    ValueChanged<String>? onChanged,
    bool forPassword = false,
    TextInputType? inputType,
    TextInputAction? textInputAction,
    VoidCallback? onEditingComplete,
    TextAlign textAlign = TextAlign.start,
    int? minLines,
    int? maxLines = 1,
        bool useUnderlineStyle = false,
  }) {
    return TextField(
      decoration: InputDecoration(
        hintText: hint,
        contentPadding: const EdgeInsets.symmetric(vertical: 5, horizontal: 12),
        border: useUnderlineStyle ? const UnderlineInputBorder() : OutlineInputBorder(
          borderRadius: BorderRadius.circular(6),
          gapPadding: 1,
        ),
      ),
      textAlign: textAlign,
      obscureText: forPassword,
      controller: controller,
      onChanged: onChanged,
      keyboardType: inputType,
      textInputAction: textInputAction,
      onEditingComplete: onEditingComplete,
      minLines: minLines,
      maxLines: maxLines,
    );
  }

  static List<Widget> inputBoxWithFrame({
    required String title,
    required String hint,
    required TextEditingController controller,
    required TextInputAction textInputAction,
    required TextInputType inputType,
    bool obscureText = false,
    int? minLines,
    int? maxLines = 1,
    int? maxLength,
  }) {
    return [
      Widgets.title(title),
      const SizedBox(height: 10),
      TextField(
        controller: controller,
        textInputAction: textInputAction,
        keyboardType: inputType,
        decoration: InputDecoration(
          border: const OutlineInputBorder(),
          hintText: hint,
        ),
        obscureText: obscureText,
        minLines: minLines,
        maxLines: maxLines,
        maxLength: maxLength,
      )
    ];
  }
}
