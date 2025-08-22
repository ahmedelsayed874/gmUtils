import 'dart:io';

import 'package:flutter/material.dart';
import 'package:flutter/services.dart';

import '../../gm_main.dart';
import '../../resources/app_theme.dart';
import '../../utils/logs.dart';

class MyRootWidget {
  String toolbarTitle = '';
  bool awareTopSafeArea = true;
  PreferredSizeWidget? _appBar;
  bool _showBackButton = false;
  Color? _backButtonColor;
  Widget? _drawer;
  DrawerCallback? _onDrawerChanged;
  late Color _background;
  late Widget _body;
  Widget? _floatingActionButton;
  EdgeInsets? _screenPadding;
  bool? _resizeToAvoidBottomInset;
  Widget? _bottomNavigationBar;
  Color? _statusBarColor;
  bool _isStatusBarThemeLight = true;

  MyRootWidget.withToolbar(this.toolbarTitle) {
    setupToolbar(null);
    _showBackButton = false;

    _background = AppTheme.appColors?.background ?? Colors.white;
    _body = const Text('use "setBody" method');
  }

  MyRootWidget.withoutToolbar({
    bool? awareTopSafeArea,
    bool showBackButton = false,
    Color? backButtonColor,
  }) {
    this.awareTopSafeArea = awareTopSafeArea ?? Platform.isAndroid;

    _showBackButton = showBackButton;
    _backButtonColor = backButtonColor;

    _background = AppTheme.appColors?.background ?? Colors.white;
    _body = const Text('use "setBody" method');
  }

  void configStatusBar({
    required Color statusBarColor,
    bool isStatusBarThemeLight = true,
  }) {
    _statusBarColor = statusBarColor;
    _isStatusBarThemeLight = isStatusBarThemeLight;
  }

  void setupToolbar(Widget? leading, [List<Widget>? action]) {
    _appBar = AppBar(
      centerTitle: true,
      leading: leading,
      actions: action,
      foregroundColor: AppTheme.appColors?.toolbarVariant,
      backgroundColor: AppTheme.appColors?.toolbar,
      title: Text(
        toolbarTitle,
        style: AppTheme.textStyleOfScreenTitle(
          textColor: AppTheme.appColors?.toolbarVariant,
          textSize: AppTheme.appMeasurement?.toolbarTitleSize,
          fontFamily: AppTheme.toolbarTitleFontFamily,
        ),
      ),
    );
  }

  MyRootWidget setToolbarActions({
    List<IconButton>? actions,
    PopupMenuButton? popupMenuButton,
    Widget? leading,
  }) {
    List<Widget> a = [];

    if (actions?.isNotEmpty == true) a.addAll(actions!);
    if (popupMenuButton != null) a.add(popupMenuButton);

    if (a.isNotEmpty) setupToolbar(leading, a);

    return this;
  }

  MyRootWidget setAppBar(AppBar? appBar) {
    _appBar = appBar;
    return this;
  }

  MyRootWidget removeToolbar() {
    _appBar = null;
    return this;
  }

  MyRootWidget setDrawer(Widget widget, {DrawerCallback? onDrawerChanged}) {
    _drawer = widget;
    _onDrawerChanged = onDrawerChanged;
    return this;
  }

  MyRootWidget changeBackground(Color bgColor) {
    _background = bgColor;
    return this;
  }

  MyRootWidget changeBackgroundForSplash() {
    changeBackground(AppTheme.appColors!.primary);
    return this;
  }

  MyRootWidget setScreenPadding(
    double top,
    double left,
    double right,
    double bottom,
  ) {
    _screenPadding = EdgeInsets.only(
      top: top,
      left: left,
      right: right,
      bottom: bottom,
    );
    return this;
  }

  MyRootWidget resizeToAvoidBottomInset(bool b) {
    _resizeToAvoidBottomInset = b;
    return this;
  }

  MyRootWidget setBody(Widget body, {bool scrollable = false}) {
    if (scrollable) {
      _body = SingleChildScrollView(child: body);
    } else {
      _body = body;
    }

    return this;
  }

  MyRootWidget setBodyWithTitle(
    String title,
    Widget body, {
    String? hint,
    double titleHeight = 60,
    bool scrollable = false,
    TextStyle? titleStyle,
    TextStyle? hintStyle,
  }) {
    Widget body2;
    if (scrollable) {
      body2 = SingleChildScrollView(child: body);
    } else {
      body2 = body;
    }

    _body = Column(
      children: [
        titleWidget(
          title,
          hint: hint,
          titleHeight: titleHeight,
          titleStyle: titleStyle,
          hintStyle: hintStyle,
        ),
        Expanded(child: body2)
      ],
    );
    return this;
  }

  Widget titleWidget(
    String title, {
    String? hint,
    double titleHeight = 150,
    TextStyle? titleStyle,
    TextStyle? hintStyle,
  }) {
    return SizedBox(
      height: titleHeight,
      child: Center(
        child: Column(
          children: [
            const SizedBox(height: 10),
            Text(
              title,
              style: titleStyle ?? AppTheme.textStyleOfScreenTitle(),
              textAlign: TextAlign.center,
            ),
            if (hint != null)
              Text(
                hint,
                textAlign: TextAlign.center,
                style: hintStyle ??
                    TextStyle(
                      color: AppTheme.appColors?.hint,
                      fontSize: AppTheme.appMeasurement?.screenTitleSize,
                    ),
              )
          ],
        ),
      ),
    );
  }

  MyRootWidget setFloatingActionButton(Widget? widget) {
    _floatingActionButton = widget;
    return this;
  }

  MyRootWidget setBottomNavigationBar(Widget? widget) {
    _bottomNavigationBar = widget;
    return this;
  }

  Widget build() {
    if (_drawer != null && _appBar == null) {
      if (_body is! StatelessWidget && _body is! StatefulWidget) {
        Logs.print(() => '******** ERROR ********* it is recommended to extend '
            '"body" from StatelessWidget or StatefulWidget to be able to use '
            'getCurrentScaffoldState() method');
      }
    }

    return Scaffold(
      appBar: _appBar,
      drawer: _drawer,
      onDrawerChanged: _onDrawerChanged,
      backgroundColor: _statusBarColor ?? Colors.black,
      body: _appBar == null
          ? SafeArea(
              top: awareTopSafeArea,
              bottom: Platform.isIOS ? false : true,
              child: AnnotatedRegion<SystemUiOverlayStyle>(
                value: _isStatusBarThemeLight
                    ? SystemUiOverlayStyle.light
                    : SystemUiOverlayStyle.dark,
                child: Container(
                  color: _background,
                  width: double.maxFinite,
                  height: double.maxFinite,
                  child: Stack(children: [
                    _defaultWidget(),
                    if (_showBackButton)
                      IconButton(
                        onPressed: () => App.navBack(),
                        icon: Icon(
                          App.isEnglish
                              ? Icons.arrow_back_ios_new
                              : Icons.arrow_forward_ios,
                          color: _backButtonColor,
                        ),
                      ),
                  ]),
                ),
              ),
            )
          : _defaultWidget(),
      floatingActionButton: _floatingActionButton,
      resizeToAvoidBottomInset: _resizeToAvoidBottomInset,
      bottomNavigationBar: _bottomNavigationBar,
    );
  }

  Widget _defaultWidget() {
    return Container(
      // color: Colors.black,
      width: double.maxFinite,
      padding: _screenPadding ??
          EdgeInsets.only(
            top: AppTheme.appMeasurement?.screenPaddingTop ?? 0.0,
            left: AppTheme.appMeasurement?.screenPaddingLeft ?? 0.3,
            right: AppTheme.appMeasurement?.screenPaddingRight ?? 0.3,
            bottom: AppTheme.appMeasurement?.screenPaddingBottom ?? 0.0,
          ),
      child: DefaultTextStyle(
        style: AppTheme.defaultTextStyle(),
        child: _body,
      ),
    );
  }

  //----------------------------------------------------------------------------

  static ScaffoldState getCurrentScaffoldState(BuildContext context) {
    return Scaffold.of(context);
  }

  static void showSnackBar(
    BuildContext context, {
    required String message,
    SnackBarAction? action,
    Color? backgroundColor,
  }) {
    final snackBar = SnackBar(
      content: Text(message),
      action: action,
      backgroundColor: backgroundColor,
    );

    ScaffoldMessenger.of(context).showSnackBar(snackBar);
  }
}
