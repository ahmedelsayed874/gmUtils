import 'dart:io';
import 'dart:ui';

import 'package:flutter/foundation.dart';
import 'package:flutter/material.dart';
import 'package:flutter/rendering.dart';
import 'package:share_plus/share_plus.dart';

import '../../gm_main.dart';
import '../../utils/date_op.dart';
import '../../utils/files.dart';
import '../dialogs/message_dialog.dart';

class CapturingScreenshotWidget extends StatefulWidget {
  CapturingScreenshotWidget({this.builder, Key? key}) : super(key: key);

  Widget Function(GlobalKey key)? builder;
  final GlobalKey _globalKey = GlobalKey();

  @override
  State<CapturingScreenshotWidget> createState() => _ToBeCapturedWidgetState();

  void shareAsImage({
    required BuildContext context,
    String text = '',
    String onFailedMessage = ''
        'Failed to capture a scene, '
        'please a take screenshot instead.',
  }) async {
    var key = _globalKey;
    var imageBytes = await WidgetCapture().captureAsBytes(key);
    if (imageBytes != null) {
      //file name
      var fileName = DateOp()
          .formatForDatabase(
            DateTime.now(),
            dateOnly: false,
          )
          .replaceAll(" ", '-')
          .replaceAll("+", '-')
          .replaceAll(":", '-');

      //create file and write data
      var files = Files.private(
        fileName, 'png'
      );
      var createdFile = await files.writeBytes(imageBytes);

      //share file
      await Share.shareFiles(
        [createdFile.path],
        mimeTypes: ['image/*'],
        text: text,
      );
    } else {
      MessageDialog.create
          .setTitle(App.isEnglish ? 'Error' : 'خطأ')
          .setMessage(onFailedMessage)
          .addAction(App.isEnglish ? 'OK' : 'حسنا')
          .show(() => context);
    }
  }

  Future<Image?> takeSceneImage() async {
    var image = await WidgetCapture().captureAsImage(_globalKey);
    return image;
  }
}

class _ToBeCapturedWidgetState extends State<CapturingScreenshotWidget> {
  @override
  void dispose() {
    widget.builder = null;
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return RepaintBoundary(
      key: widget._globalKey,
      child: widget.builder?.call(widget._globalKey),
    );
  }
}

//------------------------------------------------------------------------------

class WidgetCapture {
  Future<Uint8List?> captureAsBytes(GlobalKey widgetKey) async {
    RenderRepaintBoundary? boundary =
        widgetKey.currentContext?.findRenderObject() as RenderRepaintBoundary?;
    final image = await boundary?.toImage(pixelRatio: 3);
    final byteData = await image?.toByteData(format: ImageByteFormat.png);
    final pngBytes = byteData?.buffer.asUint8List();
    return pngBytes;
  }

  Future<Image?> captureAsImage(GlobalKey widgetKey) async {
    var bytes = await captureAsBytes(widgetKey);
    return bytes == null ? null : Image.memory(bytes);
  }

  Future<File?> captureAsFile(GlobalKey widgetKey, [String? fileName]) async {
    var imageBytes = await captureAsBytes(widgetKey);
    if (imageBytes != null) {
      //file name
      var fileName2 = fileName ??
          DateOp()
              .formatForDatabase(
                DateTime.now(),
                dateOnly: false,
              )
              .replaceAll(" ", '-')
              .replaceAll("+", '-')
              .replaceAll(":", '-');

      //create file and write data
      var files = Files.private(fileName2, 'png');
      var createdFile = await files.writeBytes(imageBytes);
      return createdFile;
    }

    return null;
  }
}
