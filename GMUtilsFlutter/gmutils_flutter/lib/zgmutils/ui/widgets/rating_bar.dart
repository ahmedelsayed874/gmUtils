import 'package:flutter/material.dart';
import 'package:flutter_rating_bar/flutter_rating_bar.dart';

class RatingBarCr {
  static Widget fixed({
    double value = 5.0,
    Color color = Colors.amber,
    double size = 17.0,
  }) {
    List<Widget> children = [];

    int valueInt = value.toInt();

    for (int i = 1; i <= valueInt; i++) {
      children.add(Icon(Icons.star, color: color, size: size));
    }

    if (value - valueInt > 0.0) {
      children.add(Icon(Icons.star_half, color: color, size: size));
    }

    int rem = 5 - children.length;

    for (int i = 0; i < rem; i++) {
      children.add(Icon(Icons.star_border, color: color, size: size));
    }

    return Row(
      mainAxisAlignment: MainAxisAlignment.center,
      children: children,
    );
  }

  static RatingBar reacted({
    required ValueChanged<double> onRatingUpdate,
    double initialRating = 5.0,
    Color color = Colors.amber,
    double size = 25.0,
  }) {
    var ratingWidget = RatingWidget(
      full: Icon(Icons.star, color: color, size: size),
      half: Icon(Icons.star_half, color: color, size: size),
      empty: Icon(Icons.star_border, color: color, size: size),
    );

    return RatingBar(
      onRatingUpdate: onRatingUpdate,
      initialRating: initialRating,
      itemCount: 5,
      itemSize: size,
      allowHalfRating: false,
      ignoreGestures: false,
      tapOnlyMode: false,
      updateOnDrag: false,
      itemPadding: EdgeInsets.symmetric(horizontal: 4.0),
      ratingWidget: ratingWidget,
    );
  }
}
