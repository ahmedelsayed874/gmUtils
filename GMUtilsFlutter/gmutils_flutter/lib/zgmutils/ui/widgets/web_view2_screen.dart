import 'dart:async';

import 'package:flutter/material.dart';
// import 'package:flutter_webview_pro/webview_flutter.dart';
import '../../gm_main.dart';

class WebView2Screen extends StatefulWidget {
  static void show({
    required String url,
    required bool hasToolbar,
    required String? toolbarTitle,
    EdgeInsets? margin,
  }) {
    App.navTo(
      WebView2Screen(
        url: url,
        hasToolbar: hasToolbar,
        toolbarTitle: toolbarTitle,
        margin: margin,
      ),
    );
  }

  final String url;
  final bool hasToolbar;
  final String? toolbarTitle;
  final EdgeInsets? margin;

  const WebView2Screen({
    required this.url,
    this.hasToolbar = true,
    this.toolbarTitle,
    this.margin,
    super.key,
  }) : super();

  @override
  State<WebView2Screen> createState() => _ImageViewerScreenState();

  static Future<void>? runJavaScript(String javaScript) {
    // return _ImageViewerScreenState._instance?.runJavaScript(javaScript);
    throw UnimplementedError();
  }

  static Future<Object>? runJavaScriptReturningResult(String javaScript) {
    // return _ImageViewerScreenState._instance?.runJavaScriptReturningResult(
    //   javaScript,
    // );
    throw UnimplementedError();
  }

  static void reload() {
    // _ImageViewerScreenState._instance?.reload();
    throw UnimplementedError();
  }

  static Future<bool?> canGoBack() async {
    // var f = await _ImageViewerScreenState._instance?._controller.future;
    // return f?.canGoBack();
    throw UnimplementedError();
  }

  static void goBack() async {
    // var f = await _ImageViewerScreenState._instance?._controller.future;
    // f?.goBack();
    throw UnimplementedError();
  }
}

class _ImageViewerScreenState extends State<WebView2Screen> {
  @override
  Widget build(BuildContext context) {
    throw UnimplementedError();
  }
}

/*class _ImageViewerScreenState extends State<WebView2Screen> {
  static _ImageViewerScreenState? _instance;
  final Completer<WebViewController> _controller =
      Completer<WebViewController>();
  int _tries = 0;

  @override
  void initState() {
    super.initState();
    _instance = this;
    if (Platform.isAndroid) {
      WebView.platform = SurfaceAndroidWebView();
    }
  }

  @override
  void dispose() {
    super.dispose();
    _instance = null;
  }

  @override
  Widget build(BuildContext context) {
    MyRootWidget? rw;

    if (widget.hasToolbar) {
      rw = MyRootWidget.withToolbar(
        widget.toolbarTitle ?? widget.url,
      ).setToolbarActions(
        actions: [
          IconButton(
            onPressed: () => Launcher().openUrl(widget.url),
            icon: const Icon(Icons.open_in_browser),
          ),
        ],
      );
    }
    *//*else {
      rw = MyRootWidget.withoutToolbar();
    }*//*

    if (widget.margin != null) {
      rw?.setScreenPadding(
        widget.margin!.top,
        widget.margin!.left,
        widget.margin!.right,
        widget.margin!.bottom,
      );
    }

    return rw?.setBody(body(context)).build() ?? body(context);
  }

  Widget body(BuildContext context) {
    Logs.print(() => 'WebView2Screen -> this url will open (${widget.url})');

    return WebView(
      initialUrl: widget.url,
      javascriptMode: JavascriptMode.unrestricted,
      onWebViewCreated: (WebViewController webViewController) {
        _controller.complete(webViewController);
      },
      onProgress: (int progress) {
        Logs.print(() => 'WebView is loading (progress : $progress%)');
      },
      javascriptChannels: <JavascriptChannel>{
        _toasterJavascriptChannel(context),
      },
      navigationDelegate: (NavigationRequest request) {
        if (request.url.startsWith('https://www.youtube.com/')) {
          Logs.print(() => 'blocking navigation to $request}');
          return NavigationDecision.prevent;
        }
        Logs.print(() => 'allowing navigation to $request');
        return NavigationDecision.navigate;
      },
      onPageStarted: (String url) {
        Logs.print(() => 'Page started loading: $url');
      },
      onPageFinished: (String url) {
        Logs.print(() => 'Page finished loading: $url');
      },
      gestureNavigationEnabled: true,
      backgroundColor: const Color(0x00000000),
      geolocationEnabled: false,

    );
  }

  JavascriptChannel _toasterJavascriptChannel(BuildContext context) {
    return JavascriptChannel(
        name: 'Toaster',
        onMessageReceived: (JavascriptMessage message) {
          // ignore: deprecated_member_use
          // Scaffold.of(context).showSnackBar(
          //   SnackBar(content: Text(message.message)),
          // );
        });
  }

  /// Runs the given JavaScript in the context of the current page.
  ///
  /// The Future completes with an error if a JavaScript error occurred.
  Future<void> runJavaScript(String javaScript) async {
    var w = await _controller.future;
    return w.runJavascript(javaScript);
  }

  /// Runs the given JavaScript in the context of the current page, and returns
  /// the result.
  ///
  /// The Future completes with an error if a JavaScript error occurred, or if
  /// the type the given expression evaluates to is unsupported. Unsupported
  /// values include certain non-primitive types on iOS, as well as `undefined`
  /// or `null` on iOS 14+.
  Future<Object> runJavaScriptReturningResult(String javaScript) async {
    var w = await _controller.future;
    return w.runJavascriptReturningResult(javaScript);
  }

  void reload() async {
    var w = await _controller.future;
    w.reload();
  }
}*/
