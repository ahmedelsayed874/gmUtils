import 'dart:async';

import 'package:flutter/material.dart';
import 'package:webview_flutter/webview_flutter.dart';

import '../../gm_main.dart';
import '../../resources/app_theme.dart';
import '../../utils/launcher.dart';
import '../../utils/logs.dart';
import '_root_widget.dart';

import 'package:webview_flutter_android/webview_flutter_android.dart';
import 'package:webview_flutter_wkwebview/webview_flutter_wkwebview.dart';

class WebViewScreen extends StatefulWidget {
  static Future<void> showWithToolbar({
    required String url,
    required String toolbarTitle,
    bool allowOpenLinkExternal = true,
    //
    bool Function(String url)? isUrlAllowedToVisit,
    void Function(String url)? onLoadCompleted,
    void Function(String? url)? onLoadFailed,
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
        isUrlAllowedToVisit: isUrlAllowedToVisit,
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
    bool Function(String url)? isUrlAllowedToVisit,
    void Function(String url)? onLoadCompleted,
    void Function(String? url)? onLoadFailed,
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
        isUrlAllowedToVisit: isUrlAllowedToVisit,
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
    //
    bool Function(String url)? isUrlAllowedToVisit,
    void Function(String url)? onLoadCompleted,
    void Function(String? url)? onLoadFailed,
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
      isUrlAllowedToVisit: isUrlAllowedToVisit,
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
  final bool Function(String url)? isUrlAllowedToVisit;
  final void Function(String url)? onLoadCompleted;
  final void Function(String? url)? onLoadFailed;

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
    required this.isUrlAllowedToVisit,
    required this.onLoadCompleted,
    required this.onLoadFailed,
    //
    super.key,
  });

  @override
  State<WebViewScreen> createState() => _WebViewScreenState();
}

class _WebViewScreenState extends State<WebViewScreen> {
  WebViewController? controller;

  @override
  void initState() {
    super.initState();

    Logs.print(() => 'WebView2Screen -> this url will open (${widget.url})');
  }

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
            color: widget.statusBarColor ??
                AppTheme.appColors?.background ??
                Colors.white,
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

          //
          SafeArea(
            child: IconButton(
              onPressed: () => App.navBack(),
              icon: Container(
                decoration: BoxDecoration(
                  color: AppTheme.appColors?.secondary,
                  borderRadius: BorderRadius.circular(7),
                  boxShadow: [
                    BoxShadow(blurRadius: 20, color: Colors.grey),
                  ],
                ),
                padding: EdgeInsets.fromLTRB(12, 7, 5, 7),
                child: Icon(
                  Icons.arrow_back_ios,
                  color: AppTheme.appColors?.secondaryVariant,
                ),
              ),
            ),
          ),
        ],
      );
    }
  }

  Widget body(BuildContext context) {
    if (controller == null) {
      late final PlatformWebViewControllerCreationParams params;

      if (WebViewPlatform.instance is WebKitWebViewPlatform) {
        params = WebKitWebViewControllerCreationParams(
          allowsInlineMediaPlayback: true,
          mediaTypesRequiringUserAction: const <PlaybackMediaTypes>{
            //PlaybackMediaTypes.audio,
            //PlaybackMediaTypes.video,
          },
        );
      }
      //
      else {
        params = const PlatformWebViewControllerCreationParams();
      }

      final WebViewController controllerX =
          WebViewController.fromPlatformCreationParams(params);

      if (controllerX.platform is AndroidWebViewController) {
        AndroidWebViewController.enableDebugging(true);
        (controllerX.platform as AndroidWebViewController)
            .setMediaPlaybackRequiresUserGesture(false);
      }

      //
      controllerX.setJavaScriptMode(JavaScriptMode.unrestricted);
      controllerX.setBackgroundColor(const Color(0x00000000));
      controllerX.setNavigationDelegate(NavigationDelegate(
        onProgress: _onProgressChanged,
        onPageStarted: _onPageStarted,
        onPageFinished: _onPageFinished,
        onWebResourceError: _onWebResourceError,
        onNavigationRequest: _onNavigationRequest,
      ));

      controller = controllerX;
    }

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

  //------------------------------------------

  void _onProgressChanged(int progress) {
    //Logs.print(() => 'WebViewScreen->onProgress: [progress= $progress]');
  }

  void _onPageStarted(String url) {
    Logs.print(() => 'WebViewScreen->onPageStarted($url)');
  }

  void _onPageFinished(String url) {
    Logs.print(() => 'WebViewScreen->onPageFinished($url)');

    widget.onLoadCompleted?.call(url);
  }

  void _onWebResourceError(WebResourceError error) {
    Logs.print(
      () => 'WebViewScreen->WebViewController->'
          'onWebResourceError: [errorCode: ${error.errorCode}, '
          'errorDesc: ${error.description}]',
    );

    _dispatchLoadingFailed(error.url);
  }

  NavigationDecision _onNavigationRequest(NavigationRequest request) {
    /*_tries++;
            Logs.print(() => 'WebViewScreen->WebViewController->onNavigationRequest: [request#$_tries: isMainFrame: ${request.isMainFrame}, ${request.url}]');
            if (_tries == 1) { return NavigationDecision.navigate; }
            else { if (_tries == 2) { _dispatchLoadingFailed(); } return NavigationDecision.prevent; }*/

    Logs.print(
      () => 'WebViewScreen->onNavigationRequest: '
          'request={isMainFrame: ${request.isMainFrame}, ${request.url}}',
    );

    if (widget.isUrlAllowedToVisit?.call(request.url) == false) {
      Logs.print(
        () => 'WebViewScreen->onNavigationRequest: '
            'NOT-ALLOWED-TO-VISIT-THIS-URL',
      );

      return NavigationDecision.prevent;
    }
    return NavigationDecision.navigate;
  }

  //------------------------------------------

  void _dispatchLoadingFailed(String? url) {
    widget.onLoadFailed?.call(url);

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
