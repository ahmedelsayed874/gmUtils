import 'package:flutter/foundation.dart';
import 'package:flutter/material.dart';

import 'network_image_with_loading.dart';


class ImagesSliderWidget extends StatefulWidget {
  final List<String> urls;
  final BoxFit? fit;
  final String? toolbarTitle;
  final bool? allowEnlargeOnClick;
  final Widget? errorPlaceHolder;

  const ImagesSliderWidget({
    required this.urls,
    this.fit,
    this.toolbarTitle,
    this.allowEnlargeOnClick,
    this.errorPlaceHolder,
    super.key,
  });

  @override
  State<ImagesSliderWidget> createState() => _PageViewExampleState();
}

class _PageViewExampleState extends State<ImagesSliderWidget>
    with TickerProviderStateMixin {
  late PageController _pageViewController;
  late TabController _tabController;
  int _currentPageIndex = 0;
  int _maxTabControllerLength = 10;

  @override
  void initState() {
    super.initState();
    _pageViewController = PageController();

    _maxTabControllerLength =  widget.urls.length < 10 ? widget.urls.length : 10;
    _tabController = TabController(
      length: _maxTabControllerLength,
      vsync: this,
    );
  }

  @override
  void dispose() {
    super.dispose();
    _pageViewController.dispose();
    _tabController.dispose();
  }

  @override
  Widget build(BuildContext context) {
    var errorPlaceHolder =
        widget.errorPlaceHolder ?? Icon(Icons.image_not_supported_outlined);

    return Stack(
      alignment: Alignment.bottomCenter,
      children: <Widget>[
        PageView(
          /// [PageView.scrollDirection] defaults to [Axis.horizontal].
          /// Use [Axis.vertical] to scroll vertically.
          controller: _pageViewController,
          onPageChanged: _handlePageViewChanged,
          children: widget.urls.isEmpty
              ? <Widget>[errorPlaceHolder]
              : widget.urls.map((url) {
                  return NetworkImageWithLoading(
                    toolbarTitle: widget.toolbarTitle ?? '',
                    imgUrl: url,
                    allowEnlargeOnClick: widget.allowEnlargeOnClick ?? false,
                    onClick: null,
                    //errorPlaceHolderSize: width.toInt(),
                    errorPlaceHolder: errorPlaceHolder,
                    fit: widget.fit ?? BoxFit.fill,
                  );
                }).toList(),
        ),
        if (widget.urls.length > 1)
          _PageIndicator(
            tabController: _tabController,
            currentPageIndex: _currentPageIndex,
            onUpdateCurrentPageIndex: _updateCurrentPageIndex,
            isOnDesktopAndWeb: _isOnDesktopAndWeb,
          ),
      ],
    );
  }

  void _handlePageViewChanged(int currentPageIndex) {
    print('_PageViewExampleState._handlePageViewChanged(currentPageIndex = $currentPageIndex)');
    if (!_isOnDesktopAndWeb) {
      return;
    }
    _tabController.index = currentPageIndex;

    // if (currentPageIndex == _maxTabControllerLength - 1) {
    //   setState(() {
    //     _currentPageIndex = _currentPageIndex - 1;
    //   });
    // }
    // //
    // else {
    //
    // }

    setState(() {
      _currentPageIndex = currentPageIndex;
    });
  }

  void _updateCurrentPageIndex(int index) {
    print('_PageViewExampleState._updateCurrentPageIndex(index = $index)');

    _tabController.index = index;
    _pageViewController.animateToPage(
      index,
      duration: const Duration(milliseconds: 400),
      curve: Curves.easeInOut,
    );
  }

  bool get _isOnDesktopAndWeb =>
      true ||
      kIsWeb ||
      switch (defaultTargetPlatform) {
        TargetPlatform.macOS ||
        TargetPlatform.linux ||
        TargetPlatform.windows =>
          true,
        TargetPlatform.android ||
        TargetPlatform.iOS ||
        TargetPlatform.fuchsia =>
          false,
      };
}

class _PageIndicator extends StatelessWidget {
  const _PageIndicator({
    super.key,
    required this.tabController,
    required this.currentPageIndex,
    required this.onUpdateCurrentPageIndex,
    required this.isOnDesktopAndWeb,
  });

  final int currentPageIndex;
  final TabController tabController;
  final void Function(int) onUpdateCurrentPageIndex;
  final bool isOnDesktopAndWeb;

  @override
  Widget build(BuildContext context) {
    // if (!isOnDesktopAndWeb) {
    //   return const SizedBox.shrink();
    // }
    final ColorScheme colorScheme = Theme.of(context).colorScheme;

    return Padding(
      padding: const EdgeInsets.all(8.0),
      child: Row(
        mainAxisAlignment: MainAxisAlignment.center,
        children: <Widget>[
          IconButton(
            splashRadius: 16.0,
            padding: EdgeInsets.zero,
            onPressed: () {
              if (currentPageIndex == 0) {
                return;
              }
              onUpdateCurrentPageIndex(currentPageIndex - 1);
            },
            icon: const Icon(Icons.arrow_left_rounded, size: 32.0),
          ),
          TabPageSelector(
            controller: tabController,
            color: colorScheme.surface,
            selectedColor: colorScheme.primary,
          ),
          IconButton(
            splashRadius: 16.0,
            padding: EdgeInsets.zero,
            onPressed: () {
              if (currentPageIndex == tabController.length) {
                return;
              }
              onUpdateCurrentPageIndex(currentPageIndex + 1);
            },
            icon: const Icon(Icons.arrow_right_rounded, size: 32.0),
          ),
        ],
      ),
    );
  }
}
