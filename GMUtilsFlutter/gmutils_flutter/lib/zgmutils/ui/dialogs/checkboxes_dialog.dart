import 'dart:math';

import 'package:flutter/material.dart';

import '../../gm_main.dart';
import '../../resources/app_theme.dart';


typedef ChecksHandler = void Function(List<CheckingElement> options);
typedef CheckRules = Future<Set<CheckingElement>?> Function(
  List<CheckingElement> options,
  CheckingElement changedElement,
  bool checked,
  Set<CheckingElement> currentChecked,
);

class CheckboxesDialog {
  static double optionElementHeight = 55.0;

  static CheckboxesDialog create(
    String title,
    List<CheckingElement> options, {
    List<CheckingElement>? selectedOptions,
    required ChecksHandler checksHandler,
    CheckRules? checkRules,
        int? maxNumberOfDisplayedItems,
  }) =>
      CheckboxesDialog(
        title,
        options,
        selectedOptions: selectedOptions,
        checksHandler: checksHandler,
        checkRules: checkRules,
        maxNumberOfDisplayedItems: maxNumberOfDisplayedItems,
      );

  final String title;
  final List<CheckingElement> options;
  final List<CheckingElement>? selectedOptions;
  ChecksHandler? _checksHandler;
  CheckRules? _checkRules;
  BuildContext? _context;
  int? maxNumberOfDisplayedItems;

  CheckboxesDialog(this.title, this.options,
      {this.selectedOptions,
      required ChecksHandler checksHandler,
      CheckRules? checkRules,
      this.maxNumberOfDisplayedItems,}) {
    _checksHandler = checksHandler;
    _checkRules = checkRules;
  }

  CheckboxesDialog show(BuildContext context) {
    _context = context;

    RouteSettings routeSettings = const RouteSettings(name: 'options_dialog');

    showDialog(
      context: context,
      builder: (context) {
        return AlertDialog(
          title: Text(title),
          titleTextStyle: AppTheme.defaultTextStyle(),
          content: _dialogBody(context),
          contentPadding: const EdgeInsets.all(0),
        );
      },
      routeSettings: routeSettings,
    ).then((value) {
      _context = null;
      _checksHandler = null;
    });

    return this;
  }

  Widget _dialogBody(BuildContext context) {
    return _OptionDialogBody(
      options: options,
      selectedOptions: selectedOptions,
      checksHandler: _checksHandler,
      checkRules: _checkRules,
      dismiss: dismiss,
        maxNumberOfDisplayedItems: maxNumberOfDisplayedItems
    );
  }

  void dismiss() {
    if (_context != null) Navigator.pop(_context!);
  }
}

class _OptionDialogBody extends StatefulWidget {
  List<CheckingElement>? options;
  List<CheckingElement>? selectedOptions;
  ChecksHandler? checksHandler;
  CheckRules? checkRules;
  void Function()? dismiss;
  int? maxNumberOfDisplayedItems;

  _OptionDialogBody({
    super.key,
    required this.options,
    this.selectedOptions,
    this.checksHandler,
    this.checkRules,
    this.dismiss,
    this.maxNumberOfDisplayedItems,
  }) : super();

  @override
  _OptionDialogBodyState createState() => _OptionDialogBodyState();

  dispose() {
    options = null;
    checksHandler = null;
    checkRules = null;
    dismiss = null;
  }
}

class _OptionDialogBodyState extends State<_OptionDialogBody> {
  Set<CheckingElement> selectedOptions = <CheckingElement>{};

  @override
  void initState() {
    super.initState();

    if (widget.selectedOptions != null) {
      selectedOptions = widget.selectedOptions!.toSet();
    }
  }

  @override
  void dispose() {
    widget.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    var widgets = <Widget>[];

    //options,
    widget.options?.forEach((element) {
      var checked = selectedOptions.contains(element);

      widgets.add(
        CheckboxListTile(
          value: checked,
          onChanged: (v) => onElementCheckChanged(
            element,
            v == true,
          ),
          title: Text(element.text),
          controlAffinity: ListTileControlAffinity.leading,
        ),
      );
    });

    var body = Column(
      mainAxisSize: MainAxisSize.min,
      children: [
        Expanded(
          child: SingleChildScrollView(
            child: Column(
              mainAxisSize: MainAxisSize.min,
              mainAxisAlignment: MainAxisAlignment.start,
              children: widgets,
            ),
          ),
        ),
        //actions
        const Divider(
          height: 2,
        ),
        Row(
          children: [
            TextButton(
              onPressed: () {
                widget.dismiss?.call();
                if (widget.checksHandler != null) {
                  //if (selectedOptions.length > 0) {
                    widget.checksHandler?.call(selectedOptions.toList());
                  //}
                }
              },
              child: Text(App.isEnglish ? 'OK' : 'حسنا'),
            ),
            TextButton(
              onPressed: () {
                widget.dismiss?.call();
              },
              child: Text(App.isEnglish ? 'Cancel' : 'إلغاء'),
            ),
          ],
        ),
      ],
    );

    double? dialogHeight;
    if (widget.maxNumberOfDisplayedItems != null) {
      final h = CheckboxesDialog.optionElementHeight;

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
          : SizedBox(
              height: dialogHeight,
              child: body,
            ),
    );
  }

  void onElementCheckChanged(CheckingElement element, bool checked) async {
    if (widget.checkRules == null) {
      setState(() {
        if (checked) {
          selectedOptions.add(element);
        } else {
          selectedOptions.remove(element);
        }
      });
    } else {
      var r = await widget.checkRules
          ?.call(widget.options!, element, checked, selectedOptions);
      setState(() {
        ////print(r);
        if (r != null) {
          selectedOptions = r;
        } else {
          if (checked) {
            selectedOptions.add(element);
          } else {
            selectedOptions.remove(element);
          }
        }
      });
    }
  }
}

class CheckingElement {
  final String text;
  final Object value;

  CheckingElement(this.text, this.value);

  @override
  bool operator ==(Object other) {
    if (other is CheckingElement) {
      return other.text == text && other.value == value;
    }

    return false;
  }

  @override
  int get hashCode => text.hashCode + pow(value.hashCode, 2).toInt();

  @override
  String toString() {
    return 'CheckingElement {'
        'text: $text, '
        'value: $value'
        '}';
  }
}
