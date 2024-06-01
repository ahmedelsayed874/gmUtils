import 'package:flutter/material.dart';

class BottomSheetCreator {
  void show({
    required BuildContext context,
    required List<Widget> children,
    VoidCallback? onClosing,
    VoidCallback? onClosed,
    bool isExpanded = false,
    bool enableDrag = false,
    double contentPadding = 15,
    Color? backgroundColor,
  }) {
    showModalBottomSheet(
      context: context,
      isScrollControlled: isExpanded,

      // shape: RoundedRectangleBorder(
      //   borderRadius: BorderRadius.only(
      //     topLeft: Radius.circular(30),
      //     topRight: Radius.circular(30),
      //   ),
      //   side: BorderSide(),
      // ),

      builder: (context) => _BottomSheetBody(
        onClosing: onClosing,
        enableDrag: enableDrag,
        contentPadding: contentPadding,
        backgroundColor: backgroundColor,
        children: children,
      ),
    ).then((value) => onClosed?.call());
  }
}

//------------------------------------------------------------------------------

class _BottomSheetBody extends StatefulWidget {
  final List<Widget> children;
  final VoidCallback? onClosing;
  final bool enableDrag;
  final double contentPadding;
  final Color? backgroundColor;

  const _BottomSheetBody({
    required this.children,
    required this.onClosing,
    required this.enableDrag, // = false,
    required this.contentPadding, // = 15,
    required this.backgroundColor,
    Key? key,
  }) : super(key: key);

  @override
  State<_BottomSheetBody> createState() => _BottomSheetBodyState();
}

class _BottomSheetBodyState extends State<_BottomSheetBody>
    with TickerProviderStateMixin {
  late AnimationController _controller;
  final Duration duration = const Duration(milliseconds: 500);

  @override
  void initState() {
    super.initState();
    _controller = AnimationController(
      vsync: this,
      duration: duration,
    );
  }

  @override
  void didUpdateWidget(_BottomSheetBody oldWidget) {
    super.didUpdateWidget(oldWidget);
    _controller.duration = duration;
  }

  @override
  void dispose() {
    _controller.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    final List<Widget> children = widget.children;
    final VoidCallback? onClosing = widget.onClosing;
    final bool enableDrag = widget.enableDrag;
    final double contentPadding = widget.contentPadding;
    final Color? backgroundColor = widget.backgroundColor;

    /*return children.length > 1
        ? _bottomOfSingleContent(
            children,
            contentPadding,
            enableDrag,
            backgroundColor,
            onClosing,
          )
        : _bottomOfScrolledContent(
            children,
            contentPadding,
          );*/

    return BottomSheet(
      enableDrag: enableDrag,
      animationController:
          enableDrag ? BottomSheet.createAnimationController(this) : null,
      backgroundColor: backgroundColor,

      // shape: RoundedRectangleBorder(
      //   borderRadius: BorderRadius.only(topLeft: Radius.circular(30), topRight: Radius.circular(30)),
      //   side: BorderSide(),
      // ),

      builder: (context) => Padding(
        padding: MediaQuery.of(context).viewInsets,
        child: Padding(
          padding: EdgeInsets.all(contentPadding),
          child: children.length > 1
              ? SingleChildScrollView(
                  child: Column(
                    mainAxisSize: MainAxisSize.min,
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: children,
                  ),
                )
              : children[0],
        ),
      ),
      onClosing: () => onClosing?.call(),
      constraints: BoxConstraints.expand(
        height: MediaQuery.of(context).size.height - 70,
      ),
    );
  }

  Widget _bottomOfSingleContent(
    children,
    contentPadding,
    enableDrag,
    backgroundColor,
    onClosing,
  ) {
    return BottomSheet(
      enableDrag: enableDrag,
      animationController:
          enableDrag ? BottomSheet.createAnimationController(this) : null,
      backgroundColor: backgroundColor,
      builder: (context) => Padding(
        padding: MediaQuery.of(context).viewInsets,
        child: Padding(
          padding: EdgeInsets.all(contentPadding),
          child: children[0],
        ),
      ),
      onClosing: () => onClosing?.call(),
      constraints: BoxConstraints.expand(
          height: MediaQuery.of(context).size.height - 70),
    );
  }

  Widget _bottomOfScrolledContent(
    children,
    contentPadding,
  ) {
    return DraggableScrollableSheet(
      initialChildSize: .2,
      minChildSize: .1,
      maxChildSize: .6,
      //expand: false,
      builder: (context, scrollController) {
        return Padding(
          padding: MediaQuery.of(context).viewInsets,
          child: Padding(
            padding: EdgeInsets.all(contentPadding),
            child: ListView(
              controller: scrollController,
              children: children,
            ),
          ),
        );
      },
    );
  }
}
