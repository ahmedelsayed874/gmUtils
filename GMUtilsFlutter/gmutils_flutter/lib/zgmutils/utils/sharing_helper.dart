import 'package:flutter/material.dart';
import 'package:share_plus/share_plus.dart';

import '../gm_main.dart';
import 'logs.dart';

class SharingHelper {
  static Future<bool> share({
    required String text,
    required String? subject,
    bool careToBoxPosition = true,
    BuildContext? context,
  }) async {
    var context2 = context ?? App.context;
    if (careToBoxPosition && !context2.mounted) {
      Logs.printMethod(extraInfo: () => 'ERROR: context not mounted.');
      return false;
    }

    await Future.delayed(const Duration(milliseconds: 150));
    if (careToBoxPosition && !context2.mounted) {
      Logs.printMethod(extraInfo: () => 'ERROR:: context not mounted.');
    }

    Rect? sharePositionOrigin;

    if (careToBoxPosition) {
      final box = context2.findRenderObject();

      if (box is RenderBox) {
        sharePositionOrigin = box.localToGlobal(Offset.zero) & box.size;
      }
      //
      else {
        Logs.printMethod(extraInfo: () => "❌ RenderBox not found for Share");
      }
    }

    var res = await SharePlus.instance.share(
      ShareParams(
        text: text,
        subject: subject,
        sharePositionOrigin: sharePositionOrigin,
      ),
    );

    return res.status == ShareResultStatus.success;
  }

  static Future<bool> shareFile({
    required String text,
    required String outputFilePath,
    String? mimeType,
    bool careToBoxPosition = true,
    BuildContext? context,
  }) async {
    var context2 = context ?? App.context;
    Rect? sharePositionOrigin;

    if (careToBoxPosition) {
      final box = context2.findRenderObject();

      if (box is RenderBox) {
        sharePositionOrigin = box.localToGlobal(Offset.zero) & box.size;
      }
      //
      else {
        Logs.printMethod(extraInfo: () => "❌ RenderBox not found for Share");
      }
    }

    var res = await SharePlus.instance.share(ShareParams(
      text: text,
      files: [XFile(outputFilePath, mimeType: mimeType)],
      sharePositionOrigin: sharePositionOrigin,
    ));

    return res.status == ShareResultStatus.success;
  }
}
