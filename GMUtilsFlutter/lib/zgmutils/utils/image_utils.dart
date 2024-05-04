import 'dart:io';
import 'dart:math';

import 'package:flutter/foundation.dart';
import 'package:image/image.dart' as img;
import 'package:path_provider/path_provider.dart';


class ImageUtils {
  Future<File?> resizeImage(
    File imageFile,
    int desiredWidth,
    int desiredHeight,
  ) async {
    return resizeImage2(imageFile.readAsBytesSync(), desiredWidth, desiredHeight);
  }

  Future<File?> resizeImage2(
    Uint8List imageBytes,
    int desiredWidth,
    int desiredHeight,
  ) async {
    img.Image? image = img.decodeImage(imageBytes);
    if (image != null) {
      int h = image.height;
      int w = image.width;
      if (h * w > desiredHeight * desiredWidth) {
        if (w > h) {
          desiredHeight = desiredWidth * h ~/ w;
        } else {
          desiredWidth = desiredHeight * w ~/ h;
        }
      } else {
        desiredWidth = w;
        desiredHeight = h;
      }

      img.Image smallerImage = img.copyResize(
        image,
        width: desiredWidth,
        height: desiredHeight,
      );

      //-------------------------------------

      final tempDir = await getTemporaryDirectory();
      final path = tempDir.path;
      int rand = Random().nextInt(100000);

      var file = File('$path/img_$rand.jpg');
      file.writeAsBytesSync(img.encodeJpg(smallerImage), flush: true);
      return file;
    } else {
      return null;
    }
  }

}
