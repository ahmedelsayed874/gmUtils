import 'dart:io';

import 'package:gmutils_flutter/data/data_source/notifications/notifications_datasource.dart';
import 'package:gmutils_flutter/data/data_source/users/users_datasource.dart';
import 'package:gmutils_flutter/data/models/users/user_account.dart';
import 'package:gmutils_flutter/ui/utils/iscreen_driver.dart';
import 'package:gmutils_flutter/zgmutils/data_utils/firebase/fcm.dart';
import 'package:gmutils_flutter/zgmutils/gm_main.dart';
import 'package:gmutils_flutter/zgmutils/utils/result.dart';
import 'package:gmutils_flutter/zgmutils/utils/string_set.dart';

import '../../../data/data_source/misc/misc_datasource.dart';
import '../../../data/models/attachment.dart';

abstract class UserProfileScreenDelegate
    extends IScreenDriverDependantDelegate {}

abstract class UserProfileScreenDriverAbs extends IScreenDriver {
  late UserProfileScreenDelegate delegate;

  final NotificationsDataSource notificationsDataSource;
  final MiscDataSource miscDataSource;
  final int? targetAccountId;

  UserProfileScreenDriverAbs(
    this.delegate, {
    required super.usersDataSource,
    required this.notificationsDataSource,
    required this.miscDataSource,
    required this.targetAccountId,
    required UserAccount? targetAccount,
  }) : super(delegate) {
    if (targetAccountId != null) {
      if (targetAccount == null) {
        _fetchUserAccountInfo();
      } else {
        _targetUserAccount = targetAccount;
      }
    }
  }

  UserAccount get authUserAccount => usersDataSource.cachedUserAccount!;

  //---------------------------------------------------------------------------

  UserAccount? _targetUserAccount;

  void _fetchUserAccountInfo() async {
    assert(targetAccountId != null);

    var response = await usersDataSource.getAccountInfo(
      accountId: targetAccountId!,
    );

    if (response.isSuccess) {
      _targetUserAccount = response.data;
      delegate.updateView();
    } else {
      Future.delayed(const Duration(seconds: 1), () {
        delegate.showErrorMessage(response.errorMessage, onRetry: () {
          _fetchUserAccountInfo();
        });
      });
    }
  }

  //---------------------------------------------------------------------------

  String? get userFullName => desiredUserAccount?.fullname;

  bool get isAuthUserProfile {
    if (targetAccountId == null) {
      return true;
    } else if (targetAccountId == authUserAccount.id) {
      return true;
    } else {
      return false;
    }
  }

  bool get isAuthUserCanSeeContacts {
    if (isAuthUserProfile) {
      return true;
    } else if (authUserAccount.isManagerOrSupervisor) {
      return true;
    } else if (authUserAccount.isTeacher &&
        (_targetUserAccount?.isManagerOrSupervisor == true ||
            _targetUserAccount?.isTeacher == true)) {
      return true;
    } else {
      return false;
    }
  }

  UserAccount? get desiredUserAccount {
    if (targetAccountId == null) {
      return authUserAccount;
    } else if (targetAccountId == authUserAccount.id) {
      return authUserAccount;
    } else {
      return _targetUserAccount;
    }
  }

  //---------------------------------------------------------------------------

  File? _profilePhotoImageFile;
  File? get profilePhotoImageFile => _profilePhotoImageFile;

  //---------------------------------------------------------------------------

  Future<Result<UserAccount>> getUserAccountInfo({
    required int accountId,
  }) async {
    var response = await usersDataSource.getAccountInfo(accountId: accountId);
    return Result(response.data, message: StringSet(response.errorMessage));
  }

  void changePhoneNumber(String phoneNumber) async {
    assert(isAuthUserProfile);

    delegate.showWaitView();

    var response = await usersDataSource.changePhoneNumber(
      accountId: authUserAccount.id,
      phoneNumber: phoneNumber,
    );

    await delegate.hideWaitView();

    if (response.isSuccess) {
      delegate.updateView();
    } else {
      delegate.showErrorMessage(response.errorMessage);
    }
  }

  Future<bool> logout({
    NotificationsDataSource? notificationsDataSource,
  }) async {
    var fcmToken = (await FCM.instance.deviceToken) ?? '';
    if (fcmToken.isEmpty) {
      return true;
    }

    delegate.showWaitView();

    var response = await usersDataSource.logout(
      notificationsDataSource:
          notificationsDataSource ?? NotificationsDataSource.instance,
      fcm: FCM.instance,
    );

    await delegate.hideWaitView();

    if (!response.isSuccess) {
      delegate.showErrorMessage(response.errorMessage);
    }

    return response.isSuccess;
  }

  //---------------------------------------------------------------------------

  Map<String, Attachment>? _uploads;

  Future<bool> changeProfilePhoto(File file) async {
    assert(isAuthUserProfile);
    _uploads ??= {};

    _profilePhotoImageFile = file;
    delegate.updateView();

    delegate.showWaitView();

    //region upload photo
    if (_uploads![file.path] == null) {
      var uploadResponse = await miscDataSource.uploadFile(file: file, fileType: Attachment.attachmentTypeImage,);

      if (uploadResponse.data != null) {
        _uploads![file.path] = uploadResponse.data!;

      } else {
        //todo apply this code if user choose to cancel retry
        _profilePhotoImageFile = null;
        delegate.updateView();

        await delegate.hideWaitView();

        uploadResponse.defaultErrorMessage = App.isEnglish ? 'Error: no data' : 'خطأ: لا توجد بيانات';
        delegate.showErrorMessage(uploadResponse.errorMessage, onRetry: () {
          changeProfilePhoto(file);
        });

        return false;
      }
    }
    //endregion

    var attachment = _uploads![file.path]!;

    var changeResponse = await usersDataSource.changeAccountPhoto(
        accountId: authUserAccount.id,
        photoPath: attachment.getAttachmentFilePath(includeDomain: false),
    );

    await delegate.hideWaitView();

    if (!changeResponse.isSuccess) {
      _profilePhotoImageFile = null;
      delegate.updateView();
    }

    return changeResponse.isSuccess;
  }

}

class UserProfileScreenDriver extends UserProfileScreenDriverAbs {
  UserProfileScreenDriver(
    super.delegate, {
    required super.targetAccountId,
    required super.targetAccount,
  }) : super(
          usersDataSource: UsersDataSource.instance,
          notificationsDataSource: NotificationsDataSource.instance,
          miscDataSource: MiscDataSource.instance,
        );
}
