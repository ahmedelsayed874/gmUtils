import 'dart:async';

import 'package:flutter/material.dart';
import 'package:webview_flutter/webview_flutter.dart';

import '../../../zgmutils/utils/launcher.dart';
import '../../../zgmutils/utils/logs.dart';
import '../../gm_main.dart';
import '_root_widget.dart' as rw;

// import 'package:webview_flutter_android/webview_flutter_android.dart';
// import 'package:webview_flutter_wkwebview/webview_flutter_wkwebview.dart';

class WebViewScreen extends StatefulWidget {
  static void show({
    required String toolbarTitle,
    required String url,
    int? height,
  }) {
    App.navTo(
      WebViewScreen(
        toolbarTitle: toolbarTitle,
        url: url,
        height: height,
      ),
    );
  }

  final String toolbarTitle;
  final String url;
  final int? height;

  const WebViewScreen({
    required this.toolbarTitle,
    required this.url,
    this.height,
    Key? key,
  }) : super(key: key);

  @override
  State<WebViewScreen> createState() => _ImageViewerScreenState();
}

class _ImageViewerScreenState extends State<WebViewScreen> {
  WebViewController? controller;

  @override
  Widget build(BuildContext context) {
    return rw.RootWidget.withToolbar(widget.toolbarTitle)
        .setBody(body(context))
        .setToolbarActions(
      actions: [
        IconButton(
          onPressed: () => Launcher().openUrl(widget.url),
          icon: const Icon(Icons.open_in_browser),
        ),
      ],
    ).build();
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
                () => 'WebViewScreen->WebViewController->onPageFinished');
          },
          onWebResourceError: (WebResourceError error) {
            Logs.print(() =>
                'WebViewScreen->WebViewController->onWebResourceError: [errorCode: ${error.errorCode}, errorDesc: ${error.description}]');
          },
          onNavigationRequest: (NavigationRequest request) {
            _tries++;

            Logs.print(() =>
                'WebViewScreen->WebViewController->onNavigationRequest: [request#$_tries: isMainFrame: ${request.isMainFrame}, ${request.url}]');

            if (_tries == 1) {
              return NavigationDecision.navigate;
            } else {
              if (_tries == 2) {
                rw.RootWidget.showSnackBar(
                  context,
                  message: App.isEnglish
                      ? "Can't open the file"
                      : "تعذر تشغيل الملف",
                );

                Future.delayed(const Duration(milliseconds: 700), () {
                  Launcher().openUrl(widget.url);
                });
              }

              return NavigationDecision.prevent;
            }
          },
        ),
      )
      ..loadRequest(Uri.parse(widget.url));

    if (widget.height == null) {
      return WebViewWidget(controller: controller!);
    } else {
      return SizedBox(
        height: widget.height!.toDouble(),
        child: WebViewWidget(controller: controller!),
      );
    }
  }
}
