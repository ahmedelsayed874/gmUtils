import 'package:flutter/material.dart';

import '../../resources/app_theme.dart';
import '../../utils/avatar_utils.dart';
import 'network_image_with_loading.dart';

///replacement of CircleAvatar
class CircularImage extends StatelessWidget {
  final String? imageUrl;
  final double? size;
  final CircularImagePlaceholder? placeholder;
  final Function? onTap;
  final bool addBorder;
  final Color? backgroundColor;

  const CircularImage({
    required this.imageUrl,
    this.size,
    this.placeholder,
    this.onTap,
    this.backgroundColor,
    this.addBorder = false,
    super.key,
  });

  @override
  Widget build(BuildContext context) {
    Widget? placeholderWidget;
    if (placeholder?.assetFileName != null) {
      placeholderWidget =
          Image.asset('assets/images/${placeholder!.assetFileName}');
    }
    //
    else if (placeholder?.alterName != null) {
      placeholderWidget = Text(
        AvatarUtils().avatarText(placeholder!.alterName!),
        style: placeholder?.alterNameStyle ??
            AppTheme.defaultTextStyle(
              textColor: AppTheme.appColors?.black.withAlpha(100),
              fontWeight: FontWeight.bold,
              textSize: 30,
            ),
      );
    }
    //
    else {
      placeholderWidget = const SizedBox.shrink();
    }

    final widget = ClipOval(
      child: Container(
        width: size,
        height: size,
        color: backgroundColor,
        child: NetworkImageWithLoading(
          imgUrl: imageUrl,
          loadingPlaceHolder: placeholderWidget,
          errorPlaceHolder: placeholderWidget,
          onClick: onTap == null ? null : (url) => onTap!(),
          fit: BoxFit.cover,
        ),
      ),
    );

    if (addBorder) {
      return _container(widget);
    } else {
      return widget;
    }
  }

  Widget _container(Widget child) {
    return Container(
      padding: const EdgeInsets.all(3),
      decoration: BoxDecoration(
        shape: BoxShape.circle,
        border: Border.all(
          color: AppTheme.appColors?.primary ?? Colors.grey,
          width: 2,
        ),
      ),
      child: child,
    );
  }
}

class CircularImagePlaceholder {
  final String? assetFileName;
  final String? alterName;
  final TextStyle? alterNameStyle;

  CircularImagePlaceholder({
    this.assetFileName,
    this.alterName,
    this.alterNameStyle,
  });
}
