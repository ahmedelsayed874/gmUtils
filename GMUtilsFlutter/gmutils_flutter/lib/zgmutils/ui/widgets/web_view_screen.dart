import 'dart:async';

import 'package:flutter/material.dart';
import 'package:webview_flutter/webview_flutter.dart';

import '../../gm_main.dart';
import '../../resources/app_theme.dart';
import '../../utils/launcher.dart';
import '../../utils/logs.dart';
import '_root_widget.dart';

// import 'package:webview_flutter_android/webview_flutter_android.dart';
// import 'package:webview_flutter_wkwebview/webview_flutter_wkwebview.dart';

class WebViewScreen extends StatefulWidget {
  static Future<void> showWithToolbar({
    required String url,
    required String toolbarTitle,
    bool allowOpenLinkExternal = true,
    //
    VoidCallback? onLoadCompleted,
    VoidCallback? onLoadFailed,
  }) {
    return App.navTo(
      WebViewScreen(
        url: url,
        //
        hasToolbar: true,
        toolbarTitle: toolbarTitle,
        allowOpenLinkExternal: allowOpenLinkExternal,
        //
        statusBarColor: null,
        topWidget: null,
        customOpenExternalWidget: null,
        currentOpenExternalOpacity: null,
        currentOpenExternalSize: null,
        currentOpenExternalBgColor: null,
        currentOpenExternalIconColor: null,
        //
        height: null,
        //
        onLoadCompleted: onLoadCompleted,
        onLoadFailed: onLoadFailed,
      ),
    );
  }

  static Future<void> showWithoutToolbar({
    required String url,
    bool allowOpenLinkExternal = true,
    //properties of no-toolbar
    Color? statusBarColor,
    Widget? topWidget,
    Widget? customOpenExternalWidget,
    double? currentOpenExternalOpacity,
    double? currentOpenExternalSize,
    Color? currentOpenExternalBgColor,
    Color? currentOpenExternalIconColor,
    //
    VoidCallback? onLoadCompleted,
    VoidCallback? onLoadFailed,
  }) {
    return App.navTo(
      WebViewScreen(
        url: url,
        //
        hasToolbar: false,
        toolbarTitle: null,
        allowOpenLinkExternal: allowOpenLinkExternal,
        //
        statusBarColor: statusBarColor,
        topWidget: topWidget,
        customOpenExternalWidget: customOpenExternalWidget,
        currentOpenExternalOpacity: currentOpenExternalOpacity,
        currentOpenExternalSize: currentOpenExternalSize,
        currentOpenExternalBgColor: currentOpenExternalBgColor,
        currentOpenExternalIconColor: currentOpenExternalIconColor,
        //
        height: null,
        //
        onLoadCompleted: onLoadCompleted,
        onLoadFailed: onLoadFailed,
      ),
    );
  }

  static WebViewScreen asWidget({
    required String url,
    //
    bool? allowOpenLinkExternal,
    Widget? topWidget,
    Widget? customOpenExternalWidget,
    double? currentOpenExternalOpacity,
    double? currentOpenExternalSize,
    Color? currentOpenExternalBgColor,
    Color? currentOpenExternalIconColor,
    //
    int? height,
    VoidCallback? onLoadCompleted,
    VoidCallback? onLoadFailed,
  }) {
    return WebViewScreen(
      url: url,
      //
      hasToolbar: false,
      toolbarTitle: null,
      allowOpenLinkExternal: false,
      //
      statusBarColor: null,
      topWidget: topWidget,
      customOpenExternalWidget: customOpenExternalWidget,
      currentOpenExternalOpacity: currentOpenExternalOpacity,
      currentOpenExternalSize: currentOpenExternalSize,
      currentOpenExternalBgColor: currentOpenExternalBgColor,
      currentOpenExternalIconColor: currentOpenExternalIconColor,
      //
      height: height,
      //
      onLoadCompleted: onLoadCompleted,
      onLoadFailed: onLoadFailed,
    );
  }

  //--------------------------------------------------------------------------

  final String url;

  //
  final bool hasToolbar;
  final String? toolbarTitle;
  final bool allowOpenLinkExternal;

  //properties of no-toolbar
  final Color? statusBarColor;
  final Widget? topWidget;
  final Widget? customOpenExternalWidget;
  final double? currentOpenExternalOpacity;
  final double? currentOpenExternalSize;
  final Color? currentOpenExternalBgColor;
  final Color? currentOpenExternalIconColor;

  //
  final int? height;

  //
  final VoidCallback? onLoadCompleted;
  final VoidCallback? onLoadFailed;

  const WebViewScreen({
    required this.url,
    //
    required this.hasToolbar,
    required this.toolbarTitle,
    required this.allowOpenLinkExternal,
    //
    required this.statusBarColor,
    required this.topWidget,
    required this.customOpenExternalWidget,
    required this.currentOpenExternalOpacity,
    required this.currentOpenExternalSize,
    required this.currentOpenExternalBgColor,
    required this.currentOpenExternalIconColor,
    //
    required this.height,
    //
    required this.onLoadCompleted,
    required this.onLoadFailed,
    //
    super.key,
  });

  @override
  State<WebViewScreen> createState() => _ImageViewerScreenState();
}

class _ImageViewerScreenState extends State<WebViewScreen> {
  WebViewController? controller;

  @override
  Widget build(BuildContext context) {
    if (widget.hasToolbar) {
      var rw = MyRootWidget.withToolbar(widget.toolbarTitle ?? '');
      rw.setBody(body(context));

      if (widget.allowOpenLinkExternal) {
        rw.setToolbarActions(
          actions: [
            IconButton(
              onPressed: () => Launcher().openUrl(widget.url),
              icon: const Icon(Icons.open_in_browser),
            ),
          ],
        );
      }

      return rw.build();
    }
    //
    else {
      return Stack(
        children: [
          //bg
          Container(
            color: widget.statusBarColor ?? AppTheme.appColors?.background ?? Colors.white,
          ),

          /*SafeArea(
              padding: EdgeInsets.only(top: 30),
              child: body(context),
          ),*/

          SafeArea(
            child: Column(
              children: [
                if (widget.topWidget != null) widget.topWidget!,
                Expanded(child: body(context)),
              ],
            ),
          ),

          //body(context),

          //
          if (widget.allowOpenLinkExternal)
            Positioned(
              right: 10,
              bottom: 10,
              child: widget.customOpenExternalWidget ??
                  Opacity(
                    opacity: widget.currentOpenExternalOpacity ?? 0.8,
                    child: GestureDetector(
                      onTap: () => Launcher().openUrl(widget.url),
                      child: Container(
                        width: widget.currentOpenExternalSize ?? 50,
                        height: widget.currentOpenExternalSize ?? 50,
                        decoration: BoxDecoration(
                          color: widget.currentOpenExternalBgColor ??
                              AppTheme.appColors?.primary ??
                              Colors.red,
                          borderRadius: BorderRadius.circular(100),
                          boxShadow: [
                            BoxShadow(offset: Offset(1, 1), blurRadius: 5),
                          ],
                        ),
                        child: Icon(
                          Icons.open_in_browser,
                          color: widget.currentOpenExternalIconColor ??
                              AppTheme.appColors?.primaryVariant ??
                              Colors.white,
                          size: 30,
                        ),
                      ),
                    ),
                  ),
            ),
        ],
      );
    }
  }

  int _tries = 0;

  Widget body(BuildContext context) {
    controller ??= WebViewController()
      ..setJavaScriptMode(JavaScriptMode.unrestricted)
      ..setBackgroundColor(const Color(0x00000000))
      ..setNavigationDelegate(
        NavigationDelegate(
          onProgress: (int progress) {
            Logs.print(() =>
                'WebViewScreen->WebViewController->onProgress: [progress= $progress]');
          },
          onPageStarted: (String url) {
            Logs.print(() => 'WebViewScreen->WebViewController->onPageStarted');
          },
          onPageFinished: (String url) {
            Logs.print(
              () => 'WebViewScreen->WebViewController->onPageFinished',
            );
            widget.onLoadCompleted?.call();
          },
          onWebResourceError: (WebResourceError error) {
            Logs.print(
              () => 'WebViewScreen->WebViewController->'
                  'onWebResourceError: [errorCode: ${error.errorCode}, '
                  'errorDesc: ${error.description}]',
            );

            _dispatchLoadingFailed();
          },
          onNavigationRequest: (NavigationRequest request) {
            _tries++;

            Logs.print(() =>
                'WebViewScreen->WebViewController->onNavigationRequest: [request#$_tries: isMainFrame: ${request.isMainFrame}, ${request.url}]');

            if (_tries == 1) {
              return NavigationDecision.navigate;
            } else {
              if (_tries == 2) {
                _dispatchLoadingFailed();
              }

              return NavigationDecision.prevent;
            }
          },
        ),
      );

    try {
      controller?.loadRequest(Uri.parse(widget.url));
    } catch (e) {
      Future.delayed(const Duration(milliseconds: 700), () {
        Launcher().openUrl(widget.url);
      });
    }

    if (widget.height == null) {
      return WebViewWidget(controller: controller!);
    }
    //
    else {
      return SizedBox(
        height: widget.height!.toDouble(),
        child: WebViewWidget(controller: controller!),
      );
    }
  }

  void _dispatchLoadingFailed() {
    widget.onLoadFailed?.call();

    MyRootWidget.showSnackBar(
      context,
      message: App.isEnglish
          ? "Unable to view the webpage .. will open external"
          : "تعذر عرض صفحة الويب .. سيتم العرض خارجيا",
    );

    Future.delayed(const Duration(milliseconds: 700), () {
      Launcher().openUrl(widget.url);
    });
  }
}
