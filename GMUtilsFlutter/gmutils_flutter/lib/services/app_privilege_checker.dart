
import 'package:gmutils_flutter/data/models/users/user_account.dart';
import 'package:gmutils_flutter/services/configs/app_configs.dart';

class AppPrivilegeChecker {

  static bool canSendMail({
    required String senderAccountType,
    required String receiverAccountType,
  }) {
    var b = AppConfigs.appConfigsData.hasAppFeature(
      AppConfigsData.appFeatureMails,
      authAccountType: senderAccountType,
    );

    if (b != true) return false;

    String senderAccountType2 = senderAccountType.toLowerCase();
    String receiverAccountType2 = receiverAccountType.toLowerCase();

    var blockedTypes = [
      UserAccount.accountTypeParent.toLowerCase(),
      UserAccount.accountTypeStudent.toLowerCase(),
      ''
    ];

    if (blockedTypes.contains(senderAccountType2)) {
      return false;

    } else if (blockedTypes.contains(receiverAccountType2)) {
      return false;
    }

    return true;
  }

  static bool canStartChat({
    required String senderAccountType,
    required String receiverAccountType,
  }) {
    var b = AppConfigs.appConfigsData.hasAppFeature(
      AppConfigsData.appFeatureChats,
      authAccountType: senderAccountType,
    );
    if (b != true) return false;

    String senderAccountType2 = senderAccountType.toLowerCase();
    String receiverAccountType2 = receiverAccountType.toLowerCase();

    var blockedTypes = [
      UserAccount.accountTypeParent.toLowerCase(),
      UserAccount.accountTypeStudent.toLowerCase(),
      '',
    ];

    if (blockedTypes.contains(senderAccountType2)) {
      if (blockedTypes.contains(receiverAccountType2)) {
        return false;
      }
    }

    return true;
  }

}