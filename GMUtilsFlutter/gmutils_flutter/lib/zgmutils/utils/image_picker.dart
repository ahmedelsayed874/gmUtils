import 'dart:io';

import 'package:image_picker/image_picker.dart' as img_picker;

///https://pub.dev/packages/image_picker
class ImagePicker {
  Future<File?> pickPhoto({double? maxWidth, double? maxHeight}) async {
    final img_picker.ImagePicker _picker = img_picker.ImagePicker();
    final img_picker.XFile? image = await _picker.pickImage(
      source: img_picker.ImageSource.gallery,
      maxWidth: maxWidth,
      maxHeight: maxHeight,
    );

    File? returningImg;

    if (image != null) {
      returningImg = File(image.path);
    }

    return returningImg;
  }

  Future<List<File>> pickMultiplePhoto(
      {double? maxWidth, double? maxHeight}) async {
    final img_picker.ImagePicker _picker = img_picker.ImagePicker();

    final List<img_picker.XFile> images = await _picker.pickMultiImage(
      maxWidth: maxWidth,
      maxHeight: maxHeight,
      imageQuality: (maxWidth == null && maxHeight == null) ? null : 100,
    );

    List<File> returningImgs = images.map((e) => File(e.path)).toList();
    return returningImgs;
  }

  Future<File?> takePhoto({double? maxWidth, double? maxHeight}) async {
    final img_picker.ImagePicker _picker = img_picker.ImagePicker();
    final img_picker.XFile? image = await _picker.pickImage(
      source: img_picker.ImageSource.camera,
      maxWidth: maxWidth,
      maxHeight: maxHeight,
    );

    File? returningImg;

    if (image != null) {
      returningImg = File(image.path);
    }

    return returningImg;
  }

  Future<File?> pickVideo() async {
    final img_picker.ImagePicker _picker = img_picker.ImagePicker();

    // Pick a video
    final img_picker.XFile? image = await _picker.pickVideo(
      source: img_picker.ImageSource.gallery,
    );

    File? returningImg;

    if (image != null) {
      returningImg = File(image.path);
    }

    return returningImg;
  }

  /*void _otherHelpers() async {
    //final img_picker.ImagePicker _picker = img_picker.ImagePicker();

    // Capture a video
    //final img_picker.XFile? video =  await _picker.pickVideo(source: img_picker.ImageSource.camera);

    // Pick multiple images
    //final List<img_picker.XFile>? images = await _picker.pickMultiImage();
  }*/
}
