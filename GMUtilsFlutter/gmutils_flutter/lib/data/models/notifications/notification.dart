import 'package:gmutils_flutter/zgmutils/utils/mappable.dart';
import 'package:gmutils_flutter/zgmutils/utils/notifications.dart';

class Notification {
  static const String relatedObjectName_Mail = 'Mail';

  static const String relatedObjectName_Chat = 'Chat';
  static const String relatedObjectName_ChatMessage = 'ChatMessage';
  static const String relatedObjectName_ChatMessageReaction = 'ChatMessageReaction';
  static const String relatedObjectName_ChatMessagePollResultUpdated = 'ChatMessagePollResultUpdated';

  static List<AndroidNotificationChannelProperties> allChannelInfo() {
    List<AndroidNotificationChannelProperties?> lst = [
      relatedObjectName_Mail,
      relatedObjectName_Chat,
      relatedObjectName_ChatMessage,
      relatedObjectName_ChatMessageReaction,
      relatedObjectName_ChatMessagePollResultUpdated,
    ].map((e) => channelInfoOf(e)).toList();

    lst.removeWhere((element) => element == null);

    return lst.map((e) => e!).toList();
  }

  static List<String> unneedNotificationIds() {
    return [
      'chat0',
      'chatMessage0',
    ];
  }

  static AndroidNotificationChannelProperties? channelInfoOf(
    String objectName,
  ) {
    if (objectName == relatedObjectName_Mail) {
      return const AndroidNotificationChannelProperties(
        channelId: 'mail',
        channelName: 'Mails',
        importance: Importance2(Importance2.max),
        soundFile: null,
      );
    }
    if (objectName == relatedObjectName_Chat) {
      return const AndroidNotificationChannelProperties(
        channelId: 'chat',
        channelName: 'Chat',
        importance: Importance2(Importance2.max),
        soundFile: SoundFile(name: 'chat_alert', extension: 'mp3'),
      );
    }
    if (
    objectName == relatedObjectName_ChatMessage ||
    objectName == relatedObjectName_ChatMessageReaction ||
    objectName == relatedObjectName_ChatMessagePollResultUpdated
    ) {
      return const AndroidNotificationChannelProperties(
        channelId: 'chatMessage',
        channelName: 'Chat Message',
        importance: Importance2(Importance2.max),
        soundFile: SoundFile(name: 'chat_alert', extension: 'mp3'),
      );
    }

    return null;
  }

  //---------------------------------------------------------------------------

  int notificationId;
  String titleEn;
  String titleAr;
  String bodyEn;
  String bodyAr;
  bool isRead; //→ will use in controlling the card color

  String? relatedObjectName; //→ a predefined name represents the target
  //                             object (or Module) like: [MailMessage -
  //                             ChatMessage - HomeWork - Exam - …. etc]
  int? relatedObjectId; //→ holds the id of target object
  //                         (or Module) to use it in loading data
  //                         like: messageId, chatId, …. etc

  String? extraParameters; //→ json array

  String createdAt; //→ yyyy-MM-dd HH:mm:ssZ [e.g.: 2000-01-01 12:00:00+0300]

  Notification({
    required this.notificationId,
    required this.titleEn,
    required this.titleAr,
    required this.bodyEn,
    required this.bodyAr,
    required this.isRead,
    required this.relatedObjectName,
    required this.relatedObjectId,
    required this.extraParameters,
    required this.createdAt,
  });

  @override
  String toString() {
    return 'Notification{notificationId: $notificationId, titleEn: $titleEn, titleAr: $titleAr, bodyEn: $bodyEn, bodyAr: $bodyAr, isRead: $isRead, relatedObjectName: $relatedObjectName, relatedObjectId: $relatedObjectId, extraParameters: $extraParameters, createdAt: $createdAt}';
  }

  bool isRelatedToChat() {
    return relatedObjectName == Notification.relatedObjectName_Chat ||
        relatedObjectName == Notification.relatedObjectName_ChatMessage ||
        relatedObjectName == Notification.relatedObjectName_ChatMessageReaction ||
        relatedObjectName == Notification.relatedObjectName_ChatMessagePollResultUpdated ;
  }

  bool isRelatedToChatMessageUpdates() {
    return relatedObjectName == Notification.relatedObjectName_ChatMessageReaction ||
        relatedObjectName == Notification.relatedObjectName_ChatMessagePollResultUpdated ;
  }

}

class NotificationMapper extends Mappable<Notification> {
  @override
  Notification fromMap(Map<String, dynamic> values) {
    /*
    {
    "notificationId":"64",

    "titleEn":"New Chat Message Received",
    "titleAr":"يوجد رسالة محادثة جديدة",

    "bodyEn":"New Chat Message Received From : Test Student 1 مجموعة المحاربين Testers",
    "bodyAr":"يوجد رسالة محادثة جديدة من : Test Student 1 مجموعة المحاربين Testers",

    "relatedObjectId":"6",
    "relatedObjectName":"Chat",

    "isRead":"false",
    "extraParameters":"{\"ChatMessageId\":91,\"ChatId\":6,\"CreateDate\":\"2024-08-04 19:41:30\",\"Message\":\"test notification\",\"SenderAccountId\":14,\"SenderAccountName\":\"Test Student 1\",\"SenderAccountPhotoPath\":\"https://localhost:5001/https://staging.bls-edu.com/uploads/images/0001/20240803200559222.jpg\"}",

    "createdAt":"2024-08-04 19:41:31",

    "NotificationReceiverId":"9",
    }
     */

    return Notification(
      notificationId: int.tryParse('${values['notificationId']}') ?? 0,
      titleEn: values['titleEn'] ?? '',
      titleAr: values['titleAr'] ?? '',
      bodyEn: values['bodyEn'] ?? '',
      bodyAr: values['bodyAr'] ?? '',
      isRead: '${values['isRead']}'.toLowerCase() == 'true',
      relatedObjectName: values['relatedObjectName'],
      relatedObjectId: values['relatedObjectId'] == null
          ? null
          : int.tryParse('${values['relatedObjectId']}'),
      extraParameters: values['extraParameters'],
      createdAt: values['createdAt'] ?? '',
    );
  }

  @override
  Map<String, dynamic> toMap(Notification object) {
    return {
      'notificationId': object.notificationId,
      'titleEn': object.titleEn,
      'titleAr': object.titleAr,
      'bodyEn': object.bodyEn,
      'bodyAr': object.bodyAr,
      'isRead': object.isRead,
      'relatedObjectName': object.relatedObjectName,
      'relatedObjectId': object.relatedObjectId,
      'extraParameters': object.extraParameters,
      'createdAt': object.createdAt,
    };
  }
}
