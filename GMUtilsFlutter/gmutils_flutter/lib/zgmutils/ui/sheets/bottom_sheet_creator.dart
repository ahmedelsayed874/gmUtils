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
    bool useSafeArea = false,
    double contentPadding = 15,
    Color? backgroundColor,
  }) {
    showModalBottomSheet(
      context: context,
      //
      isScrollControlled: isExpanded,
      useSafeArea: useSafeArea,
      //
      // useRootNavigator: true,
      // isDismissible: true,
      // enableDrag: true,
      // barrierColor: Colors.black54,
      // backgroundColor: Colors.transparent,
      //
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

  void showAsDraggableScroll({
    required BuildContext context,
    required List<Widget> children,
    void Function(dynamic)? onClosed,
    //
    bool useSafeArea = false,
    Color? backgroundColor,
  }) {
    showModalBottomSheet(
      context: context,
      //
      isScrollControlled: true,
      useSafeArea: useSafeArea,
      //
      useRootNavigator: true,
      isDismissible: true,
      enableDrag: true,
      barrierColor: Colors.black54,
      backgroundColor: Colors.transparent,
      //
      builder: (context) => _DraggableScrollWidget(
        backgroundColor: backgroundColor,
        child: children.length == 1
            ? children.first
            : Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: children,
              ),
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
}

//-----

class _DraggableScrollWidget extends StatelessWidget {
  final Color? backgroundColor;
  final Widget child;

  const _DraggableScrollWidget({
    this.backgroundColor,
    required this.child,
    super.key,
  });

  @override
  Widget build(BuildContext context) {
    return DraggableScrollableSheet(
      initialChildSize: 0.7,
      minChildSize: 0.30,
      maxChildSize: 0.90,
      builder: (_, controller) {
        return Container(
          decoration: BoxDecoration(
            color: backgroundColor ?? Colors.white,
            borderRadius: const BorderRadius.vertical(top: Radius.circular(20)),
            boxShadow: const [
              BoxShadow(color: Colors.black26, blurRadius: 50),
            ],
          ),
          child: SafeArea(
            top: false,
            child: Padding(
              padding: const EdgeInsets.symmetric(horizontal: 1),
              child: Column(
                children: [
                  const SizedBox(height: 10),
                  Container(
                    width: 40,
                    height: 4,
                    decoration: BoxDecoration(
                      color: Colors.black26,
                      borderRadius: BorderRadius.circular(2),
                    ),
                  ),
                  const SizedBox(height: 8),

                  //
                  Expanded(
                    child: SingleChildScrollView(
                      controller: controller,
                      child: child,
                    ),
                  ),
                ],
              ),
            ),
          ),
        );
      },
    );
  }
}
