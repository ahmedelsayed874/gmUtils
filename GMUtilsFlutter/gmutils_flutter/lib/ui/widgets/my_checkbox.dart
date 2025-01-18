import 'package:flutter/material.dart';

class MyCheckbox extends StatefulWidget {
  final String text;
  final bool isChecked;
  final bool singleCheckMode;
  final Color? color;
  final Function(bool value) onChanged;

  const MyCheckbox({
    required this.text,
    required this.isChecked,
    this.singleCheckMode = false,
    this.color,
    required this.onChanged,
    super.key,
  });

  @override
  State<MyCheckbox> createState() => _MyCheckboxState();
}

class _MyCheckboxState extends State<MyCheckbox> {
  bool isChecked = false;

  @override
  void initState() {
    super.initState();
    isChecked = widget.isChecked;
  }

  @override
  Widget build(BuildContext context) {
    var checked = widget.singleCheckMode ? widget.isChecked : isChecked;
    return Row(
      mainAxisSize: MainAxisSize.min,
      children: [
        Checkbox(
          value: checked,
          onChanged: (b) => onCheckChanged(b == true),
          fillColor: widget.color == null
              ? null
              : WidgetStatePropertyAll(widget.color),
        ),
        //SizedBox(width: 10),
        GestureDetector(
          onTap: () => onCheckChanged(!isChecked),
          child: Text(widget.text),
        ),
      ],
    );
  }

  void onCheckChanged(bool checked) {
    if (widget.singleCheckMode) {
      widget.onChanged(checked);
    } else {
      setState(() {
        isChecked = checked;
        widget.onChanged(isChecked);
      });
    }
  }
}
