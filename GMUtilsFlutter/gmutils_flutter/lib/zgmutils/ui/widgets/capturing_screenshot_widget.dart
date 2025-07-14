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
  late final GlobalKey globalKey;// = GlobalKey();
  final Widget Function() builder;
  
  CapturingScreenshotWidget({
    GlobalKey? globalKey,
    required this.builder, 
    super.key,
  }) {
    this.globalKey = globalKey ?? GlobalKey();
  }

  @override
  State<CapturingScreenshotWidget> createState() => _ToBeCapturedWidgetState();

  WidgetCapture get captureTool => WidgetCapture(globalKey);

  void shareExternal({
    required BuildContext context,
    String text = '',
    String onFailedMessage = ''
        'Failed to capture a scene, '
        'please a take screenshot instead.',
  }) async {
    var key = globalKey;
    var file = await WidgetCapture(key).captureAsFile();
    if (file != null) {
      //share file
      await Share.shareXFiles(
        [XFile(file.path, mimeType: 'image/*')],
        text: text,
      );
    }
    //
    else {
      MessageDialog.create
          .setTitle(App.isEnglish ? 'Error' : 'خطأ')
          .setMessage(onFailedMessage)
          .addActions([
        MessageDialogActionButton(App.isEnglish ? 'OK' : 'حسنا')
      ]).show(() => context);
    }
  }

  Future<Image?> takeSceneImage() async {
    var image = await WidgetCapture(globalKey).captureAsImage();
    return image;
  }
}

class _ToBeCapturedWidgetState extends State<CapturingScreenshotWidget> {
  @override
  Widget build(BuildContext context) {
    return RepaintBoundary(
      key: widget.globalKey,
      child: widget.builder.call(),
    );
  }
}

//------------------------------------------------------------------------------

class WidgetCapture {
  final GlobalKey widgetKey;

  WidgetCapture(this.widgetKey);

  Future<Uint8List?> captureAsBytes() async {
    RenderRepaintBoundary? boundary =
        widgetKey.currentContext?.findRenderObject() as RenderRepaintBoundary?;
    final image = await boundary?.toImage(pixelRatio: 3);
    final byteData = await image?.toByteData(format: ImageByteFormat.png);
    final pngBytes = byteData?.buffer.asUint8List();
    return pngBytes;
  }

  Future<Image?> captureAsImage() async {
    var bytes = await captureAsBytes();
    return bytes == null ? null : Image.memory(bytes);
  }

  Future<File?> captureAsFile([String? fileName]) async {
    var imageBytes = await captureAsBytes();
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
