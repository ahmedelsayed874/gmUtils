import 'dart:math';

import 'package:flutter/material.dart';

import '../../gm_main.dart';
import '../../resources/app_theme.dart';

typedef OptionSelectHandler = void Function(OptionElement choice);

class OptionsDialog {
  static double optionElementHeight = 55.0;

  static OptionsDialog create(
    String title,
    List<OptionElement> options, {
    OptionElement? selectedOption,
    required OptionSelectHandler optionSelectHandler,
    int? maxNumberOfDisplayedItems,
  }) =>
      OptionsDialog(
        title,
        options,
        selectedOption: selectedOption,
        optionSelectHandler: optionSelectHandler,
        maxNumberOfDisplayedItems: maxNumberOfDisplayedItems,
      );

  final String title;
  final List<OptionElement> options;
  final OptionElement? selectedOption;
  OptionSelectHandler? _optionSelectHandler;
  BuildContext Function()? _context;
  int? maxNumberOfDisplayedItems;

  OptionsDialog(
    this.title,
    this.options, {
    this.selectedOption,
    required OptionSelectHandler optionSelectHandler,
    this.maxNumberOfDisplayedItems,
  }) {
    _optionSelectHandler = optionSelectHandler;
  }

  OptionsDialog show(BuildContext Function() context) {
    _context = context;

    RouteSettings routeSettings = const RouteSettings(name: 'options_dialog');

    showDialog(
      context: context(),
      builder: (context) {
        return AlertDialog(
          title: Text(
            title,
            style: AppTheme.defaultTextStyle(
              fontWeight: FontWeight.bold,
            ),
          ),
          titleTextStyle: AppTheme.defaultTextStyle(),
          content: _dialogBody(context),
          contentPadding: const EdgeInsets.all(0),
        );
      },
      routeSettings: routeSettings,
    ).then((value) {
      _context = null;
      _optionSelectHandler = null;
    });

    return this;
  }

  Widget _dialogBody(BuildContext context) {
    return _OptionDialogBody(
      options: options,
      selectedOption: selectedOption,
      optionSelectHandler: _optionSelectHandler,
      dismiss: dismiss,
      maxNumberOfDisplayedItems: maxNumberOfDisplayedItems,
    );
  }

  void dismiss() {
    if (_context != null) Navigator.pop(_context!());
  }
}

class _OptionDialogBody extends StatefulWidget {
  List<OptionElement>? options;
  OptionElement? selectedOption;
  OptionSelectHandler? optionSelectHandler;
  void Function()? dismiss;
  int? maxNumberOfDisplayedItems;

  _OptionDialogBody({
    Key? key,
    required this.options,
    required this.selectedOption,
    this.optionSelectHandler,
    this.dismiss,
    this.maxNumberOfDisplayedItems,
  }) : super(key: key);

  @override
  _OptionDialogBodyState createState() => _OptionDialogBodyState();

  dispose() {
    options = null;
    optionSelectHandler = null;
    dismiss = null;
  }
}

class _OptionDialogBodyState extends State<_OptionDialogBody> {
  @override
  void dispose() {
    widget.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    if (widget.selectedOption == null) {
      if (widget.options?.isNotEmpty == true) {
        widget.selectedOption = widget.options?.first;
      }
    }

    var listView = ListView.builder(
        itemCount: widget.options?.length ?? 0,
        itemBuilder: (context, index) {
          var element = widget.options![index];
          return ListTile(
            leading: Radio(
              value: element,
              groupValue: widget.selectedOption,
              onChanged: (option) {
                setState(() {
                  widget.selectedOption = option as OptionElement?;
                });
              },
            ),
            title: Text(element.text),
            onTap: () {
              setState(() {
                widget.selectedOption = element;
              });
            },
          );
        });

    var body = Container(
      width: double.maxFinite,
      child: Column(
        children: [
          Expanded(child: listView),
          //actions
          Divider(
            height: 2,
            color: AppTheme.appColors?.hint,
          ),
          Row(
            children: [
              TextButton(
                onPressed: () {
                  widget.dismiss?.call();

                  if (widget.optionSelectHandler != null) {
                    if (widget.selectedOption != null) {
                      widget.optionSelectHandler!(widget.selectedOption!);
                    }
                  }
                },
                child: Text(
                  App.isEnglish ? 'OK' : 'حسنا',
                  style: AppTheme.defaultTextStyle(
                    fontWeight: FontWeight.bold,
                    textColor: AppTheme.appColors?.primary,
                  ),
                ),
              ),
              TextButton(
                onPressed: () {
                  widget.dismiss?.call();
                },
                child: Text(
                  App.isEnglish ? 'Cancel' : 'إلغاء',
                  style: AppTheme.defaultTextStyle(
                    fontWeight: FontWeight.bold,
                    //textColor: AppTheme.appColors?.primary,
                  ),
                ),
              ),
            ],
          )
        ],
      ),
    );

    double? dialogHeight;
    if (widget.maxNumberOfDisplayedItems != null) {
      final h = OptionsDialog.optionElementHeight;

      dialogHeight = h + (widget.options?.length ?? 0) * h;
      var maxNumberOfDisplayedItems = h + widget.maxNumberOfDisplayedItems! * h;

      if (dialogHeight > maxNumberOfDisplayedItems) {
        dialogHeight = maxNumberOfDisplayedItems;
      }
    }

    return DefaultTextStyle(
      style: AppTheme.defaultTextStyle(),
      child: dialogHeight == null
          ? body
          : Container(child: body, height: dialogHeight),
    );
  }
}

class OptionElement {
  final String text;
  final Object value;

  OptionElement(this.text, this.value);

  @override
  bool operator ==(Object other) {
    if (other is OptionElement) {
      return other.text == text && other.value == value;
    }

    return false;
  }

  @override
  int get hashCode => text.hashCode + pow(value.hashCode, 2).toInt();
}
