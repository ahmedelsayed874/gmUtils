import 'dart:io';
import 'dart:ui';

import 'package:image_cropper/image_cropper.dart' as ic;

import '../../../resources/_resources.dart';

///https://pub.dev/packages/image_cropper
/*
  <activity
    android:name="com.yalantis.ucrop.UCropActivity"
    android:screenOrientation="portrait"
    android:theme="@style/Theme.AppCompat.Light.NoActionBar"/>
 */
class ImageCropper {
  Future<File?> cropImage({
    required File imageFile,
    required String toolbarTitle,
    Color? toolbarColor,
  }) async {
    final croppedFile = await ic.ImageCropper().cropImage(
      sourcePath: imageFile.path,
      compressFormat: ic.ImageCompressFormat.jpg,
      compressQuality: 100,
      aspectRatio: const ic.CropAspectRatio(ratioX: 1, ratioY: 1),
      //aspectRatioPresets: [ic.CropAspectRatioPreset.square],
      uiSettings: [
        ic.AndroidUiSettings(
          toolbarTitle: toolbarTitle,
          toolbarColor: toolbarColor ?? Res.themes.colors.primary,
          toolbarWidgetColor: const Color.fromARGB(255, 255, 255, 255),
          initAspectRatio: ic.CropAspectRatioPreset.square,
          lockAspectRatio: true,
          hideBottomControls: true,
        ),
        ic.IOSUiSettings(
          title: toolbarTitle,
          aspectRatioLockEnabled: true,
        ),
      ],
    );

    if (croppedFile != null) {
      return File(croppedFile.path);
    } else {
      return null;
    }
  }
}
