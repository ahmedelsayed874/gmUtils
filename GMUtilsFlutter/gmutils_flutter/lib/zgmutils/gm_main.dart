import 'dart:io';

import 'package:firebase_core/firebase_core.dart';
import 'package:flutter/material.dart';
import 'package:flutter_localizations/flutter_localizations.dart';

import 'data_utils/firebase/fcm.dart';
import 'data_utils/storages/app_preferences_storage.dart';
import 'resources/app_colors.dart';
import 'resources/app_measurement.dart';
import 'resources/app_theme.dart';
import 'ui/utils/screen_utils.dart';
import 'utils/logs.dart';
import 'utils/notifications/notifications_manager.dart';

typedef OnInitialize = void Function(BuildContext);

//==============================================================================

class GMMain {
  static void init({
    required AppPreferences? defaultAppPreferences,
    required String Function(BuildContext context)? appName,
    required AppMeasurement Function(BuildContext context) measurements,
    required AppColors Function(BuildContext context, bool isLight) appColors,
    required String? Function()? toolbarTitleFontFamily,
    required String? Function()? defaultFontFamily,
    required FcmRequirements? fcmRequirements,
    required NotificationsConfigurations? localNotificationsConfigurations,
    required OnInitialize? onInitialize,
    required Widget startScreen,
    Map<String, WidgetBuilder>? screensRoutes,
    required CustomWaitViewController? customWaitViewController,
  }) async {
    WidgetsFlutterBinding.ensureInitialized();

    if (localNotificationsConfigurations != null) {
      await NotificationsManager.instance
          .init(localNotificationsConfigurations);
    }

    if (fcmRequirements != null && localNotificationsConfigurations != null) {
      throw 'while you need to init FCM, so you don\'t need to set localNotificationsConfigurations too';
    }

    if (fcmRequirements != null) {
      await Firebase.initializeApp(
        options: fcmRequirements.firebaseOptions,
      );
      await FCM.instance.init(
        fcmRequirements.fcmConfigurations,
      );
      fcmRequirements.onFcmInitialized?.call(FCM.instance);
    }

    if (customWaitViewController != null) {
      ScreenUtils.waitViewController = customWaitViewController;
    }

    AppPreferencesStorage()
        .savedAppPreferences(defaultAppPreferences: defaultAppPreferences)
        .then(
      (value) {
        App._appPreferences = value;
        runApp(App(
          appName: appName,
          measurements: measurements,
          appColors: appColors,
          toolbarTitleFontFamily: toolbarTitleFontFamily,
          defaultFontFamily: defaultFontFamily,
          startScreen: startScreen,
          screensRoutes: screensRoutes,
          onInitialize: onInitialize,
        ));
      },
    );
  }
}

class FcmRequirements {
  FCMConfigurations fcmConfigurations;
  FirebaseOptions? firebaseOptions;
  void Function(FCM fcm)? onFcmInitialized;

  FcmRequirements({
    required this.fcmConfigurations,
    required this.firebaseOptions,
    required this.onFcmInitialized,
  });
}

//==============================================================================

class App extends StatefulWidget {
  String Function(BuildContext context)? appName;
  AppMeasurement Function(BuildContext context)? measurements;
  AppColors Function(BuildContext context, bool isLight)? appColors;
  String? Function()? toolbarTitleFontFamily;
  String? Function()? defaultFontFamily;
  Widget? startScreen;
  Map<String, WidgetBuilder>? screensRoutes;
  OnInitialize? onInitialize;

  App({
    required this.appName,
    required this.measurements,
    required this.appColors,
    required this.toolbarTitleFontFamily,
    required this.defaultFontFamily,
    required Widget this.startScreen,
    required this.screensRoutes,
    required this.onInitialize,
    super.key,
  });

  //============================================================================

  static Map<String, dynamic> globalVariables = {};

  //============================================================================

  //region observer
  static Map<
      String /*category*/,
      Map<
          String /*name*/,
          ObserverDelegate /*observer(name, args)*/
          >>? _observers;

  static void addObserver({
    required String category,
    required String observerName,
    required ObserverDelegate observer,
  }) {
    _observers ??= {};
    _observers![category] ??= {};
    _observers![category]![observerName] = observer;
  }

  static void removeObserver({
    required String category,
    String? observerName,
  }) {
    if (observerName == null) {
      _observers?.remove(category);
    }
    //
    else {
      _observers?[category]?.remove(observerName);
      if (_observers?[category]?.isEmpty == true) _observers?.remove(category);
    }

    if (_observers?.isEmpty == true) _observers = null;
  }

  static void callObservers({required String category, dynamic args}) {
    _observers?[category]?.forEach((key, value) {
      value.call(key, args);
    });
  }

  static void clearObservers() {
    _observers?.forEach((key, value) => value.clear());
    _observers?.clear();
  }

  static bool isObserverCategoryExist(String category) {
    return _observers?.containsKey(category) == true;
  }

  static bool isObserverNameExist({
    required String category,
    required String observerName,
  }) {
    if (_observers?.containsKey(category) == true) {
      return _observers?[category]?.containsKey(observerName) == true;
    }

    return false;
  }

  //endregion

  //============================================================================

  //region navigation
  static BuildContext? _context;

  static BuildContext get context => _context!;

  static Future<RT?> navTo<RT>(
    Widget screen, {
    BuildContext? context,
    Object? args,
    bool singleTop = false,
    bool animate = false,
  }) async {
    RouteSettings? settings = RouteSettings(name: null, arguments: args);

    /*final route = MaterialPageRoute<RT>(builder: (context) => screen, settings: settings);*/

    pageBuilder(context, animation, secondaryAnimation) => screen;

    final route = animate
        ? PageRouteBuilder<RT>(
            transitionDuration: const Duration(milliseconds: 500),
            transitionsBuilder: (
              context,
              animation,
              secondaryAnimation,
              child,
            ) {
              const begin = Offset(1.0, 0.0);
              const end = Offset.zero;
              final tween = Tween(begin: begin, end: end)
                  .chain(CurveTween(curve: Curves.ease));
              return SlideTransition(
                position: animation.drive(tween),
                child: child,
              );
            },

            //
            pageBuilder: pageBuilder,
            settings: settings,
          )
        : PageRouteBuilder<RT>(
            pageBuilder: pageBuilder,
            settings: settings,
          );

    final nav = Navigator.of(context ?? App.context);

    RT? result;
    if (singleTop) {
      result = await nav.pushAndRemoveUntil(route, (route) => false);
    }
    //
    else {
      result = await nav.push(route);
    }

    return result;
  }

  static T? navArgs<T>(BuildContext context) {
    return ModalRoute.of(context)?.settings.arguments as T?;
  }

  static void navBack<T extends Object?>([T? result]) {
    Navigator.of(context).pop(result);
  }

  //endregion

  //============================================================================

  static AppPreferences _appPreferences = AppPreferences(
    isEn: null,
    isLightMode: null,
  );

  static bool get isEnglish => _appPreferences.isEn ?? true;

  static bool get isLightTheme => _appPreferences.isLightMode ?? true;

  static bool changeAppLanguage({
    required BuildContext context,
    required bool toEnglish,
  }) {
    Logs.print(() =>
        '[GMMain] App.changeAppLanguage(toEnglish: $toEnglish) ... current is en? ${_appPreferences.isEn}');

    if (_appPreferences.isEn != null && _appPreferences.isEn == toEnglish) {
      return false;
    }

    _appPreferences.isEn = toEnglish;
    AppPreferencesStorage().setLanguage(toEnglish);

    var s = context.findAncestorStateOfType<_AppState>();
    s?.invalidate();
    return s != null;
  }

  static bool changeAppAppearance({
    required BuildContext context,
    required bool? toLight,
  }) {
    Logs.print(() =>
        '[GMMain] App.changeAppAppearance(toLight: $toLight) ... current is light? ${_appPreferences.isLightMode}');

    if (_appPreferences.isLightMode != null &&
        _appPreferences.isLightMode == toLight) {
      return false;
    }

    _appPreferences.isLightMode = toLight;
    AppPreferencesStorage().setAppearance(toLight);

    var s = context.findAncestorStateOfType<_AppState>();
    s?.invalidate();
    return s != null;
  }

  //============================================================================

  @override
  State<App> createState() => _AppState();
}

class _AppState extends State<App> {
  void invalidate() => setState(() {});

  @override
  Widget build(BuildContext context) {
    Logs.print(() => '[GMMain] _AppState.build()');

    if (App._context?.widget == null) {
      App._context = context;
    }

    App._appPreferences.isEn ??=
        (!Platform.localeName.toLowerCase().startsWith('ar'));

    App._appPreferences.isLightMode ??=
        MediaQuery.platformBrightnessOf(context) == Brightness.light;

    //------------------------------------------------------------------------

    final lightColors = widget.appColors?.call(context, true) ??
        AppColors.def(isLightMode: true);
    final darkColors = widget.appColors?.call(context, false) ??
        AppColors.def(isLightMode: false);
    widget.appColors?.call(context, App.isLightTheme);

    theme(AppColors colors) => ThemeData(
          primarySwatch: colors.primarySwatch,
          scaffoldBackgroundColor: colors.background,
          //bottomAppBarTheme: BottomAppBarTheme(color: colors.toolbar),
          bottomAppBarTheme: BottomAppBarThemeData(color: colors.toolbar),
          cardTheme: CardTheme.of(context).copyWith(
            color: colors.card,
            surfaceTintColor: colors.card,
          ),
          hintColor: colors.hint,
          // iconTheme: colors.isLightMode ? null : IconThemeData(color: colors.black,),
          inputDecorationTheme: InputDecorationTheme(
            hintStyle: AppTheme.defaultTextStyle(
              textColor: colors.hint,
              fontWeight: FontWeight.normal,
            ),
          ),
          elevatedButtonTheme: ElevatedButtonThemeData(
            style: ButtonStyle(
              backgroundColor: WidgetStatePropertyAll(colors.primary),
              textStyle: WidgetStatePropertyAll(AppTheme.defaultTextStyle(
                textColor: colors.primaryVariant,
                fontWeight: FontWeight.w600,
              )),
              foregroundColor: WidgetStatePropertyAll(colors.primaryVariant),
              shape: WidgetStatePropertyAll(
                RoundedRectangleBorder(
                  borderRadius: BorderRadius.circular(5),
                ),
              ),
            ),
          ),
          textButtonTheme: TextButtonThemeData(
            style: ButtonStyle(
              shape: WidgetStatePropertyAll(
                RoundedRectangleBorder(
                  borderRadius: BorderRadius.circular(5),
                ),
              ),
            ),
          ),
          colorScheme: colors.isLightMode
              ? ColorScheme.light(
                  primary: colors.primary,
                  secondary: colors.primary,
                  background: colors.background,
                  surface: colors.background,
                )
              : ColorScheme.dark(
                  primary: colors.primary,
                  secondary: colors.primary,
                  background: colors.background,
                  surface: colors.background,
                ),
          brightness: colors.isLightMode ? Brightness.light : Brightness.dark,
          bottomSheetTheme: BottomSheetThemeData(
            backgroundColor: colors.background,
            surfaceTintColor: colors.background,
          ),
        );

    return MaterialApp(
      title: widget.appName?.call(context) ?? '',
      //
      theme: theme(lightColors),
      darkTheme: theme(darkColors),
      themeMode: App._appPreferences.isLightMode == null
          ? ThemeMode.system
          : (App._appPreferences.isLightMode!
              ? ThemeMode.light
              : ThemeMode.dark),
      //
      localizationsDelegates: const [
        GlobalMaterialLocalizations.delegate,
        GlobalWidgetsLocalizations.delegate,
        GlobalCupertinoLocalizations.delegate,
      ],
      supportedLocales: const [
        Locale('en', ''), // English, no country code
        Locale('ar', ''), // Arabic, no country code
      ],
      locale: App._appPreferences.isEn == null
          ? null
          : (App._appPreferences.isEn!
              ? const Locale('en', '')
              : const Locale('ar', '')),
      //
      home: StarterWidget(
        startScreen: widget.startScreen!,
        measurements: widget.measurements,
        appColors: widget.appColors,
        toolbarTitleFontFamily: widget.toolbarTitleFontFamily,
        defaultFontFamily: widget.defaultFontFamily,
        onInitialize: widget.onInitialize,
      ),
      routes: widget.screensRoutes ?? const <String, WidgetBuilder>{},
      initialRoute: null,
      navigatorObservers: [_NavigatorObserver()],
      debugShowCheckedModeBanner: false,
    );
  }

  @override
  void dispose() {
    Logs.print(() => '[GMMain] _AppState.dispose() ---> ${App._context}');

    widget.appName = null;
    widget.startScreen = null;
    widget.measurements = null;
    widget.appColors = null;
    widget.toolbarTitleFontFamily = null;
    widget.defaultFontFamily = null;
    widget.onInitialize = null;

    App.clearObservers();
    App._context = null;
    App.globalVariables.clear();

    super.dispose();
  }
}

class StarterWidget extends StatefulWidget {
  Widget? startScreen;
  AppMeasurement Function(BuildContext context)? measurements;
  AppColors Function(BuildContext context, bool isLight)? appColors;
  String? Function()? toolbarTitleFontFamily;
  String? Function()? defaultFontFamily;
  OnInitialize? onInitialize;

  StarterWidget({
    required Widget this.startScreen,
    required this.measurements,
    required this.appColors,
    required this.toolbarTitleFontFamily,
    required this.defaultFontFamily,
    required this.onInitialize,
    super.key,
  }) : super();

  @override
  State<StarterWidget> createState() => _StarterWidgetState();
}

class _StarterWidgetState extends State<StarterWidget> {
  @override
  Widget build(BuildContext context) {
    Logs.print(() => '[GMMain] _StarterWidgetState.build() '
        '---> startScreen: ${widget.startScreen}');

    if (App._context?.widget == null) {
      App._context = context;
    }

    var appMeasurement =
        widget.measurements?.call(context) ?? AppMeasurement.def();

    var isLight = App.isLightTheme;
    var appColors = widget.appColors?.call(context, isLight) ??
        AppColors.def(isLightMode: isLight);

    //init app theme
    AppTheme(
      appColors: appColors,
      appMeasurement: appMeasurement,
      toolbarTitleFontFamily: widget.toolbarTitleFontFamily?.call(),
      defaultFontFamily: widget.defaultFontFamily?.call(),
    );

    widget.onInitialize?.call(context);

    return widget.startScreen!;
  }

  @override
  void dispose() {
    super.dispose();
    widget.startScreen = null;
    widget.measurements = null;
    widget.appColors = null;
    widget.toolbarTitleFontFamily = null;
    widget.defaultFontFamily = null;
    widget.onInitialize = null;
  }
}

//==============================================================================

class _NavigatorObserver extends NavigatorObserver {
  @override
  void didPush(Route<dynamic> route, Route<dynamic>? previousRoute) {
    App._context = route.navigator?.context;
    Logs.print(() => '[GMMain] _NavigatorObserver.didPush() '
        '---> ${App._context?.widget} (MOUNTED: ${App._context?.mounted})');
  }

  @override
  void didPop(Route<dynamic> route, Route<dynamic>? previousRoute) {
    App._context = route.navigator?.context;
    Logs.print(() => '[GMMain] _NavigatorObserver.didPop() '
        '---> ${App._context?.widget} (MOUNTED: ${App._context?.mounted})');
  }
}

//==============================================================================

typedef ObserverDelegate = void Function(String observerName, dynamic /*args*/);
