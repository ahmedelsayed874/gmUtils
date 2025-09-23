import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import '../../gm_main.dart';
import '../../resources/app_theme.dart';
import '../../utils/collections/pairs.dart';

class Picker {
  Future<List<Pair<dynamic, int>>?> show({
    required BuildContext context,
    required String title,
    required String? hint,
    required List<ListItems> lists,
    Widget Function(int listIndex, dynamic item, int index)? itemBuilder,
  }) async {
    assert(lists.isNotEmpty);

    return showModalBottomSheet(
      context: context,
      isScrollControlled: true,
      backgroundColor: Colors.transparent,
      builder: (BuildContext context) {
        return _PickerBody(
          title: title,
          hint: hint,
          lists: lists,
          itemBuilder: itemBuilder,
        );
      },
    );
  }
}

class ListItems {
  final List items;
  final dynamic initialItem;

  ListItems({required this.items, required this.initialItem})
      : assert(items.isNotEmpty);
}

//------------------------------------------------------------------------------

class _PickerBody extends StatefulWidget {
  final String title;
  final String? hint;
  final List<ListItems> lists;
  final Widget Function(int listIndex, dynamic item, int index)? itemBuilder;

  const _PickerBody({
    required this.title,
    required this.hint,
    required this.lists,
    required this.itemBuilder,
  });

  @override
  State<_PickerBody> createState() => _PickerBodyState();
}

class _UiListArgs {
  late FixedExtentScrollController _scrollController;
  dynamic _selectedItem;
  late int _selectedItemIndex;
}

class _PickerBodyState extends State<_PickerBody>
    with TickerProviderStateMixin {
  List<_UiListArgs> uiListArgs = [];

  late AnimationController _fadeController;
  late AnimationController _slideController;
  late Animation<double> _fadeAnimation;
  late Animation<Offset> _slideAnimation;

  @override
  void initState() {
    super.initState();

    for (var lst in widget.lists) {
      final args = _UiListArgs();
      uiListArgs.add(args);

      if (lst.initialItem != null) {
        args._selectedItemIndex = lst.items.indexOf(lst.initialItem);
        if (args._selectedItemIndex < 0) args._selectedItemIndex = 0;
      }
      //
      else {
        args._selectedItemIndex = 0;
      }

      args._selectedItem = lst.items[args._selectedItemIndex];
      args._scrollController = FixedExtentScrollController(
        initialItem: args._selectedItemIndex,
      );
    }

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
    for (var args in uiListArgs) {
      args._scrollController.dispose();
    }
    _fadeController.dispose();
    _slideController.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return GestureDetector(
      onTap: _cancelSelection,
      child: Container(
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
                          style: AppTheme.defaultTextStyle(
                            textSize: 20,
                            fontWeight: FontWeight.bold,
                            textColor: AppTheme.appColors?.primary,
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
                            style: AppTheme.defaultTextStyle(
                              textSize: 12,
                              textColor: AppTheme.appColors?.hint,
                            ),
                          ),
                        ),
                      ),

                    const SizedBox(height: 10),

                    // Time roller
                    _buildRoller(),

                    const SizedBox(height: 20),

                    // Action buttons
                    Padding(
                      padding: const EdgeInsets.all(20),
                      child: Row(
                        children: [
                          Expanded(
                            child: OutlinedButton(
                              onPressed: _cancelSelection,
                              child: Text(App.isEnglish ? 'Cancel' : 'إلغاء'),
                            ),
                          ),
                          const SizedBox(width: 16),
                          Expanded(
                            child: OutlinedButton(
                              onPressed: _confirm,
                              child: Text(App.isEnglish ? 'Apply' : 'تطبيق'),
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
      ),
    );
  }

  //------------------------------------

  Widget _buildRoller() {
    int listIndex = -1;

    return Container(
      height: 200,
      padding: const EdgeInsets.symmetric(horizontal: 20),
      child: Row(
        children: uiListArgs.map((args) {
          listIndex++;

          return Expanded(
            child: Stack(
              children: [
                ListWheelScrollView.useDelegate(
                  controller: args._scrollController,
                  itemExtent: 50,
                  physics: const FixedExtentScrollPhysics(),
                  onSelectedItemChanged: (index) => onSelectedItemChanged(
                    listIndex,
                    args,
                    index,
                  ),
                  childDelegate: itemListBuildDelegate(listIndex, args),
                ),
                //
                Align(
                  alignment: Alignment.center,
                  child: Container(
                    color: Theme.of(context).primaryColor.withOpacity(0.2),
                    width: double.maxFinite,
                    height: 1,
                    margin: EdgeInsets.only(bottom: 47),
                  ),
                ),
                Align(
                  alignment: Alignment.center,
                  child: Container(
                    color: Theme.of(context).primaryColor.withOpacity(0.2),
                    width: double.maxFinite,
                    height: 1,
                    margin: EdgeInsets.only(top: 47),
                  ),
                ),
              ],
            ),
          );
        }).toList(),
      ),
    );
  }

  itemListBuildDelegate(int listIndex, _UiListArgs args) {
    return ListWheelChildBuilderDelegate(
      builder: (context, index) {
        final item = widget.lists[listIndex].items[index];

        if (widget.itemBuilder != null) {
          return widget.itemBuilder!(listIndex, item, index);
        }
        //
        final isSelected = index == args._selectedItemIndex;

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
      childCount: widget.lists[listIndex].items.length,
    );
  }

  onSelectedItemChanged(int listIndex, _UiListArgs args, int index) {
    HapticFeedback.selectionClick();
    setState(() {
      args._selectedItem = widget.lists[listIndex].items[index];
      args._selectedItemIndex = index;
    });
  }

  //------------------------------------

  void _confirm() async {
    await _slideController.reverse();
    if (mounted) {
      Navigator.of(context).pop(uiListArgs
          .map(
            (args) => Pair(
              value1: args._selectedItem,
              value2: args._selectedItemIndex,
            ),
          )
          .toList());
    }
  }

  void _cancelSelection() async {
    await _slideController.reverse();
    if (mounted) {
      Navigator.of(context).pop();
    }
  }
}
