import 'package:flutter/material.dart';

class BottomSheetCreator {
  void show({
    required BuildContext context,
    required List<Widget> children,
    Widget? title,
    VoidCallback? onClosing,
    void Function(dynamic)? onClosed,
    bool isExpanded = false,
    bool disableDrag = false,
    double contentPadding = 15,
    Color? backgroundColor,
  }) {
    showModalBottomSheet(
      context: context,
      isScrollControlled: isExpanded,
      builder: (context) => _BottomSheetBody(
        onClosing: onClosing,
        disableDrag: disableDrag,
        contentPadding: contentPadding,
        backgroundColor: backgroundColor,
        title: title,
        children: children,
      ),
    ).then((value) => onClosed?.call(value));
  }
}

//------------------------------------------------------------------------------

class _BottomSheetBody extends StatefulWidget {
  final Widget? title;
  final List<Widget> children;
  final VoidCallback? onClosing;
  final bool disableDrag;
  final double contentPadding;
  final Color? backgroundColor;

  const _BottomSheetBody({
    required this.title,
    required this.children,
    required this.onClosing,
    required this.disableDrag, // = false,
    required this.contentPadding, // = 15,
    required this.backgroundColor,
    super.key,
  }) : super();

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
    final Widget? title = widget.title;
    final List<Widget> children = widget.children;
    final VoidCallback? onClosing = widget.onClosing;
    final bool disableDrag = widget.disableDrag;
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

    var body = Padding(
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
    );

    return BottomSheet(
      enableDrag: disableDrag,
      animationController:
      !disableDrag ? BottomSheet.createAnimationController(this) : null,
      backgroundColor: backgroundColor,

      // shape: RoundedRectangleBorder(
      //   borderRadius: BorderRadius.only(topLeft: Radius.circular(30), topRight: Radius.circular(30)),
      //   side: BorderSide(),
      // ),

      builder: (context) => Padding(
        padding: MediaQuery.of(context).viewInsets,
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          mainAxisSize: MainAxisSize.min,
          children: [
            if (title != null) title,
            Expanded(child: body),
          ],
        ),
      ),
      onClosing: () => onClosing?.call(),
      constraints: BoxConstraints.expand(
        height: MediaQuery.of(context).size.height - 70,
      ),
    );
  }

  /*Widget _bottomOfSingleContent(
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
  }*/
}
