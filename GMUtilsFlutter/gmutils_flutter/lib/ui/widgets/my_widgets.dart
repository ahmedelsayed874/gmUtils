import 'package:gmutils_flutter/main.dart' as main;
import 'package:gmutils_flutter/resources/_resources.dart';
import 'package:gmutils_flutter/zgmutils/ui/widgets/network_image_with_loading.dart';
import 'package:flutter/material.dart';

class MyWidgets {
  Widget userPhotoAvatar({
    required String? photoPath,
    required double size,
    Color? strokeColor,
    Widget? defaultWidget,
    IconData? orDefaultIcon,
    void Function(String url)? onClick,
  }) {
    if (photoPath != null) {
      if (photoPath.toLowerCase().startsWith(main.serverUrl)) {
        photoPath = photoPath.replaceAll(main.serverUrl, '');
      }
      if (photoPath.toLowerCase().startsWith('http') == false) {
        if (photoPath.toLowerCase().startsWith(main.serverUrl) == false) {
          photoPath = main.serverUrl + photoPath;
        }
      }
    }

    return Stack(
      children: [
        Container(
          decoration: BoxDecoration(
            borderRadius: BorderRadius.circular(size/2),
            color: strokeColor ?? Res.themes.colors.primaryVariant.withAlpha(180),
          ),
          clipBehavior: Clip.hardEdge,
          width: size,
          height: size,
        ),
        Container(
          margin: EdgeInsets.all(1),
          decoration: BoxDecoration(
            borderRadius: BorderRadius.circular(size/2),
            color: Res.themes.colors.primaryVariant,
            //color: Res.themes.colors.red,
          ),
          clipBehavior: Clip.hardEdge,
          width: size - 2,
          height: size - 2,
          child: NetworkImageWithLoading(
            imgUrl: photoPath,
            fit: BoxFit.fill,
            desiredImageHeight: size - 2,
            desiredImageWidth: size - 2,
            onClick: onClick,
            loadingPlaceHolder: Icon(
              Icons.person,
              color: Res.themes.colors.primary,
              size: size - 10,
            ),
            errorPlaceHolder: defaultWidget ?? Icon(
              orDefaultIcon ?? Icons.person,
              color: Res.themes.colors.primary,
              size: size - 10,
            ),
            errorPlaceHolderBackgroundColor: Res.themes.colors.primaryVariant,
          ),
        ),
      ],
    );
  }

  //===========================================================================

  Widget row({required List<Widget> children, double space = 30}) {
    List<Widget> children2 = [];
    var i = 0;

    for (var child in children) {
      children2.add(child);
      i++;
      if (i < children.length) {
        children2.add(SizedBox(width: space));
      }
    }

    return Row(
        mainAxisSize: MainAxisSize.min,
        crossAxisAlignment: CrossAxisAlignment.start,
        children: children2);
  }

  Widget screenTitle({required text}) {
    return Center(
      child: Padding(
        padding: const EdgeInsets.only(bottom: 10),
        child: Text(
          text,
          textAlign: TextAlign.center,
          style: Res.themes.textStyleOfScreenTitle(),
        ),
      ),
    );
  }

}