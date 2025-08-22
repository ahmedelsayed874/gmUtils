import 'package:flutter/material.dart';

class SearchBoxWidget extends StatelessWidget {
  void Function(String)? onSearchTextChanged;
  TextEditingController? textEditingController;
  String? hint;
  InputDecoration? inputDecoration;
  bool autoConvertToLowerCase;
  int delay;
  bool autofocus;

  SearchBoxWidget({
    required this.onSearchTextChanged,
    this.textEditingController,
    this.hint,
    this.inputDecoration,
    this.autoConvertToLowerCase = true,
    this.delay = 1000,
    this.autofocus = false,
    super.key,
  }) : super() {
    if (hint != null && inputDecoration != null) throw Exception();
  }

  void dispose() {
    onSearchTextChanged = null;
  }

  @override
  Widget build(BuildContext context) {
    return TextField(
      autofocus: autofocus,
      decoration: inputDecoration ?? InputDecoration(
          hintText: hint,
          border: OutlineInputBorder(borderRadius: BorderRadius.circular(15),),
          suffixIcon: const Icon(Icons.search_outlined),
          contentPadding: const EdgeInsets.symmetric(horizontal: 10),
      ),
      onChanged: (text) => _onSearchTextChanged(text),
      textInputAction: TextInputAction.search,
      maxLines: 1,
      controller: textEditingController,
    );
  }

  //----------------------------------------------------------------------------

  String searchingText = '';
  int _lastInputTime = 0;
  bool _futureSearchExecuted = false;

  void _onSearchTextChanged(String text) {
    searchingText = autoConvertToLowerCase ? text.toLowerCase() : text;
    _lastInputTime = DateTime.now().millisecondsSinceEpoch;

    if (!_futureSearchExecuted) {
      _futureSearchExecuted = true;
      _executeSearchAfterPeriod();
    }
  }

  void _executeSearchAfterPeriod() {
    Future.delayed(Duration(milliseconds: delay), () {
      var current = DateTime.now().millisecondsSinceEpoch;

      if (current - _lastInputTime >= delay - 10) {
        _futureSearchExecuted = false;
        onSearchTextChanged?.call(searchingText);
      } else {
        _executeSearchAfterPeriod();
      }
    });
  }
}
