import 'dart:math';

import 'package:flutter/material.dart';

import '../../gm_main.dart';
import '../../resources/app_theme.dart';

typedef OptionSelectHandler<T> = void Function(OptionElement<T> choice);

class OptionsDialog<T> {
  static OptionsDialog<T> create<T>(
    String title,
    List<OptionElement<T>> options, {
    OptionElement<T>? selectedOption,
    required OptionSelectHandler<T> optionSelectHandler,
    int? maxNumberOfDisplayedItems,
    void Function(bool? ok)? onDismiss,
  }) =>
      OptionsDialog<T>(
        title,
        options,
        selectedOption: selectedOption,
        optionSelectHandler: optionSelectHandler,
        maxNumberOfDisplayedItems: maxNumberOfDisplayedItems,
        onDismiss: onDismiss,
      );

  final String title;
  final List<OptionElement<T>> options;
  final OptionElement<T>? selectedOption;
  OptionSelectHandler<T>? _optionSelectHandler;
  BuildContext Function()? _context;
  int? maxNumberOfDisplayedItems;
  late final double estimatedOptionHeight;
  void Function(bool? ok)? _onDismiss;

  OptionsDialog(
    this.title,
    this.options, {
    this.selectedOption,
    required OptionSelectHandler<T> optionSelectHandler,
    this.maxNumberOfDisplayedItems,
    double? estimatedOptionHeight,
    void Function(bool? ok)? onDismiss,
  })  : estimatedOptionHeight = estimatedOptionHeight ?? 55,
        _optionSelectHandler = optionSelectHandler,
        _onDismiss = onDismiss;

  bool? _dismissedByOk;

  OptionsDialog<T> show(BuildContext Function() context) {
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

      _onDismiss?.call(_dismissedByOk);
      _onDismiss = null;
    });

    return this;
  }

  Widget _dialogBody(BuildContext context) {
    return _OptionDialogBody<T>(
      options: options,
      selectedOption: selectedOption,
      optionSelectHandler: _optionSelectHandler,
      dismiss: _dismiss,
      maxNumberOfDisplayedItems: maxNumberOfDisplayedItems,
      estimatedOptionHeight: estimatedOptionHeight,
    );
  }

  void _dismiss(bool ok) {
    _dismissedByOk = ok;
    dismiss();
  }

  void dismiss() {
    if (_context != null) Navigator.pop(_context!());
  }
}

class _OptionDialogBody<T> extends StatefulWidget {
  List<OptionElement<T>>? options;
  OptionElement<T>? selectedOption;
  OptionSelectHandler<T>? optionSelectHandler;
  void Function(bool ok)? dismiss;
  int? maxNumberOfDisplayedItems;
  double estimatedOptionHeight;

  _OptionDialogBody({
    Key? key,
    required this.options,
    required this.selectedOption,
    this.optionSelectHandler,
    this.dismiss,
    this.maxNumberOfDisplayedItems,
    required this.estimatedOptionHeight,
  }) : super(key: key);

  @override
  _OptionDialogBodyState<T> createState() => _OptionDialogBodyState<T>();

  dispose() {
    options = null;
    optionSelectHandler = null;
    dismiss = null;
  }
}

class _OptionDialogBodyState<T> extends State<_OptionDialogBody<T>> {
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
                  widget.selectedOption = option as OptionElement<T>?;
                });
              },
            ),
            title: Text(
              element.text,
              style: AppTheme.defaultTextStyle(),
            ),
            onTap: () {
              setState(() {
                widget.selectedOption = element;
              });
            },
          );
        });

    var body = SizedBox(
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
                  widget.dismiss?.call(true);

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
                  widget.dismiss?.call(false);
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
      final h = widget.estimatedOptionHeight;

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
          : SizedBox(height: dialogHeight, child: body),
    );
  }
}

class OptionElement<T> {
  final String text;
  final T value;

  OptionElement(this.text, this.value);

  @override
  bool operator ==(Object other) {
    if (other is OptionElement<T>) {
      return other.text == text && other.value == value;
    }

    return false;
  }

  @override
  int get hashCode => text.hashCode + pow(value.hashCode, 2).toInt();
}
