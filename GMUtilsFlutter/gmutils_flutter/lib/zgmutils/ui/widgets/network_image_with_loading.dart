import 'dart:io';
import 'dart:typed_data';
import 'dart:ui';

import 'package:flutter/material.dart';
import 'package:path_provider/path_provider.dart';

import '../../gm_main.dart';
import '../../utils/logs.dart';
import 'image_viewer_screen.dart';

class NetworkImageWithLoading extends StatelessWidget {
  final String? imgUrl;
  final bool allowEnlargeOnClick;
  final String toolbarTitle;
  final double? desiredImageWidth;
  final double? desiredImageHeight;
  final double progressSize;
  final void Function(String)? onClick;
  final Widget? loadingPlaceHolder;
  final Widget errorPlaceHolder;
  final Color? errorPlaceHolderBackgroundColor;
  final int? errorPlaceHolderSize;
  final BoxFit? fit;
  final void Function(bool success, ImageMetaData?)? onLoadComplete;
  final bool enableCaching;

  //---------------------

  NetworkImageWithLoading({
    required this.imgUrl,
    this.allowEnlargeOnClick = false,
    this.toolbarTitle = '',
    this.desiredImageWidth,
    this.desiredImageHeight,
    this.progressSize = 25,
    this.onClick,
    this.loadingPlaceHolder,
    this.errorPlaceHolder = const Icon(Icons.image_not_supported_outlined),
    this.errorPlaceHolderBackgroundColor, // = (AppTheme.appColors?.hint ?? Colors.grey[400]),
    this.errorPlaceHolderSize,
    this.fit,
    this.onLoadComplete,
    this.enableCaching = true,
    super.key,
  });

  int? _imageWidth;
  File? _cachedFile;
  static String? _cacheDirPath;
  bool _onLoadInvoked = false;

  @override
  Widget build(BuildContext context) {
    if (_cacheDirPath == null && enableCaching) {
      return FutureBuilder(
        future: getApplicationCacheDirectory(),
        builder: (context, s) {
          if (s.data == null) {
            return SizedBox(
              width: desiredImageWidth,
              height: desiredImageHeight,
            );
          }
          //
          else {
            _cacheDirPath = s.data!.path;
            return _build2(context, _cacheDirPath!);
          }
        },
      );
    }
    //
    else {
      return _build2(context, _cacheDirPath);
    }
  }

  Widget _build2(BuildContext context, String? cacheDirPath) {
    if (enableCaching && imgUrl != null) {
      if (_cachedFile == null && cacheDirPath != null) {
        var fileName = imgUrl!
            .replaceAll('://', '')
            .replaceAll('.', '-')
            .replaceAll('/', '-');

        _cachedFile = File('$cacheDirPath/$fileName');
      }

      var cache = _getCachedImage(imgUrl!);

      if (cache != null) {
        return _loadImageFromCachedMemory(cache);
      }
      //
      else if (_cachedFile?.existsSync() == true) {
        return _loadImageFromCachedFile();
      }
      //
      else {
        return _loadImageFromWeb();
      }
    }
    //
    else {
      return _loadImageFromWeb();
    }
  }

  Widget _loadImageFromCachedMemory(_CachedImage cache) {
    Logs.print(() =>
        'NetworkImageWithLoading._loadImageFromCachedMemory(cache: ${cache.name})');

    _onLoadInvoked = false;

    return GestureDetector(
      onTap: imgUrl == null
          ? null
          : () {
              if (allowEnlargeOnClick) {
                ImageViewerScreen.showFromUrl(
                  toolbarTitle: toolbarTitle,
                  photoUrl: imgUrl!,
                );
              }
              //
              else if (onClick != null) {
                onClick?.call(imgUrl!);
              }
            },
      child: Image.memory(
        cache.imageBytes,
        width: desiredImageWidth,
        height: desiredImageHeight,
        frameBuilder: (context, child, int? frame, wasSynchronouslyLoaded) {
          /*Log s.print(() =>
              'NetworkImageWithLoading._loadImageFromCachedMemory.ON_SUCCESS');*/

          _invokeOnLoadComplete(true, null);

          return child;
        },
        errorBuilder: (context, error, stackTrace) {
          if (_cachedFile?.existsSync() == true) {
            return _loadImageFromCachedFile();
          }
          //
          else {
            return _loadImageFromWeb();
          }
        },
        fit: fit,
      ),
    );
  }

  Widget _loadImageFromCachedFile() {
    Logs.print(() =>
        'NetworkImageWithLoading._loadImageFromCachedFile(_cachedFile: ${_cachedFile?.path}');

    _onLoadInvoked = false;

    Uint8List imgBytes = _cachedFile!.readAsBytesSync();
    if (imgUrl != null) {
      _appendCachedImage(_CachedImage(name: imgUrl!, imageBytes: imgBytes));
    }

    return GestureDetector(
      onTap: imgUrl == null
          ? null
          : () {
              if (allowEnlargeOnClick) {
                ImageViewerScreen.showFromUrl(
                  toolbarTitle: toolbarTitle,
                  photoUrl: imgUrl!,
                );
              }
              //
              else if (onClick != null) {
                onClick?.call(imgUrl!);
              }
            },
      child: Image.memory(
        imgBytes,
        width: desiredImageWidth,
        height: desiredImageHeight,
        frameBuilder: (context, child, int? frame, wasSynchronouslyLoaded) {
          /*Log s.print(() =>
              'NetworkImageWithLoading._loadImageFromCachedFile.ON_SUCCESS');*/

          _invokeOnLoadComplete(true, null);

          return child;
        },
        errorBuilder: (context, error, stackTrace) {
          return _loadImageFromWeb();
        },
        fit: fit,
      ),
    );
  }

  Widget _loadImageFromWeb() {
    Logs.print(
        () => 'NetworkImageWithLoading._loadImageFromWeb(imgUrl: $imgUrl)');

    _onLoadInvoked = false;

    var errorWidget = Center(
      child: Container(
        color: errorPlaceHolderBackgroundColor,
        width: errorPlaceHolderSize?.toDouble(),
        height: errorPlaceHolderSize?.toDouble(),
        child: onClick == null
            ? errorPlaceHolder
            : GestureDetector(
                onTap: () => onClick?.call(imgUrl ?? ''),
                child: errorPlaceHolder,
              ),
      ),
    );
    var lnk = imgUrl ?? '';
    if (lnk.isEmpty) return errorWidget;

    var image = Image.network(
      lnk,
      width: desiredImageWidth,
      height: desiredImageHeight,
      loadingBuilder: (ctx, child, loadingProgress) {
        /*Log s.print(() => 'NetworkImageWithLoading -> Image -> loadingBuilder[ '
            'imgUrl: $imgUrl, '
            'child: $child, '
            'progress: ['
            'cumulativeBytesLoaded: ${loadingProgress?.cumulativeBytesLoaded}, '
            'expectedTotalBytes: ${loadingProgress?.expectedTotalBytes}'
            ']'
            ' ]');*/

        Widget widget;
        if (allowEnlargeOnClick) {
          widget = GestureDetector(
            onTap: () {
              ImageViewerScreen.showFromUrl(
                toolbarTitle: toolbarTitle,
                photoUrl: imgUrl ?? '',
              );
            },
            child: child,
          );
        }
        //
        else if (onClick != null) {
          widget = GestureDetector(
            onTap: () => onClick?.call(imgUrl ?? ''),
            child: child,
          );
        }
        //
        else {
          widget = child;
        }

        if (loadingProgress?.expectedTotalBytes == null) {
          return widget;
        } else {
          var e = loadingProgress!.expectedTotalBytes;
          var d = loadingProgress.cumulativeBytesLoaded;
          if (e == d) {
            return widget;
          } else {
            return Stack(
              children: [
                if (loadingPlaceHolder != null)
                  Align(
                    alignment: Alignment.center,
                    child: loadingPlaceHolder,
                  ),

                //
                Align(
                  alignment: Alignment.center,
                  child: SizedBox(
                    width: progressSize,
                    height: progressSize,
                    child: const CircularProgressIndicator(
                      strokeWidth: 2,
                    ),
                  ),
                ),
              ],
            );
          }
        }
      },
      errorBuilder: (context, error, stackTrace) {
        /*Log s.print(() => 'NetworkImageWithLoading._loadImageFromWeb -> ERROR ->'
            'loading image failed for: $imgUrl');*/

        _invokeOnLoadComplete(false, null);
        return errorWidget;
      },
      fit: fit,
    );

    image.image.resolve(const ImageConfiguration()).addListener(
      ImageStreamListener((image, synchronousCall) async {
        //var imgW = _imageWidth;
        var update = _imageWidth == null;

        /*Log s.print(() => 'NetworkImageWithLoading._loadImageFromWeb -> '
            'ImageStreamListener -> '
            'imageWidth: ${imgW} ---> '
            'update: $update');*/

        if (update) {
          if (onLoadComplete != null) {
            var imageMetaData = ImageMetaData(
              imgUrl: imgUrl ?? '',
              imageWidth: image.image.width,
              imageHeight: image.image.height,
              cachedFile: _cachedFile,
            );
            _invokeOnLoadComplete(true, imageMetaData);
          }
        }

        if (enableCaching) {
          var b = await image.image.toByteData(format: ImageByteFormat.png);
          if (b != null) {
            var imgBytes = b.buffer.asUint8List();
            if (_cachedFile?.existsSync() == true) {
              _cachedFile?.deleteSync();
            }
            _cachedFile?.createSync();
            _cachedFile?.writeAsBytesSync(imgBytes);

            _appendCachedImage(
              _CachedImage(name: imgUrl!, imageBytes: imgBytes),
            );
          }
        }
      }),
    );

    return image;
  }

  void _invokeOnLoadComplete(bool success, ImageMetaData? meta) {
    Logs.print(
      () => 'NetworkImageWithLoading'
          '._invokeOnLoadComplete(success: $success) .... '
          'was invoked? $_onLoadInvoked}',
    );

    if (_onLoadInvoked) return;
    _onLoadInvoked = true;

    if (onLoadComplete == null) return;

    Future.delayed(const Duration(milliseconds: 300), () {
      try {
        onLoadComplete?.call(success, meta);
      } catch (e1) {
        _invokeOnLoadComplete(success, meta);
      }
    });
  }
}

//==============================================================================

class _CachedImage {
  final String name;
  final Uint8List imageBytes;

  _CachedImage({required this.name, required this.imageBytes});
}

_CachedImage? _getCachedImage(String name) {
  var i = _cachedImagesIndexes[name] ?? -1;
  if (i >= 0 && i < _cachedImagesList.length) {
    return _cachedImagesList[i];
  } else {
    return null;
  }
}

void _appendCachedImage(_CachedImage image) {
  const maxBytes = 30 /*MB*/ * 1024 /*KB*/ * 1024 /*B*/;
  var totalBytes = 0;
  for (var e in _cachedImagesList) {
    totalBytes += e.imageBytes.length;
  }
  if (totalBytes > maxBytes) {
    _cachedImagesList.removeAt(0);
    _cachedImagesIndexes.clear();

    for (var i = 0; i < _cachedImagesIndexes.length; i++) {
      var img = _cachedImagesList[i];
      _cachedImagesIndexes[img.name] = i;
    }
  }

  _cachedImagesList.add(image);
  _cachedImagesIndexes[image.name] = _cachedImagesList.length - 1;
}

List<_CachedImage> get _cachedImagesList {
  var lst = App.globalVariables['NetworkImageWithLoading.cachedImages.storage'];
  if (lst == null) {
    lst = <_CachedImage>[];
    App.globalVariables['NetworkImageWithLoading.cachedImages.storage'] = lst;
  }
  return lst;
}

Map<String, int> get _cachedImagesIndexes {
  var map = App.globalVariables['NetworkImageWithLoading.cachedImages.indexes'];
  if (map == null) {
    map = <String, int>{};
    App.globalVariables['NetworkImageWithLoading.cachedImages.indexes'] = map;
  }
  return map;
}

//==============================================================================

class ImageMetaData {
  final String imgUrl;

  final int imageWidth;
  final int imageHeight;

  final File? cachedFile;

  ImageMetaData({
    required this.imgUrl,
    required this.imageWidth,
    required this.imageHeight,
    required this.cachedFile,
  });

  @override
  String toString() {
    return 'ImageMetaData{imgUrl: $imgUrl, imageWidth: $imageWidth, imageHeight: $imageHeight, _cachedFile: $cachedFile}';
  }
}
