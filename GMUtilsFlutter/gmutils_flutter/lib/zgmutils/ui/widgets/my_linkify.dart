import 'package:flutter/material.dart';
import 'package:flutter_linkify/flutter_linkify.dart';

import '../../resources/app_theme.dart';
import '../../utils/launcher.dart';
import '../../utils/text/text_utils.dart';

class MyLinkify extends StatelessWidget {
  final String text;
  final TextStyle? textStyle;
  final TextStyle? linkTextStyle;
  final bool enableSelect;
  final LinkifyOptions? options;
  final List<Linkifier>? otherLinkifiers;

  const MyLinkify({
    required this.text,
    this.textStyle,
    this.linkTextStyle,
    this.enableSelect = false,
    this.options,
    this.otherLinkifiers,
    super.key,
  });

  @override
  Widget build(BuildContext context) {
    var text2 = text.replaceAll(' bit.ly', ' https://bit.ly');
    text2 = text2.replaceAll(' https://https://', ' https://');
    text2 = text2.replaceAll(' http://https://', ' https://');

    final defaultStyle = textStyle ?? AppTheme.defaultTextStyle(autoScaleTextSize: false,);
    final linkifyLinkStyle = linkTextStyle ??
        AppTheme.defaultTextStyle(
          textColor: Colors.blueAccent,
          autoScaleTextSize: false,
        );
    final textDirection =
        TextUtils().isStartWithArabic(text2) == true ? TextDirection.rtl : null;
    onOpen(e) {
      if (e is UrlElement) {
        Launcher().openUrl(e.url);
      }
      //
      else if (e is EmailElement) {
        Launcher().sendEmail(e.emailAddress, '', '');
      }
    }

    List<Linkifier> linkifiers = defaultLinkifiers;
    if (otherLinkifiers?.isNotEmpty == true) {
      linkifiers.addAll(otherLinkifiers!);
    }

    return enableSelect
        ? SelectableLinkify(
            text: text2,
            linkifiers: linkifiers,
            style: defaultStyle,
            linkStyle: linkifyLinkStyle,
            textDirection: textDirection,
            options: options ?? const LinkifyOptions(),
            onOpen: onOpen,
          )
        : Linkify(
            text: text2,
            linkifiers: linkifiers,
            style: defaultStyle,
            linkStyle: linkifyLinkStyle,
            textDirection: textDirection,
            options: options ?? const LinkifyOptions(),
            onOpen: onOpen,
          );
  }
}

const _urlLinkifier = UrlLinkifier();
const _emailLinkifier = EmailLinkifier();
const defaultLinkifiers = [_urlLinkifier, _emailLinkifier];
