import 'dart:io';

import 'package:image_picker/image_picker.dart' as imgPicker;

///https://pub.dev/packages/image_picker
class ImagePicker {
  Future<File?> pickPhoto({double? maxWidth, double? maxHeight}) async {
    final imgPicker.ImagePicker _picker = imgPicker .ImagePicker();
    final imgPicker.XFile? image =
        await _picker.pickImage(source: imgPicker.ImageSource.gallery,
          maxWidth: maxWidth,
          maxHeight: maxHeight,
        );

    File? returningImg;

    if (image != null) {
        returningImg = File(image.path);
      }

    return returningImg;
  }

  Future<List<File>> pickMultiplePhoto({double? maxWidth, double? maxHeight}) async {
    final imgPicker.ImagePicker _picker = imgPicker .ImagePicker();

    final List<imgPicker.XFile> images = await _picker.pickMultiImage(
      maxWidth: maxWidth,
      maxHeight: maxHeight,
      imageQuality: (maxWidth == null && maxHeight == null) ? null : 100,
    );

    List<File> returningImgs = images.map((e) => File(e.path)).toList();
    return returningImgs;
  }

  Future<File?> takePhoto({double? maxWidth, double? maxHeight}) async {
    final imgPicker.ImagePicker _picker = imgPicker.ImagePicker();
    final imgPicker.XFile? image =
        await _picker.pickImage(source: imgPicker.ImageSource.camera,
          maxWidth: maxWidth,
          maxHeight: maxHeight,);

    File? returningImg;

    if (image != null) {
        returningImg = File(image.path);
      }

    return returningImg;
  }

  Future<File?> pickVideo() async {
    final imgPicker.ImagePicker _picker = imgPicker.ImagePicker();

    // Pick a video
    final imgPicker.XFile? image = await _picker.pickVideo(
      source: imgPicker.ImageSource.gallery,
    );

    File? returningImg;

    if (image != null) {
      returningImg = File(image.path);
    }

    return returningImg;
  }

  void _otherHelpers() async {
    final imgPicker.ImagePicker _picker = imgPicker.ImagePicker();

    // Capture a video
    final imgPicker.XFile? video =
        await _picker.pickVideo(source: imgPicker.ImageSource.camera);
    // Pick multiple images
    final List<imgPicker.XFile>? images = await _picker.pickMultiImage();
  }
}
