import 'dart:async';
import 'dart:convert';
import 'dart:io';

import 'package:gmutils_flutter/data/models/users/user_account.dart';
import 'package:gmutils_flutter/main.dart' as main;
import 'package:gmutils_flutter/zgmutils/data_utils/firebase/firebase_configs.dart';
import 'package:gmutils_flutter/zgmutils/data_utils/storages/general_storage.dart';
import 'package:gmutils_flutter/zgmutils/utils/app_version_check.dart';
import 'package:gmutils_flutter/zgmutils/utils/date_op.dart';
import 'package:gmutils_flutter/zgmutils/utils/logs.dart';
import 'package:gmutils_flutter/zgmutils/utils/mappable.dart';

class AppConfigs {
  String get currentAppVersion => main.appVersion;

  String get iosAppStoreId => main.iosAppStoreId;

  late final IStorage _storage;

  AppConfigs({IStorage? storage}) {
    _storage = storage ?? GeneralStorage.o('app_configs_cached');
  }

  IFirebaseConfigs get firebaseConfigs {
    if (main.useProductionData) {
      return FirebaseConfigs();
    }
    //
    else {
      var a = _AppConfigsDataHandler();
      a.appConfigsData = AppConfigsData(
        latestAndroidAppVersion: '0.0.0',
        latestIosAppVersion: '0.0.0',
        previousAndroidAppVersionExpiryDate: '',
        previousIosAppVersionExpiryDate: '',
        serverUrl: 'https://ahmedelsayed.abdo/',
        specialServerUrl: '',
        logFileDeadline: '2025-11-11 11:11:11',
        appFeatures: """
          {
              "allowed": [
                    "${AppConfigsData.appFeatureMails}",
                    "${AppConfigsData.appFeatureChats}",
                    "${AppConfigsData.appFeatureLessons}",
                    "${AppConfigsData.appFeatureVirtualClasses}",
                    "${AppConfigsData.appFeatureQuestionBank}",
                    "${AppConfigsData.appFeatureTeacherStudents}",
                    "${AppConfigsData.appFeatureHomeworkExams}",
                    "${AppConfigsData.appFeatureVirtualMeetings}",
                    "${AppConfigsData.appFeatureChildren}",
                    "${AppConfigsData.appFeatureSchoolStuff}",
                    "${AppConfigsData.appFeatureSchoolStudents}",
                    "${AppConfigsData.appFeatureSchoolParents}",
                    "${AppConfigsData.appFeatureSubjectRanks}",
                    "${AppConfigsData.appFeatureTestRanks}"
              ],
              "disallowed": []
          }
          """,
        allowStudentToChat: false,
        chatSuggestedMessages: '["please contact me"]',
        developers: '["testmanager"]',
        messages: '',
      );

      return FirebaseConfigsMock(a.toMap());
    }
  }

  //----------------------------------------------------------------------------

  static AppConfigsData? _appConfigsData;

  static AppConfigsData get appConfigsData =>
      _appConfigsData ??
      AppConfigsData(
        latestAndroidAppVersion: '0.0.0',
        latestIosAppVersion: '0.0.0',
        previousAndroidAppVersionExpiryDate: '',
        previousIosAppVersionExpiryDate: '',
        serverUrl: 'https://bls-edu.com/',
        specialServerUrl: '',
        logFileDeadline: '2222-11-01 00:00:00',
        appFeatures: '',
        allowStudentToChat: false,
        chatSuggestedMessages: '',
        developers: '[]',
        messages: '',
      );

  AppConfigsData get appConfigs => appConfigsData;

  Future<void> fetch() async {
    _AppConfigsDataHandler handler = _AppConfigsDataHandler();
    var b = await firebaseConfigs.fetch(handlers: [handler]);

    if (handler.appConfigsData != null) {
      _appConfigsData = handler.appConfigsData;
      _cacheAppConfigsData(handler);
    }

    Logs.print(
      () => 'AppConfigs.fetch -> appConfigsData: $_appConfigsData',
    );
  }

  void _cacheAppConfigsData(_AppConfigsDataHandler handler) async {
    Logs.print(
        () => 'AppConfigs._cacheAppConfigsData ===> ${handler.appConfigsData}');

    var map = handler.toMap();
    var json = jsonEncode(map);
    await _storage.save('data', json);
  }

  Future<AppConfigsData?> get cachedAppConfigsData async {
    var json = await _storage.retrieve('data');
    if (json == null || json.isEmpty) {
      Logs.print(() => 'AppConfigs.cachedAppConfigsData ===> no-cached-data');
      return null;
    }

    try {
      var map = jsonDecode(json);
      var h = _AppConfigsDataHandler();
      h.handle(Map.from(map));
      var configs = h.appConfigsData;
      Logs.print(() => 'AppConfigs.cachedAppConfigsData ===> $configs');
      _appConfigsData ??= configs;
      return configs;
    } catch (e) {
      Logs.print(() => 'AppConfigs.cachedAppConfigsData ===> EXCEPTION: $e');
      return null;
    }
  }

  //--------------------------------------------------------------------------

  Stream<String> getServerUrl({required String? username}) async* {
    var cachedData = await cachedAppConfigsData;
    if (cachedData != null) {
      yield cachedData.getServerUrl(username: username);
    }

    await fetch();

    yield appConfigsData.getServerUrl(username: username);
  }

  //--------------------------------------------------------------------------

  bool needUpdateApp() {
    /*if (appConfigsData == null) {
      Logs.print(
        () => 'AppConfigs.needUpdate() -> must use get() method first',
      );
      return false;
    }*/

    var checker = _getAppVersionCheckInstance();
    var b = checker.hasNewVersion(
      publishedAndroidVersion: appConfigsData.latestAndroidAppVersion,
      publishedIosVersion: appConfigsData.latestIosAppVersion,
    );
    Logs.print(() => 'AppConfigs -> needUpdate() -> $b');
    return b == true;
  }

  bool mustUpdateApp() {
    /*if (appConfigsData == null) {
      Logs.print(
        () => 'AppConfigs.needUpdate() -> must use get() method first',
      );
      return false;
    }*/

    String expireDate = '';
    if (Platform.isIOS) {
      expireDate = appConfigsData.previousIosAppVersionExpiryDate;
    }
    //
    else if (Platform.isAndroid) {
      expireDate = appConfigsData.previousAndroidAppVersionExpiryDate;
    }
    //
    else {
      return false;
    }

    var date = DateOp().parse(expireDate, convertToLocalTime: false);
    if (date == null) {
      Logs.print(
        () => 'AppConfigs -> forceUpdateNow()::\n'
            'expireDate: $expireDate ($date) ..... return false',
      );
      return false;
    }

    var timeNow = DateTime.now();
    var b = timeNow.millisecondsSinceEpoch >= date.millisecondsSinceEpoch;
    Logs.print(
      () => 'AppConfigs -> forceUpdateNow()::\n'
          '$timeNow >= $date ? $b',
    );
    return b;
  }

  void updateApp() {
    /*if (appConfigsData == null) {
      Logs.print(
        () => 'AppConfigs.needUpdate() -> must use get() method first',
      );
    }*/

    var checker = _getAppVersionCheckInstance();

    if (Platform.isIOS) {
      checker.openAppleStore();
    }
    //
    else if (Platform.isAndroid) {
      checker.openPlayStore();
    }
  }

  AppVersionCheck _getAppVersionCheckInstance() {
    return AppVersionCheck(
      runningAndroidVersion: currentAppVersion,
      runningIosVersion: currentAppVersion,
      iosAppId: iosAppStoreId,
    );
  }
}

class _AppConfigsDataHandler extends FirebaseConfigsHandler {
  AppConfigsData? appConfigsData;

  @override
  void handle(Map<String, String> configsMap) {
    appConfigsData = AppConfigsData(
      latestAndroidAppVersion: configsMap['appVersion_android_latest'] ?? '',
      previousAndroidAppVersionExpiryDate:
          configsMap['appVersion_android_previousExpiryDate'] ?? '',
      //
      latestIosAppVersion: configsMap['appVersion_ios_latest'] ?? '',
      previousIosAppVersionExpiryDate:
          configsMap['appVersion_ios_previousExpiryDate'] ?? '',
      //
      serverUrl: configsMap['serverUrl'] ?? '',
      //
      specialServerUrl: configsMap['specialServerUrl'] ?? '',
      //
      logFileDeadline: configsMap['zLogFileDeadline'] ?? '',
      //
      appFeatures: configsMap['appFeatures'] ?? '',
      //
      allowStudentToChat:
          (configsMap['allowStudentToChat'] ?? '').toLowerCase() == 'true',
      chatSuggestedMessages: configsMap['chatSuggestedMessages'] ?? '',
      //
      developers: configsMap['developers'] ?? '[]',
      messages: configsMap['messages'] ?? '[]',
    );
  }

  Map<String, String> toMap() {
    if (appConfigsData == null) return {};
    var appConfigs = appConfigsData!;

    return {
      'appVersion_android_latest': appConfigs.latestAndroidAppVersion,
      'appVersion_android_previousExpiryDate':
          appConfigs.previousAndroidAppVersionExpiryDate,
      //
      'appVersion_ios_latest': appConfigs.latestIosAppVersion,
      'appVersion_ios_previousExpiryDate':
          appConfigs.previousIosAppVersionExpiryDate,
      //
      'serverUrl': appConfigs._serverUrl,
      //
      'specialServerUrl': appConfigs._specialServerUrl,
      //
      'zLogFileDeadline': appConfigs._logFileDeadline,
      //
      'appFeatures': appConfigs._appFeatures,
      //
      'allowStudentToChat':
          appConfigs.allowStudentToChat.toString().toLowerCase(),
      'chatSuggestedMessages': appConfigs._chatSuggestedMessages,
    };
  }
}

class AppConfigsData {
  static const String appFeatureMails = 'mails';
  static const String appFeatureChats = 'chats';
  static const String appFeatureLessons = 'lessons';
  static const String appFeatureVirtualClasses = 'virtual_classes';
  static const String appFeatureQuestionBank = 'question_bank';
  static const String appFeatureTeacherStudents = 'teacher_students';
  static const String appFeatureHomeworkExams = 'homework_exams';
  static const String appFeatureVirtualMeetings = 'virtual_meetings';
  static const String appFeatureChildren = 'children';
  static const String appFeatureSchoolStuff = 'school_stuff';
  static const String appFeatureSchoolStudents = 'school_students';
  static const String appFeatureSchoolParents = 'school_parents';
  static const String appFeatureSubjectRanks = 'subject_ranks';
  static const String appFeatureTestRanks = 'test_ranks';
  static const String appFeatureStatistics = 'statistics';

  final String latestAndroidAppVersion;
  final String latestIosAppVersion;
  final String previousAndroidAppVersionExpiryDate;
  final String previousIosAppVersionExpiryDate;

  //
  final String _serverUrl;

  /*
  e.g.: {
           "username": {
                 "url":"http://domain.com/",
                 "expireOn": "yyyy-MM-dd HH-mm:dd"
           }
        }
   */
  final String _specialServerUrl;

  /*
  e.g.: {
           "username": {
                 "expireOn": "yyyy-MM-dd HH-mm:dd"
           }
        }
   */
  final String _logFileDeadline;

  /*
  e.g: {
          "allowed": [
                    "lessons",
                    "virtual_classes",
                    "question_bank",
                    "students_for_teacher",
                    "homework_exams",
                    "virtual_meetings"
          ],
          "disallowed": []
       }
   */
  final String _appFeatures;

  //
  final bool allowStudentToChat;

  //json array
  final String _chatSuggestedMessages;

  /*
  ["username"]
   */
  final String developers;

  /*
  [
     {
        "message": "....",
        "expireOn": "yyyy-MM-dd HH:mm:ss",
        "targetPlatform": null, //or:- Android, iOS
        "targetUsers": ["username1", "username2"], //or:- null
        "action": "http......", //or:- null
        "canDismiss": true //or:- false
     }
  ]
   */
  final String messages;

  //---------------------------------------------------------------------------

  AppConfigsData({
    required this.latestAndroidAppVersion,
    required this.latestIosAppVersion,
    required this.previousAndroidAppVersionExpiryDate,
    required this.previousIosAppVersionExpiryDate,
    //
    required String serverUrl,
    //
    required String specialServerUrl,
    //
    required String logFileDeadline,
    //
    required String appFeatures,
    //
    required this.allowStudentToChat,
    required String chatSuggestedMessages,
    required this.developers,
    required this.messages,
  })  : _serverUrl = serverUrl,
        _specialServerUrl = specialServerUrl,
        _logFileDeadline = logFileDeadline,
        _appFeatures = appFeatures,
        _chatSuggestedMessages = chatSuggestedMessages;

  @override
  String toString() {
    return 'AppConfigsData{'
        'latestAndroidAppVersion: $latestAndroidAppVersion, '
        'latestIosAppVersion: $latestIosAppVersion, '
        'previousAndroidAppVersionExpiryDate: $previousAndroidAppVersionExpiryDate, '
        'previousIosAppVersionExpiryDate: $previousIosAppVersionExpiryDate, '
        ''
        '_serverUrl: $_serverUrl, '
        '_specialServerUrl: $_specialServerUrl, '
        ''
        '_logFileDeadline: $_logFileDeadline, '
        ''
        '_appFeatures: $_appFeatures, '
        ''
        'allowStudentToChat: $allowStudentToChat, '
        'chatSuggestedMessages: $_chatSuggestedMessages, '
        'developers: $developers, '
        'messages: $messages'
        '}';
  }

  //---------------------------------------------------------------------------

  String getServerUrl({required String? username}) {
    String? url;
    String? expire;

    if (_specialServerUrl.isNotEmpty && username?.isNotEmpty == true) {
      try {
        /*
        specialServerUrl:
        {
          "sw1":  {    "url": "https://beyti.mnc-control.com/",    "expireOn":"2024-05-16 17:00:00"  },
          "9606": {    "url": "https://beyti.mnc-control.com/",    "expireOn":"2024-05-16 17:00:00"  }
        }
         */
        var jsonAsMap = jsonDecode(_specialServerUrl);
        var usernameMap = jsonAsMap[username];
        url = usernameMap['url'];
        expire = usernameMap['expireOn'];
        if (_isUrlExpired(expire ?? '')) {
          url = null;
          expire = null;
        }
      } catch (e) {
        Logs.print(() =>
            'AppConfigs.getServerUrlIfExist(username: $username) ---> Exception: $e');
      }
    }

    if (url == null || url.isEmpty) {
      url = _serverUrl;
      expire = '9999-09-09 00:00:00';
    }

    if (url.isNotEmpty) {
      if (_isUrlExpired(expire ?? '')) {
        return '';
      } else {
        return url;
      }
    } else {
      return '';
    }
  }

  bool _isUrlExpired(String date) {
    if (date.isEmpty) return true;
    var d = DateTime.tryParse(date);
    if (d == null) return true;
    return d.millisecondsSinceEpoch < DateTime.now().millisecondsSinceEpoch;
  }

  //----------------------------------------------------------

  bool hasAppFeature(
    String featureName, {
    required String authAccountType,
  }) {
    bool? allowed;

    if (_appFeatures.isNotEmpty) {
      try {
        /*
              { "allowed": [], "disallowed": [] }
         */
        var jsonAsMap = jsonDecode(_appFeatures);

        var allowedFeatures = (jsonAsMap['allowed'] as List?)?.toSet();
        if (allowedFeatures?.contains(featureName) == true) {
          allowed = true;
        }

        var disallowedFeatures = (jsonAsMap['disallowed'] as List?)?.toSet();
        if (disallowedFeatures?.contains(featureName) == true) {
          allowed = false;
        }
      } catch (e) {
        Logs.print(() =>
            'AppConfigs.hasAppFeatures(featureName: $featureName) ---> Exception: $e');
      }
    }

    if (allowed == null) {
      if (featureName == appFeatureMails) {
        allowed = true;
      }
      //
      else if (featureName == appFeatureChats) {
        allowed = true;
      }
      //
      else {
        if (UserAccount.accountTypeManager.toLowerCase() ==
            authAccountType.toLowerCase()) {
          if (featureName == AppConfigsData.appFeatureSchoolStuff) {
            allowed = true;
          }
          //
          else if (featureName == AppConfigsData.appFeatureSchoolStudents) {
            allowed = true;
          }
          //
          else if (featureName == AppConfigsData.appFeatureSchoolParents) {
            allowed = true;
          }
          //
          else {
            allowed = false;
          }
        }
        //
        else {
          allowed = false;
        }
      }
    }

    if (featureName == appFeatureMails) {
      var blockedTypes = [
        //'',
        UserAccount.accountTypeStudent.toLowerCase(),
        UserAccount.accountTypeParent.toLowerCase(),
      ];
      if (blockedTypes.contains(authAccountType.toLowerCase())) {
        allowed = false;
      }
    }

    Logs.print(() => 'AppConfigsData.hasAppFeature('
        'featureName: $featureName, '
        'authAccountType: $authAccountType'
        ') ---> allowed=$allowed');

    return allowed;
  }

  //----------------------------------------------------------

  List<String> getChatSuggestedMessages() {
    if (_chatSuggestedMessages.isNotEmpty) {
      try {
        var json = jsonDecode(_chatSuggestedMessages);
        return List.from(json);
      } catch (e) {
        Logs.print(
            () => 'AppConfigs.getChatSuggestedMessages() ---> Exception: $e');
      }
    }

    return [];
  }

  //----------------------------------------------------------

  bool isDeveloper(String username) {
    try {
      var list = jsonDecode(developers);
      var set = Set.from(list);
      return set.contains(username.toLowerCase());
    } catch (e) {
      Logs.print(() =>
          'AppConfigs.isDeveloper(username: $username) --> EXCEPTION:: $e');
      return false;
    }
  }

  //----------------------------------------------------------

  List<Message> getMessages(String username) {
    try {
      var list = jsonDecode(this.messages);
      var messages = MessageMapper().fromMapList(list);
      List<Message> msgs = [];
      messages?.forEach((m) {
        bool add = false;

        var d = DateOp().parse(m.expireOn);
        if ((d?.millisecondsSinceEpoch ?? 0) >
            DateTime.now().millisecondsSinceEpoch) {
          add = true;

          if (m.targetPlatform != null) {
            if (Platform.isAndroid && m.targetPlatform == 'Android') {
              add = true;
            }
            //
            else if (Platform.isIOS && m.targetPlatform == 'iOS') {
              add = true;
            }
            //
            else {
              add = false;
            }
          }

          if (m.targetUsers != null) {
            var i = m.targetUsers!
                .indexWhere((e) => e.toLowerCase() == username.toLowerCase());
            if (i >= 0) {
              add = true;
            }
            //
            else {
              add = false;
            }
          }
        }

        if (add) msgs.add(m);
      });
      return msgs;
    } catch (e) {
      Logs.print(() =>
          'AppConfigs.getMessages(username: $username) --> EXCEPTION:: $e');
      return [];
    }
  }

  //----------------------------------------------------------

  String? getLogFileDeadline({required String username}) {
    String? expire;

    if (_logFileDeadline.isNotEmpty) {
      try {
        /*
        logFileDeadline:
        {
          "sw1":  {  "expireOn":"2024-05-16 17:00:00"  },
          "9606": {  "expireOn":"2024-05-16 17:00:00"  }
        }
         */
        var jsonAsMap = jsonDecode(_logFileDeadline);
        var usernameMap = jsonAsMap[username];
        expire = usernameMap['expireOn'];
      } catch (e) {
        Logs.print(() =>
            'AppConfigs.getLogFileDeadline(username: $username) ---> Exception: $e');
      }
    }

    return expire;
  }
}

//-------------------------------------------------------------------------

class FirebaseConfigsMock extends IFirebaseConfigs {
  final Map<String, String> data;

  FirebaseConfigsMock(this.data);

  @override
  Future<bool> fetch({required List<FirebaseConfigsHandler> handlers}) async {
    await Future.delayed(const Duration(seconds: 10));
    for (var a in handlers) {
      a.handle(data);
    }
    return true;
  }

  @override
  Future<DateTime?> get lastFetchTime async => null;

  @override
  Future<double> get remainMinuteTillNextFetch async => 0;
}

///////////////////////////////////////////////////////////////////////////////

class Message {
  String message;
  String expireOn; //yyyy-MM-dd HH:mm:ss
  String? targetPlatform; // null, Android, iOS
  List<String>? targetUsers; //["username1", "username2"], //or:- null
  String? action; //http......", //or:- null
  bool canDismiss;

  Message({
    required this.message,
    required this.expireOn,
    required this.targetPlatform,
    required this.targetUsers,
    required this.action,
    required this.canDismiss,
  });

  @override
  String toString() {
    return 'Message{message: $message, expireOn: $expireOn, targetPlatform: $targetPlatform, targetUsers: $targetUsers, action: $action, canDismiss: $canDismiss}';
  }
}

class MessageMapper extends Mappable<Message> {
  @override
  Message fromMap(Map<String, dynamic> values) {
    return Message(
      message: values['message'],
      expireOn: values['expireOn'],
      targetPlatform: values['targetPlatform'],
      targetUsers: values['targetUsers'],
      action: values['action'],
      canDismiss: values['canDismiss'] == true,
    );
  }

  @override
  Map<String, dynamic> toMap(Message object) {
    throw UnimplementedError();
  }
}
