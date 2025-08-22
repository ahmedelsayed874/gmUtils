import 'package:flutter/material.dart';

import '../../gm_main.dart';
import '../../resources/app_theme.dart';
import '../widgets/search_box_widget.dart';

class SearchDialog {
  static double listElementHeight = 90;

  BuildContext Function()? _context;
  bool _dismissed = false;

  SearchDialogArguments searchDialogArguments;

  SearchDialog(this.searchDialogArguments)
      : assert(searchDialogArguments.onSearchTextChanged != null),
        assert(searchDialogArguments.onBuildListElement != null ||
            searchDialogArguments.onBuildListElementAlter != null),
        assert(searchDialogArguments.onResultSelected != null);

  int _tries = 3;

  SearchDialog show(BuildContext Function() context) {
    try {
      _show(context);
    } catch (e) {
      Future.delayed(const Duration(milliseconds: 500), () {
        if (--_tries == 0) {
          _context = null;
          return;
        }

        if (!_dismissed && _context != null) {
          show(_context!);
        } else {
          _context = null;
        }
      });
    }

    return this;
  }

  SearchDialog _show(BuildContext Function() context) {
    _context = context;

    RouteSettings routeSettings = const RouteSettings(name: 'search_dialog');

    showDialog(
      context: context(),
      builder: (context) {
        return Material(
            type: MaterialType.transparency,
            child: _SearchDialogWidget(
              searchDialogArguments: searchDialogArguments,
              dismiss: dismiss,
            ));
      },
      routeSettings: routeSettings,
    ).then((value) => _context = null);

    return this;
  }

  void dismiss() {
    _dismissed = true;
    if (_context != null) {
      // try {
      Navigator.pop(_context!());
      // } catch (e) {
      //   _dismissed = false;
      //   Future.delayed(Duration(milliseconds: 700), () {
      //     dismiss();
      //   });
      // }
    }
  }
}

class SearchDialogArguments {
  String? title;
  String hint;
  Future<List<dynamic>?> Function(String)? onSearchTextChanged;
  Widget Function(dynamic)? onBuildListElement;
  List<String> Function(dynamic)? onBuildListElementAlter;
  void Function(dynamic)? onResultSelected;
  int? maxNumberOfDisplayedItems;

  SearchDialogArguments({
    this.title,
    required this.hint,
    required this.onSearchTextChanged,
    required this.onBuildListElement,
    required this.onBuildListElementAlter,
    required this.onResultSelected,
    this.maxNumberOfDisplayedItems,
  });
}

//==============================================================================

class _SearchDialogWidget extends StatefulWidget {
  SearchDialogArguments searchDialogArguments;
  Function() dismiss;

  _SearchDialogWidget({
    required this.searchDialogArguments,
    required this.dismiss,
    super.key,
  }) : super();

  List<dynamic>? _result;

  @override
  State<_SearchDialogWidget> createState() => _SearchDialogWidgetState();
}

class _SearchDialogWidgetState extends State<_SearchDialogWidget> {
  @override
  void dispose() {
    widget.searchDialogArguments.onSearchTextChanged = null;
    widget.searchDialogArguments.onBuildListElement = null;
    widget.searchDialogArguments.onBuildListElementAlter = null;
    widget.searchDialogArguments.onResultSelected = null;
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    List<Widget> children = [];

    //title
    children.add(
      Center(
        child: Text(
          widget.searchDialogArguments.title ?? (App.isEnglish ? 'search' : 'بحث'),
          style: AppTheme.textStyleOfScreenTitle(),
        ),
      ),
    );

    //search box
    children.add(
      SearchBoxWidget(
        hint: widget.searchDialogArguments.hint,
        onSearchTextChanged: (text) async {
          widget._result = await widget
              .searchDialogArguments.onSearchTextChanged
              ?.call(text);
          setState(() {});
        },
      ),
    );

    //result list
    if (widget._result != null) {
      if ((widget._result?.length ?? 0) > 0) {
        var listView = ListView.builder(
            itemCount: widget._result?.length ?? 0,
            itemBuilder: (context, index) {
              var element = widget._result![index];
              Widget elementWidget;

              if (widget.searchDialogArguments.onBuildListElement != null) {
                elementWidget =
                    widget.searchDialogArguments.onBuildListElement!(element);
              } else if (widget.searchDialogArguments.onBuildListElementAlter !=
                  null) {
                var lines = widget
                    .searchDialogArguments.onBuildListElementAlter!(element);
                if (lines.isEmpty) {
                  elementWidget = const Text('NO-LINES-PROVIDED');
                } else {
                  List<Text> texts = [];
                  for (var i = 0; i < lines.length; i++) {
                    texts.add(
                      Text(
                        lines[i],
                        style: AppTheme.defaultTextStyle(
                            fontWeight: i == 0 ? FontWeight.bold : null,
                            textColor: i == 0
                                ? AppTheme.appColors?.text
                                : AppTheme.appColors?.title),
                      ),
                    );
                  }
                  texts.add(const Text(''));

                  elementWidget = Column(
                    mainAxisSize: MainAxisSize.min,
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: texts,
                  );
                }
              } else {
                elementWidget = Text(element.toString());
              }

              return GestureDetector(
                onTap: () {
                  widget.dismiss();
                  widget.searchDialogArguments.onResultSelected?.call(element);
                },
                child: Padding(
                  padding: const EdgeInsets.all(8.0),
                  child: elementWidget,
                ),
              );
            });

        int elements =
            widget.searchDialogArguments.maxNumberOfDisplayedItems ?? 5;

        final h = SearchDialog.listElementHeight;
        double dialogHeight = (widget._result?.length ?? 0) * h;
        double maxNumberOfDisplayedItems = elements * h;
        if (dialogHeight > maxNumberOfDisplayedItems) {
          dialogHeight = maxNumberOfDisplayedItems;
        }

        children.add(const SizedBox(height: 10));
        children.add(SizedBox(
          height: dialogHeight,
          child: listView,
        ));
      } else {
        children.add(const SizedBox(height: 10));
        children.add(Center(
          child: Text(App.isEnglish ? 'No data' : 'لا توجد بيانات'),
        ));
      }
    }

    //close button
    children.add(const SizedBox(height: 10));
    children.add(TextButton(
      onPressed: () => widget.dismiss(),
      child: Text(App.isEnglish ? 'Close' : 'إغلاق'),
    ));

    return Center(
      child: Container(
        margin: const EdgeInsets.all(15),
        padding: const EdgeInsets.all(15),
        color: Colors.white,
        child: Column(
          mainAxisSize: MainAxisSize.min,
          children: children,
        ),
      ),
    );
  }
}
