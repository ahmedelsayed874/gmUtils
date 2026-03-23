import 'package:flutter/material.dart';
import 'package:share_plus/share_plus.dart';

import '../gm_main.dart';

class SharingHelper {
  static Future<void> share({
    required String text,
    required String? subject,
    bool careToBoxPosition = true,
    BuildContext? context,
  }) async {
    var context2 = context ?? App.context;
    if (careToBoxPosition && !context2.mounted) return;
    await Future.delayed(const Duration(milliseconds: 150));
    if (careToBoxPosition && !context2.mounted) return;

    Rect? sharePositionOrigin;

    if (careToBoxPosition) {
      final box = context2.findRenderObject();

      if (box is RenderBox) {
        sharePositionOrigin = box.localToGlobal(Offset.zero) & box.size;
      }
      //
      else {
        debugPrint("❌ RenderBox not found for Share");
      }
    }

    await SharePlus.instance.share(
      ShareParams(
        text: text,
        subject: subject,
        sharePositionOrigin: sharePositionOrigin,
      ),
    );
  }

  static Future<void> shareFile({
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
        debugPrint("❌ RenderBox not found for Share");
      }
    }

    await SharePlus.instance.share(ShareParams(
      text: text,
      files: [XFile(outputFilePath, mimeType: mimeType)],
      sharePositionOrigin: sharePositionOrigin,
    ));
  }
}
