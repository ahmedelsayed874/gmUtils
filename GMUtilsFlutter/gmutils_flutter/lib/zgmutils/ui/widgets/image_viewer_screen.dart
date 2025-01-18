import 'dart:async';
import 'dart:io';

import 'package:flutter/foundation.dart';
import 'package:flutter/material.dart';
import 'package:webview_flutter/webview_flutter.dart';

import '../../gm_main.dart';
import '../../utils/launcher.dart';
import '../../utils/logs.dart';
import '_root_widget.dart';

class ImageViewerScreen {
  static void showFromUrl({
    required String toolbarTitle,
    required String photoUrl,
  }) {
    App.navTo(
      _ImageViewerOnWebViewScreen(toolbarTitle: toolbarTitle),
      args: photoUrl,
    );
  }

  static void showFromFile({
    required String toolbarTitle,
    required File file,
  }) {
    App.navTo(
      _ImageViewerOnImageViewScreen(toolbarTitle: toolbarTitle),
      args: file,
    );
  }

  static void showFromBytes({
    required String toolbarTitle,
    required Uint8List data,
  }) {
    App.navTo(
      _ImageViewerOnImageViewScreen(toolbarTitle: toolbarTitle),
      args: data,
    );
  }
}

//------------------------------------------------------------------------------

class _ImageViewerOnWebViewScreen extends StatefulWidget {
  final String toolbarTitle;

  const _ImageViewerOnWebViewScreen({
    required this.toolbarTitle,
    Key? key,
  }) : super(key: key);

  @override
  State<_ImageViewerOnWebViewScreen> createState() =>
      _ImageViewerScreenOnWebViewState();
}

///depend on webview
class _ImageViewerScreenOnWebViewState
    extends State<_ImageViewerOnWebViewScreen> {
  String _photoUrl = '';
  WebViewController? controller;

  @override
  Widget build(BuildContext context) {
    _photoUrl = ModalRoute.of(context)?.settings.arguments as String;

    return MyRootWidget.withToolbar(widget.toolbarTitle)
        .setBody(body(context))
        .setToolbarActions(
      actions: [
        IconButton(
          onPressed: () => Launcher().openUrl(_photoUrl),
          icon: const Icon(Icons.open_in_browser),
        ),
      ],
    ).build();
  }

  Widget body(BuildContext context) {
    controller ??= WebViewController()
      ..setJavaScriptMode(JavaScriptMode.unrestricted)
      ..setBackgroundColor(const Color(0x00000000))
      ..setNavigationDelegate(
        NavigationDelegate(
          onProgress: (int progress) {
            Logs.print(() =>
                'ImageViewerScreen->WebViewController->onProgress: [progress= $progress]');
          },
          onPageStarted: (String url) {
            Logs.print(
                () => 'ImageViewerScreen->WebViewController->onPageStarted');
          },
          onPageFinished: (String url) {
            Logs.print(
                () => 'ImageViewerScreen->WebViewController->onPageFinished');
          },
          onWebResourceError: (WebResourceError error) {
            Logs.print(() =>
                'ImageViewerScreen->WebViewController->onWebResourceError: [errorCode: ${error.errorCode}, errorDesc: ${error.description}]');
          },
          onNavigationRequest: (NavigationRequest request) {
            Logs.print(() =>
                'ImageViewerScreen->WebViewController->onNavigationRequest: [request: ${request.url}]');
            /*if (request.url.startsWith('https://www.youtube.com/')) {
              return NavigationDecision.prevent;
            }*/
            return NavigationDecision.navigate;
          },
        ),
      )
      ..loadRequest(Uri.parse(_photoUrl));

    return WebViewWidget(controller: controller!);
  }
}

//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

class _ImageViewerOnImageViewScreen extends StatefulWidget {
  final String toolbarTitle;

  const _ImageViewerOnImageViewScreen({
    required this.toolbarTitle,
    Key? key,
  }) : super(key: key);

  @override
  State<_ImageViewerOnImageViewScreen> createState() =>
      //_ImageViewerScreenOnImageViewState();
      _ImageViewerScreenOnImageViewAndNoScrollViewState();
}

///depend on Scroll view
class _ImageViewerScreenOnImageViewState
    extends State<_ImageViewerOnImageViewScreen> {
  //String _photoUrl = '';
  Object? _image; //url , uint8list, asset path

  int? originalImageWidth;
  int? originalImageHeight;
  double? _imageWidth;
  double? _imageHeight;
  final GlobalKey _key = GlobalKey();
  double? _screenWidth;

  @override
  Widget build(BuildContext context) {
    _image = ModalRoute.of(context)?.settings.arguments;

    _screenWidth ??= MediaQuery.of(context).size.width;

    return MyRootWidget.withToolbar(widget.toolbarTitle)
        .setBody(body(context))
        .setToolbarActions(actions: [
      if (_image is String)
        IconButton(
          onPressed: () => Launcher().openUrl(_image as String),
          icon: const Icon(Icons.open_in_browser),
        ),
      IconButton(
        onPressed: () => _changeImageSize(true),
        icon: const Icon(Icons.remove),
      ),
      IconButton(
        onPressed: () => _changeImageSize(false),
        icon: const Icon(Icons.add),
      ),
    ]).build();
  }

  Widget body(BuildContext context) {
    return SingleChildScrollView(
      scrollDirection: Axis.horizontal,
      child: SingleChildScrollView(
        scrollDirection: Axis.vertical,
        child: SizedBox(
            width: _imageWidth, height: _imageHeight, child: imageWidget()),
      ),
    );
  }

  Image imageWidget() {
    ImageProvider<Object>? imageProvider;
    if (_image is String) {
      var path = _image as String;
      if (path.toLowerCase().startsWith('http')) {
        imageProvider = NetworkImage(_image as String);
      } else {
        imageProvider = AssetImage(path);
      }
    } else if (_image is Uint8List) {
      imageProvider = MemoryImage(_image as Uint8List);
    }

    var image = Image(
      key: _key,
      image: imageProvider!,
      loadingBuilder: (context, child, loadingProgress) {
        if (loadingProgress == null) {
          return child;
        }

        return Center(
          child: Padding(
            padding: const EdgeInsets.all(8.0),
            child: CircularProgressIndicator(
              value: loadingProgress.expectedTotalBytes != null
                  ? loadingProgress.cumulativeBytesLoaded /
                      loadingProgress.expectedTotalBytes!
                  : null,
            ),
          ),
        );
      },
      errorBuilder: (ctx, e, t) {
        return const SizedBox(
            width: 100,
            child: Column(
              children: [
                Icon(Icons.error_outline),
                Text(
                  'can\'t load this image',
                  style: TextStyle(fontSize: 12),
                ),
              ],
            ));
      },
    );

    image.image.resolve(const ImageConfiguration()).addListener(
      ImageStreamListener((image, synchronousCall) {
        bool update = originalImageWidth == null;
        originalImageWidth = image.image.width;
        originalImageHeight = image.image.height;
        if (update && _screenWidth != null && originalImageWidth != null) {
          var s = _screenWidth! / originalImageWidth!;
          Future.delayed(const Duration(milliseconds: 200), () {
            _setScale(s);
          });
        }
      }),
    );

    return image;
  }

  //--------------------------------------------------------------------

  double _scale = 1;

  void _changeImageSize(bool down) {
    if (originalImageWidth != null && originalImageHeight != null) {
      bool changed = false;
      if (down) {
        if (_scale > 0.1) {
          _scale -= 0.1;
          changed = true;
        }
      } else {
        if (_scale < 1) {
          _scale += 0.1;
          changed = true;
        }
      }

      if (changed) {
        _setScale(_scale);
      }
    }
  }

  void _setScale(double scale) {
    if (originalImageWidth != null && originalImageHeight != null) {
      _scale = scale;
      _imageWidth = originalImageWidth! * _scale;
      _imageHeight = originalImageHeight! * _scale;
      setState(() {});
    }
  }
}

//to do test this
///depend on Scroll view
class _ImageViewerScreenOnImageViewAndNoScrollViewState
    extends State<_ImageViewerOnImageViewScreen> {
  //String _photoUrl = '';
  Object? _image; //url , uint8list, asset path

  int? originalImageWidth;
  int? originalImageHeight;
  double? _imageWidth;
  double? _imageHeight;
  double? _x, _y;
  final GlobalKey _key = GlobalKey();
  double? _screenWidth;

  @override
  Widget build(BuildContext context) {
    _image = ModalRoute.of(context)?.settings.arguments;

    _screenWidth ??= MediaQuery.of(context).size.width;

    return MyRootWidget.withToolbar(widget.toolbarTitle)
        .setBody(body(context))
        .setToolbarActions(actions: [
      if (_image is String)
        IconButton(
          onPressed: () => Launcher().openUrl(_image as String),
          icon: const Icon(Icons.open_in_browser),
        ),
      IconButton(
        onPressed: () => _changeImageSize(true),
        icon: const Icon(Icons.remove),
      ),
      IconButton(
        onPressed: () => _changeImageSize(false),
        icon: const Icon(Icons.add),
      ),
    ]).build();
  }

  Widget body(BuildContext context) {
    return Stack(
      children: [
        Positioned(
          left: _x,
          top: _y,
          child: GestureDetector(
            onScaleStart: onScaleStarted,
            onScaleUpdate: onScaleUpdate,
            onScaleEnd: onScaleEnd,
            //
            /*onPanDown: onPanDown,
            onPanStart: onPanStart,
            onPanUpdate: onPanUpdate,
            onPanEnd: onPanEnd,
            onPanCancel: onPanCancel,*/
            //
            child: SizedBox(
              width: _imageWidth,
              height: _imageHeight,
              child: imageWidget(),
            ),
          ),
        ),
      ],
    );
  }

  Image imageWidget() {
    ImageProvider<Object>? imageProvider;
    if (_image is String) {
      var path = _image as String;
      if (path.toLowerCase().startsWith('http')) {
        imageProvider = NetworkImage(_image as String);
      } else {
        imageProvider = AssetImage(path);
      }
    }
    //
    else if (_image is Uint8List) {
      imageProvider = MemoryImage(_image as Uint8List);
    }
    //
    else if (_image is File) {
      imageProvider = FileImage(_image as File);
    }

    var image = Image(
      key: _key,
      image: imageProvider!,
      loadingBuilder: (context, child, loadingProgress) {
        if (loadingProgress == null) {
          return child;
        }

        return Center(
          child: Padding(
            padding: const EdgeInsets.all(8.0),
            child: CircularProgressIndicator(
              value: loadingProgress.expectedTotalBytes != null
                  ? loadingProgress.cumulativeBytesLoaded /
                      loadingProgress.expectedTotalBytes!
                  : null,
            ),
          ),
        );
      },
      errorBuilder: (ctx, e, t) {
        return const Center(
          child: Padding(
            padding: EdgeInsets.all(8.0),
            child: SizedBox(
                width: 100,
                child: Column(
                  children: [
                    Icon(Icons.error_outline),
                    Text(
                      'can\'t load this image',
                      textAlign: TextAlign.center,
                      style: TextStyle(fontSize: 12),
                    ),
                  ],
                )),
          ),
        );
      },
    );

    image.image.resolve(const ImageConfiguration()).addListener(
      ImageStreamListener((image, synchronousCall) {
        bool update = originalImageWidth == null;
        originalImageWidth = image.image.width;
        originalImageHeight = image.image.height;
        if (update && _screenWidth != null && originalImageWidth != null) {
          var s = _screenWidth! / originalImageWidth!;
          Future.delayed(const Duration(milliseconds: 200), () {
            _setScale(s);
          });
        }
      }),
    );

    return image;
  }

  //--------------------------------------------------------------------

  double _scale = 1;

  void _changeImageSize(bool down) {
    if (originalImageWidth != null && originalImageHeight != null) {
      bool changed = false;
      if (down) {
        if (_scale > 0.1) {
          _scale -= 0.1;
          changed = true;
        }
      } else {
        if (_scale < 1) {
          _scale += 0.1;
          changed = true;
        }
      }

      if (changed) {
        _setScale(_scale);
      }
    }
  }

  void _setScale(double scale) {
    if (originalImageWidth != null && originalImageHeight != null) {
      _scale = scale;
      _imageWidth = originalImageWidth! * _scale;
      _imageHeight = originalImageHeight! * _scale;
      setState(() {});
    }
  }

  //============================================================================

  void onScaleStarted(ScaleStartDetails details) {
    Logs.print(() => [
          '_ImageViewerScreenOnImageViewAndNoScrollViewState',
          '.onScaleStarted: ',
          '{'
              'focalPoint: '
              '${details.focalPoint}'
              ', localFocalPoint: '
              '${details.localFocalPoint}'
              ', pointerCount: '
              '${details.pointerCount}'
              '}',
        ]);
  }

  void onScaleUpdate(ScaleUpdateDetails details) {
    Logs.print(() => [
          '_ImageViewerScreenOnImageViewAndNoScrollViewState',
          '.onScaleUpdate: ',
          '{'
              'focalPoint: '
              '${details.focalPoint}'
              ', localFocalPoint: '
              '${details.localFocalPoint}'
              ', pointerCount: '
              '${details.pointerCount}'
              ', scale: '
              '${details.scale}'
              ', horizontalScale: '
              '${details.horizontalScale}'
              ', verticalScale: '
              '${details.verticalScale}'
              '}',
        ]);
  }

  void onScaleEnd(ScaleEndDetails details) {
    Logs.print(() => [
          '_ImageViewerScreenOnImageViewAndNoScrollViewState',
          '.onScaleEnd: ',
          '{'
              'velocity: '
              '${details.velocity}'
              'velocity.pixelsPerSecond: '
              '${details.velocity.pixelsPerSecond}'
              ', scaleVelocity: '
              '${details.scaleVelocity}'
              ', pointerCount: '
              '${details.pointerCount}'
              '}',
        ]);
  }

  //----------------------------------------------------

  /*void onPanDown(DragDownDetails details) {
    Logs.print(() => [
          '_ImageViewerScreenOnImageViewAndNoScrollViewState',
          '.onPanDown: ',
          '{'
              'localPosition: '
              '${details.localPosition}'
              'globalPosition: '
              '${details.globalPosition}'
              '}',
        ]);
  }

  void onPanStart(DragStartDetails details) {
    Logs.print(() => [
          '_ImageViewerScreenOnImageViewAndNoScrollViewState',
          '.onPanStart: ',
          '{'
              'localPosition: '
              '${details.localPosition}'
              'globalPosition: '
              '${details.globalPosition}'
              'kind: '
              '${details.kind}'
              'sourceTimeStamp: '
              '${details.sourceTimeStamp}'
              '}',
        ]);
  }

  void onPanUpdate(DragUpdateDetails details) {
    Logs.print(() => [
          '_ImageViewerScreenOnImageViewAndNoScrollViewState',
          '.onPanUpdate: ',
          '{'
              'localPosition: '
              '${details.localPosition}'
              'globalPosition: '
              '${details.globalPosition}'
              'primaryDelta: '
              '${details.primaryDelta}'
              'delta: '
              '${details.delta}'
              '}',
        ]);
  }

  void onPanEnd(DragEndDetails details) {
    Logs.print(() => [
          '_ImageViewerScreenOnImageViewAndNoScrollViewState',
          '.onPanEnd: ',
          '{'
              'velocity: '
              '${details.velocity}'
              'primaryVelocity: '
              '${details.primaryVelocity}'
              '}',
        ]);
  }

  void onPanCancel() {
    Logs.print(() => [
          '_ImageViewerScreenOnImageViewAndNoScrollViewState',
          '.onPanCancel',
        ]);
  }*/
}
