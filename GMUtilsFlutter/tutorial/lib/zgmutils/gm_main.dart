import 'package:firebase_core/firebase_core.dart';
import 'package:flutter/material.dart';
import 'package:flutter_localizations/flutter_localizations.dart';

import '../resources/_resources.dart';
import 'data_utils/firebase/fcm.dart';
import 'data_utils/storages/locale_preference.dart';
import 'resources/app_colors.dart';
import 'resources/app_measurement.dart';
import 'resources/app_theme.dart';
import 'utils/notifications.dart';

typedef OnInitialize = void Function(BuildContext);

//==============================================================================

class GMMain {
  static void init({
    required bool isEnglishDefaultLocale,
    required String Function(BuildContext context)? appName,
    required AppMeasurement Function(BuildContext context) measurements,
    required AppColors Function(BuildContext context) appColors,
    required String? Function()? toolbarTitleFontFamily,
    required String? Function()? defaultFontFamily,
    required Widget startScreen,
    required FcmRequirements? fcmRequirements,
    required NotificationsConfigurations? localNotificationsConfigurations,
    required OnInitialize? onInitialize,
  }) async {
    WidgetsFlutterBinding.ensureInitialized();

    if (localNotificationsConfigurations != null) {
      await Notifications.instance.init(localNotificationsConfigurations);
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

    LocalePreference().isEn().then(
      (value) {
        App._isEnglish = value ?? isEnglishDefaultLocale;
        runApp(App(
          appName: appName,
          measurements: measurements,
          appColors: appColors,
          toolbarTitleFontFamily: toolbarTitleFontFamily,
          defaultFontFamily: defaultFontFamily,
          startScreen: startScreen,
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
  AppColors Function(BuildContext context)? appColors;
  String? Function()? toolbarTitleFontFamily;
  String? Function()? defaultFontFamily;
  Widget? startScreen;
  OnInitialize? onInitialize;

  App({
    required this.appName,
    required this.measurements,
    required this.appColors,
    required this.toolbarTitleFontFamily,
    required this.defaultFontFamily,
    required Widget startScreen,
    required this.onInitialize,
    Key? key,
  }) : super(key: key) {
    this.startScreen = startScreen;
  }

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

  static void removeObserver({required String category, String? name}) {
    if (name == null) {
      _observers?.remove(category);
    } else {
      _observers?[category]?.remove(name);
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

  //endregion

  //============================================================================

  //region navigation
  static BuildContext? _context;

  static BuildContext get context => _context!;

  static Future<RT?> navTo<RT>(
    Widget screen, {
    Object? args,
    bool singleTop = false,
  }) async {
    final route = MaterialPageRoute<RT>(
      builder: (context) => screen,
      settings: RouteSettings(
        name: null,
        arguments: args,
      ),
    );

    final nav = Navigator.of(context);
    RT? result;
    if (singleTop) {
      result = await nav.pushAndRemoveUntil(route, (route) => false);
    } else {
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

  static bool _isEnglish = true;

  static bool get isEnglish => _isEnglish;

  static bool changeAppLanguage({
    required BuildContext context,
    required bool toEnglish,
  }) {
    if (_isEnglish == toEnglish) return false;

    LocalePreference().setLocale(toEnglish);
    var s = context.findAncestorStateOfType<_AppState>();
    s?.invalidate(toEnglish);
    return s != null;
  }

  //============================================================================

  @override
  State<App> createState() => _AppState();
}

class _AppState extends State<App> {
  void invalidate(bool toEnglish) => setState(() {
        App._isEnglish = toEnglish;
      });

  @override
  Widget build(BuildContext context) {
    final colors = widget.appColors!(context);

    return MaterialApp(
      title: widget.appName?.call(context) ?? '',
      theme: ThemeData(
        primarySwatch: colors.primarySwatch,
        scaffoldBackgroundColor: colors.background,
        bottomAppBarTheme: BottomAppBarTheme(color: colors.toolbar),
        cardTheme: CardTheme(
          color: colors.card,
          surfaceTintColor: colors.card,
        ),
        hintColor: colors.hint,
        elevatedButtonTheme: ElevatedButtonThemeData(
          style: ButtonStyle(
            backgroundColor: MaterialStatePropertyAll(colors.primary),
            textStyle: MaterialStatePropertyAll(Res.themes.defaultTextStyle(
              textColor: colors.textOnPrimary,
              fontWeight: FontWeight.w600,
            )),
            foregroundColor: MaterialStatePropertyAll(colors.textOnPrimary),
            shape: MaterialStatePropertyAll(
              RoundedRectangleBorder(
                borderRadius: BorderRadius.circular(5),
              ),
            ),
          ),
        ),
        textButtonTheme: TextButtonThemeData(
          style: ButtonStyle(
            shape: MaterialStatePropertyAll(
              RoundedRectangleBorder(
                borderRadius: BorderRadius.circular(5),
              ),
            ),
          ),
        ),
        colorScheme: ColorScheme.light(
            primary: colors.primary,
            secondary: colors.primary,
            background: colors.background),
        //backgroundColor: colors.background,
        brightness: colors.isLightMode ? Brightness.light : Brightness.dark,
        bottomSheetTheme: BottomSheetThemeData(
          backgroundColor: colors.background,
          surfaceTintColor: colors.background,
        ),
      ),
      localizationsDelegates: const [
        GlobalMaterialLocalizations.delegate,
        GlobalWidgetsLocalizations.delegate,
        GlobalCupertinoLocalizations.delegate,
      ],
      supportedLocales: const [
        Locale('en', ''), // English, no country code
        Locale('ar', ''), // Arabic, no country code
      ],
      locale: App.isEnglish ? const Locale('en', '') : const Locale('ar', ''),
      home: StarterWidget(
        startScreen: widget.startScreen!,
        measurements: widget.measurements,
        appColors: widget.appColors,
        toolbarTitleFontFamily: widget.toolbarTitleFontFamily,
        defaultFontFamily: widget.defaultFontFamily,
        onInitialize: widget.onInitialize,
      ),
      navigatorObservers: [_NavigatorObserver()],
      debugShowCheckedModeBanner: false,
    );
  }

  @override
  void dispose() {
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
  AppColors Function(BuildContext context)? appColors;
  String? Function()? toolbarTitleFontFamily;
  String? Function()? defaultFontFamily;
  OnInitialize? onInitialize;

  StarterWidget({
    required this.startScreen,
    required this.measurements,
    required this.appColors,
    required this.toolbarTitleFontFamily,
    required this.defaultFontFamily,
    required this.onInitialize,
    Key? key,
  }) : super(key: key);

  @override
  State<StarterWidget> createState() => _StarterWidgetState();
}

class _StarterWidgetState extends State<StarterWidget> {
  @override
  Widget build(BuildContext context) {
    App._context = context;

    AppMeasurement Function(BuildContext context)? measurements =
        widget.measurements;
    AppColors Function(BuildContext context)? appColors = widget.appColors;

    AppTheme(
      appColors: appColors!(context),
      appMeasurement: measurements!(context),
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
  }

  @override
  void didPop(Route<dynamic> route, Route<dynamic>? previousRoute) {
    App._context = route.navigator?.context;
  }
}

//==============================================================================

typedef ObserverDelegate = Function(String observerName, dynamic /*args*/);
