import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:gmutils_flutter/zgmutils/resources/app_theme.dart';

import '../../gm_main.dart';
import '../../utils/collections/pairs.dart';

class Picker<T> {
  Future<Pair<T, int>?> show({
    required BuildContext context,
    required String title,
    required String? hint,
    required List<T> items,
    required T? initialItem,
    Widget Function(T item, int index)? itemBuilder,
    double? itemHeight,
    double? topLinePosition,
    double? bottomLinePosition,
  }) async {
    assert(items.isNotEmpty);

    return showModalBottomSheet<Pair<T, int>>(
      context: context,
      isScrollControlled: true,
      backgroundColor: Colors.transparent,
      builder: (BuildContext context) {
        return _PickerBody<T>(
          title: title,
          hint: hint,
          items: items,
          initialItem: initialItem,
          itemBuilder: itemBuilder,
          //
          itemHeight: itemHeight,
          topLinePosition: topLinePosition,
          bottomLinePosition: bottomLinePosition,
        );
      },
    );
  }
}

class _PickerBody<T> extends StatefulWidget {
  final String title;
  final String? hint;
  final List<T> items;
  final T? initialItem;
  final Widget Function(T item, int index)? itemBuilder;
  final double? itemHeight;
  final double? topLinePosition;
  final double? bottomLinePosition;

  const _PickerBody({
    required this.title,
    required this.hint,
    required this.items,
    required this.initialItem,
    required this.itemBuilder,
    required this.itemHeight,
    required this.topLinePosition,
    required this.bottomLinePosition,
  });

  @override
  State<_PickerBody> createState() => _PickerBodyState<T>();
}

class _PickerBodyState<T> extends State<_PickerBody>
    with TickerProviderStateMixin {
  late FixedExtentScrollController _scrollController;

  late T _selectedItem;
  late int _selectedItemIndex;

  late AnimationController _fadeController;
  late AnimationController _slideController;
  late Animation<double> _fadeAnimation;
  late Animation<Offset> _slideAnimation;

  @override
  void initState() {
    super.initState();

    if (widget.initialItem != null) {
      _selectedItemIndex = widget.items.indexOf(widget.initialItem);
      if (_selectedItemIndex < 0) _selectedItemIndex = 0;
    } else {
      _selectedItemIndex = 0;
    }
    _selectedItem = widget.items[_selectedItemIndex];
    _scrollController = FixedExtentScrollController(
      initialItem: _selectedItemIndex,
    );

    _fadeController = AnimationController(
      duration: const Duration(milliseconds: 200),
      vsync: this,
    );
    _slideController = AnimationController(
      duration: const Duration(milliseconds: 300),
      vsync: this,
    );

    _fadeAnimation = Tween<double>(begin: 0.0, end: 1.0).animate(
      CurvedAnimation(parent: _fadeController, curve: Curves.easeInOut),
    );
    _slideAnimation = Tween<Offset>(
      begin: const Offset(0.0, 1.0),
      end: Offset.zero,
    ).animate(
      CurvedAnimation(parent: _slideController, curve: Curves.easeOutCubic),
    );

    _slideController.forward();
    _fadeController.forward();
  }

  @override
  void dispose() {
    _scrollController.dispose();
    _fadeController.dispose();
    _slideController.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return Container(
      color: Colors.black54,
      child: SlideTransition(
        position: _slideAnimation,
        child: Align(
          alignment: Alignment.bottomCenter,
          child: FadeTransition(
            opacity: _fadeAnimation,
            child: Container(
              width: double.infinity,
              constraints: const BoxConstraints(maxHeight: 600),
              decoration: const BoxDecoration(
                color: Colors.white,
                borderRadius: BorderRadius.only(
                  topLeft: Radius.circular(20),
                  topRight: Radius.circular(20),
                ),
              ),
              child: Column(
                mainAxisSize: MainAxisSize.min,
                children: [
                  // Handle bar
                  Container(
                    margin: const EdgeInsets.only(top: 10),
                    width: 40,
                    height: 4,
                    decoration: BoxDecoration(
                      color: Colors.grey.shade300,
                      borderRadius: BorderRadius.circular(2),
                    ),
                  ),

                  // Title
                  Align(
                    alignment: Alignment.centerLeft,
                    child: Padding(
                      padding: const EdgeInsets.fromLTRB(20, 10, 0, 0),
                      child: Text(
                        widget.title,
                        style: TextStyle(
                          fontSize: AppTheme.appMeasurement?.screenTitleSize,
                          fontFamily: AppTheme.defaultFontFamily,
                          fontWeight: FontWeight.bold,
                          color: Theme.of(context).primaryColor,
                        ),
                      ),
                    ),
                  ),

                  //hint
                  if (widget.hint != null)
                    Align(
                      alignment: Alignment.centerLeft,
                      child: Padding(
                        padding: const EdgeInsets.fromLTRB(22, 5, 0, 0),
                        child: Text(
                          widget.hint!,
                          style: TextStyle(
                            fontSize: 12,
                            fontFamily: AppTheme.defaultFontFamily,
                            color: AppTheme.appColors?.text.withAlpha(200),
                          ),
                        ),
                      ),
                    ),

                  const SizedBox(height: 10),

                  // Time roller
                  _buildTimeRoller(),

                  const SizedBox(height: 20),

                  // Action buttons
                  Padding(
                    padding: const EdgeInsets.all(20),
                    child: Row(
                      children: [
                        Expanded(
                          /*child: Widgets.instant.customButton(
                            text: Res.strings.apply,
                            onPressed: _confirm,
                          ),*/
                          child: ElevatedButton(
                            onPressed: _confirm,
                            child: Text(
                              App.isEnglish ? 'Confirm' : 'تطبيق',
                            ),
                          ),
                        ),
                        const SizedBox(width: 16),
                        Expanded(
                          /*child: Widgets.instant.customButton2(
                            text: Res.strings.cancel,
                            onPressed: _cancelSelection,
                          ),*/
                          child: ElevatedButton(
                            onPressed: _cancelSelection,
                            child: Text(
                              App.isEnglish ? 'Cancel' : 'إلغاء',
                            ),
                          ),
                        ),
                      ],
                    ),
                  ),

                  // Safe area padding
                  SizedBox(height: MediaQuery.of(context).padding.bottom),
                ],
              ),
            ),
          ),
        ),
      ),
    );
  }

  //------------------------------------

  Widget _buildTimeRoller() {
    return Container(
      height: 200,
      padding: const EdgeInsets.symmetric(horizontal: 20),
      child: Row(
        children: [
          Expanded(
            child: Stack(
              children: [
                ListWheelScrollView.useDelegate(
                  controller: _scrollController,
                  itemExtent: widget.itemHeight ?? 50,
                  physics: const FixedExtentScrollPhysics(),
                  onSelectedItemChanged: onSelectedItemChanged,
                  childDelegate: itemListBuildDelegate(),
                ),
                //
                Align(
                  alignment: Alignment.center,
                  child: Container(
                    color: Theme.of(context).primaryColor.withOpacity(0.2),
                    width: double.maxFinite,
                    height: 1,
                    margin: EdgeInsets.only(
                      bottom: widget.topLinePosition ?? 47,
                    ),
                  ),
                ),
                Align(
                  alignment: Alignment.center,
                  child: Container(
                    color: Theme.of(context).primaryColor.withOpacity(0.2),
                    width: double.maxFinite,
                    height: 1,
                    margin: EdgeInsets.only(
                      top: widget.bottomLinePosition ?? 47,
                    ),
                  ),
                ),
              ],
            ),
          ),
        ],
      ),
    );
  }

  onSelectedItemChanged(int index) {
    HapticFeedback.selectionClick();
    setState(() {
      _selectedItem = widget.items[index];
      _selectedItemIndex = index;
    });
  }

  itemListBuildDelegate() {
    return ListWheelChildBuilderDelegate(
      builder: (context, index) {
        final item = widget.items[index];

        if (widget.itemBuilder != null) {
          return widget.itemBuilder!(item, index);
        }
        //
        final isSelected = index == _selectedItemIndex;
        return Center(
          child: Text(
            item.toString(),
            style: TextStyle(
              fontSize: isSelected ? 20 : 14,
              fontWeight: isSelected ? FontWeight.bold : FontWeight.normal,
              color: isSelected ? Theme.of(context).primaryColor : Colors.grey,
            ),
          ),
        );
      },
      childCount: widget.items.length,
    );
  }

  //------------------------------------

  void _confirm() async {
    await _slideController.reverse();
    if (mounted) {
      Navigator.of(context).pop(
        Pair(value1: _selectedItem, value2: _selectedItemIndex),
      );
    }
  }

  void _cancelSelection() async {
    await _slideController.reverse();
    if (mounted) {
      Navigator.of(context).pop();
    }
  }
}
