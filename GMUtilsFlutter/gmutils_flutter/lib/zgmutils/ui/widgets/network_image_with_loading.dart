import 'dart:io';
import 'dart:typed_data';
import 'dart:ui';

import 'package:flutter/material.dart';
import 'package:path_provider/path_provider.dart';

import '../../gm_main.dart';
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
  final void Function(NetworkImageWithLoading)? onLoadComplete;
  final bool enableCaching;

  //---------------------

  int? imageWidth;
  int? imageHeight;
  bool isLoadingCompleted = false;

  File? cachedFile;

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
    Key? key,
  }) : super(key: key);

  static String? _cacheDirPath;

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
          } else {
            _cacheDirPath = s.data!.path;
            return build2(context, _cacheDirPath!);
          }
        },
      );
    } else {
      return build2(context, _cacheDirPath);
    }
  }

  Widget build2(BuildContext context, String? cacheDirPath) {
    if (enableCaching && imgUrl != null) {
      if (cachedFile == null && cacheDirPath != null) {
        var fileName = imgUrl!
            .replaceAll('://', '')
            .replaceAll('.', '-')
            .replaceAll('/', '-');

        cachedFile = File('$cacheDirPath/$fileName');
      }

      var cache = _getCachedImage(imgUrl!);

      if (cache != null) {
        return loadImageFromCachedMemory(cache);
      }
      //
      else if (cachedFile?.existsSync() == true) {
        return loadImageFromCachedFile();
      }
      //
      else {
        return loadImageFromWeb();
      }
    }
    //
    else {
      return loadImageFromWeb();
    }
  }

  Widget loadImageFromCachedMemory(_CachedImage cache) {
    //Logs.print(() => 'NetworkImageWithLoading.loadImageFromCachedMemory --> url: $imgUrl}');
    return GestureDetector(
      onTap: imgUrl == null
          ? null
          : () {
              if (allowEnlargeOnClick) {
                //Logs.print(() => imgUrl);
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
        errorBuilder: (context, error, stackTrace) {
          if (cachedFile?.existsSync() == true) {
            return loadImageFromCachedFile();
          } else {
            return loadImageFromWeb();
          }
        },
        fit: fit,
      ),
    );
  }

  Widget loadImageFromCachedFile() {
    //Logs.print(() => 'NetworkImageWithLoading.loadImageFromCachedFile --> url: $imgUrl}');

    Uint8List imgBytes = cachedFile!.readAsBytesSync();
    if (imgUrl != null) {
      _appendCachedImage(_CachedImage(name: imgUrl!, imageBytes: imgBytes));
    }

    return GestureDetector(
      onTap: imgUrl == null
          ? null
          : () {
              if (allowEnlargeOnClick) {
                //Logs.print(() => 'NetworkImageWithLoading.loadImageFromCachedFile.enlarge --> $imgUrl');
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
        errorBuilder: (context, error, stackTrace) {
          return loadImageFromWeb();
        },
        fit: fit,
      ),
    );
  }

  Widget loadImageFromWeb() {
    //Logs.print(() => 'NetworkImageWithLoading.loadImageFromWeb --> url: $imgUrl}');

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
        /*//Logs.print(() => 'NetworkImageWithLoading -> Image -> loadingBuilder[ '
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
              //Logs.print(() => imgUrl);
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
        return errorWidget;
      },
      fit: fit,
    );

    image.image.resolve(const ImageConfiguration()).addListener(
      ImageStreamListener((image, synchronousCall) async {
        var update = imageWidth == null;

        imageWidth = image.image.width;
        imageHeight = image.image.height;

        if (update) {
          isLoadingCompleted = true;
          if (onLoadComplete != null) {
            Future.delayed(const Duration(milliseconds: 200), () {
              onLoadComplete?.call(this);
            });
          }
        }

        if (enableCaching) {
          var b = await image.image.toByteData(format: ImageByteFormat.png);
          if (b != null) {
            var imgBytes = b.buffer.asUint8List();
            if (cachedFile?.existsSync() == true) {
              cachedFile?.deleteSync();
            }
            cachedFile?.createSync();
            cachedFile?.writeAsBytesSync(imgBytes);

            _appendCachedImage(
              _CachedImage(name: imgUrl!, imageBytes: imgBytes),
            );
          }
        }
      }),
    );

    return image;
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
  _cachedImagesList.forEach((e) {
    totalBytes += e.imageBytes.length;
  });
  if (totalBytes > maxBytes) {
    _cachedImagesList.removeAt(0);
    //_cachedImagesIndexes.remove(c.name);
    _cachedImagesIndex.clear();
    for (var i = 0; i < _cachedImages.length; i++) {
      var img = _cachedImages[i];
      _cachedImagesIndex[img.name] = i;
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
