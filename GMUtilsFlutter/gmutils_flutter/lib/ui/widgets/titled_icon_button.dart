import 'package:flutter/material.dart';

import '../../resources/_resources.dart';
import '../../zgmutils/ui/widgets/network_image_with_loading.dart';

class TitledIconButton extends StatefulWidget {
  static const double defaultWidth = 100;

  final double size;
  final String? assetIcon;
  final String? photoUrl;
  final Widget? alterPhotoUrl;
  final double iconSizeRatio;
  final String title;
  final int notificationCount;
  final VoidCallback onClick;

  const TitledIconButton({
    this.size = defaultWidth,
    required String? icon,
    this.photoUrl,
    this.alterPhotoUrl,
    this.iconSizeRatio = 0.4,
    required this.title,
    required this.notificationCount,
    required this.onClick,
    super.key,
  }) : assetIcon = icon;

  @override
  State<TitledIconButton> createState() => _TitledIconButtonState();
}

class _TitledIconButtonState extends State<TitledIconButton> {
  bool tapped = false;

  @override
  Widget build(BuildContext context) {
    return GestureDetector(
      onTap: onClick,
      child: SizedBox(
        width: widget.size,
        child: Column(
          children: [
            const SizedBox(height: 10),
            SizedBox(
              width: widget.size - 20,
              height: widget.size - 20,
              child: Stack(
                children: [
                  Container(
                    //margin: const EdgeInsets.all(7),
                    decoration: BoxDecoration(
                      color: tapped
                          ? Res.themes.colors.primary
                          : null, //Res.themes.colors.primary,
                      borderRadius: BorderRadius.circular(widget.size / 2),
                      border: Border.all(color: Res.themes.colors.secondary),
                    ),
                    child: Center(
                      child: widget.assetIcon != null
                          ? Image.asset(
                              widget.assetIcon!,
                              color: tapped
                                  ? Colors.white
                                  : Res.themes.colors.secondary,
                              width: widget.size * widget.iconSizeRatio,
                            )
                          : SizedBox(
                              width: widget.size * widget.iconSizeRatio,
                              child: NetworkImageWithLoading(
                                imgUrl: widget.photoUrl,
                                loadingPlaceHolder: widget.alterPhotoUrl!,
                                errorPlaceHolder: widget.alterPhotoUrl!,
                                onClick: (s) {
                                  onClick();
                                },
                              ),
                            ),
                    ),
                  ),

                  //count
                  if (widget.notificationCount > 0)
                    Align(
                      alignment: Alignment.topRight,
                      child: Container(
                        padding: EdgeInsets.symmetric(
                          horizontal: widget.notificationCount < 10 ? 7 : 3,
                        ),
                        decoration: BoxDecoration(
                          color: Res.themes.colors.red,
                          borderRadius: BorderRadius.circular(99),
                        ),
                        child: Text(
                          widget.notificationCount > 999
                              ? '+999'
                              : '${widget.notificationCount}',
                          style: Res.themes.defaultTextStyle(
                            textColor: Colors.white,
                            textSize: 10,
                          ),
                        ),
                      ),
                    ),
                ],
              ),
            ),
            Row(
              mainAxisAlignment: MainAxisAlignment.center,
              children: [
                Expanded(
                  child: Text(
                    widget.title,
                    textAlign: TextAlign.center,
                    maxLines: 2,
                    style: Res.themes.defaultTextStyle(
                      textSize: 14,
                      fontWeight: FontWeight.w700,
                    ),
                  ),
                ),
              ],
            ),
          ],
        ),
      ),
    );
  }

  void onClick() {
    if (tapped) return;

    setState(() {
      tapped = true;

      Future.delayed(const Duration(milliseconds: 120), () {
        setState(() {
          tapped = false;
          widget.onClick();
        });
      });
    });
  }
}
