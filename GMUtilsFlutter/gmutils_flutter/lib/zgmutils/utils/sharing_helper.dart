import 'package:flutter/material.dart';
import 'package:share_plus/share_plus.dart';

class SharingHelper {
  static void share({
    required String text,
    required String? subject,
    bool careToBoxPosition = true,
    BuildContext? context,
  }) async {
    if (careToBoxPosition && !context!.mounted) return;
    await Future.delayed(const Duration(milliseconds: 150));
    if (careToBoxPosition && !context!.mounted) return;

    Rect? sharePositionOrigin;

    if (careToBoxPosition) {
      final box = context!.findRenderObject();

      if (box is RenderBox) {
        sharePositionOrigin = box.localToGlobal(Offset.zero) & box.size;
      }
      //
      else {
        debugPrint("❌ RenderBox not found for Share");
      }
    }

    SharePlus.instance.share(
      ShareParams(
        text: text,
        subject: subject,
        sharePositionOrigin: sharePositionOrigin,
      ),
    );
  }

  static void shareFile({
    required String text,
    required String outputFilePath,
    bool careToBoxPosition = true,
    BuildContext? context,
  }) async {
    Rect? sharePositionOrigin;

    if (careToBoxPosition) {
      final box = context!.findRenderObject();

      if (box is RenderBox) {
        sharePositionOrigin = box.localToGlobal(Offset.zero) & box.size;
      }
      //
      else {
        debugPrint("❌ RenderBox not found for Share");
      }
    }

    SharePlus.instance.share(ShareParams(
      text: text,
      files: [XFile(outputFilePath)],
      sharePositionOrigin: sharePositionOrigin,
    ));
  }
}
