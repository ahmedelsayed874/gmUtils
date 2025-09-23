import 'package:gmutils_flutter/main.dart' as main;
import 'package:gmutils_flutter/zgmutils/data_utils/utils/mappable.dart';


class UserAccount {
  static const String accountTypeManager = 'Manager';
  //static const String accountTypeSupervisor = 'Supervisor';
  static const String accountTypeTeacher = 'Teacher';
  static const String accountTypeStudent = 'Student';
  static const String accountTypeParent = 'Parent';

  //----------------------------------------------------------------------------

  final int id;

  final String accountType; //[Manager-Supervisor - Teacher - Student - Parent]

  String? email;
  String? mobile;

  final String firstName;
  final String? middleName;
  final String? lastName;

  String? _personalPhoto;
  String? idNumber;

  final String? dateOfBirth; //yyyy-MM-dd
  final String? gender; //[male - female]

  String? lastLoginTime; //yyyy-MM-dd HH:mm:ssZ

  final String? createdAt; //yyyy-MM-dd HH:mm:ssZ
  final String? updatedAt; //yyyy-MM-dd HH:mm:ssZ


  UserAccount({
    required this.id,
    required this.accountType,
    //
    required this.email,
    required this.mobile,
    //
    required this.firstName,
    required this.middleName,
    required this.lastName,
    //
    required String? personalPhoto,
    required this.idNumber,
    required this.dateOfBirth,
    required this.gender,
    //
    required this.lastLoginTime,
    //
    required this.createdAt,
    required this.updatedAt,
    //
  }) : _personalPhoto = personalPhoto;

  String get fullname {
    var name = firstName;
    if (middleName?.isNotEmpty == true) {
      name += ' $middleName';
    }
    if (lastName?.isNotEmpty == true) {
      name += ' $lastName';
    }
    return name;
  }

  bool get isManagerOrSupervisor =>
      accountType.toLowerCase() == accountTypeManager.toLowerCase();/* ||
      accountType.toLowerCase() == accountTypeSupervisor.toLowerCase();*/

  bool get isTeacher =>
      accountType.toLowerCase() == accountTypeTeacher.toLowerCase();

  bool get isStudent =>
      accountType.toLowerCase() == accountTypeStudent.toLowerCase();

  bool get isParent =>
      accountType.toLowerCase() == accountTypeParent.toLowerCase();

  bool get isMale => gender?.toLowerCase() != 'female';

  String job(bool en) {
    if (isManagerOrSupervisor) {
      if (accountType.toLowerCase() == accountTypeManager.toLowerCase()) {
        return en ? 'Manager' : 'مدير';
      } else {
        return en ? 'Supervisor' : 'مشرف';
      }
    }
    //
    else {
      return "";
    }
  }

  set personalPhoto(String? p) => _personalPhoto = p;
  String? get personalPhoto {
    if (_personalPhoto == null) return null;

    if (_personalPhoto!.startsWith('http://') ||
        _personalPhoto!.startsWith('https://')) {
      return _personalPhoto;
    } else {
      return main.serverUrl + _personalPhoto!;
    }
  }

  @override
  String toString() {
    return 'UserAccount{'
        'id: $id, accountType: $accountType, email: $email, mobile: $mobile, '
        'firstName: $firstName, middleName: $middleName, lastName: $lastName, '
        'personalPhoto: $personalPhoto, idNumber: $idNumber, dateOfBirth: $dateOfBirth, '
        'lastLoginTime: $lastLoginTime, '
        'gender: $gender, createdAt: $createdAt, updatedAt: $updatedAt'
        '}';
  }
}

class UserAccountMapper extends Mappable<UserAccount> {
  @override
  UserAccount fromMap(Map<String, dynamic> values) {
    return UserAccount(
      id: values['id'],
      accountType: values['accountType'],
      //
      email: values['email'],
      mobile: values['mobile'],
      //
      firstName: values['firstName'],
      middleName: values['middleName'],
      lastName: values['lastName'],
      //
      personalPhoto: values['personlPhoto'] ?? values['personalPhoto'],
      idNumber: values['idNumber'],
      dateOfBirth: values['dateOfBirth'],
      gender: values['gender'],
      //
      lastLoginTime: values['lastLoginTime'],
      //
      createdAt: values['createdAt'],
      updatedAt: values['updatedAt'],
    );
  }

  @override
  Map<String, dynamic> toMap(UserAccount object) {
    return {
      'id': object.id,
      'accountType': object.accountType,
      //
      'email': object.email,
      'mobile': object.mobile,
      //
      'firstName': object.firstName,
      'middleName': object.middleName,
      'lastName': object.lastName,
      //
      'personalPhoto': object.personalPhoto,
      'idNumber': object.idNumber,
      'dateOfBirth': object.dateOfBirth,
      'gender': object.gender,
      //
      'lastLoginTime': object.lastLoginTime,
      //
      'createdAt': object.createdAt,
      'updatedAt': object.updatedAt,

    };
  }
}
