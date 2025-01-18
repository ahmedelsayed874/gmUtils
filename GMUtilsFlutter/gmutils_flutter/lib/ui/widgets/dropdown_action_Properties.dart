
import 'package:flutter/material.dart';

class DropdownActionProperties {
  IconData icon;
  Color? iconColor;
  String text;
  Color? textColor;

  DropdownActionProperties({
    required this.icon,
    this.iconColor,
    required this.text,
    this.textColor,
  });
}