import '../../../zgmutils/utils/mappable.dart';

class UserAccountHeader {
  final int accountId;

  final String? accountType;

  final String firstName;
  final String? middleName;
  final String? lastName;

  final String? personalPhoto;
  final String? lastLoginTime; //yyyy-MM-dd HH:mm:ssZ

  TeacherInfo? teacherInfo;
  final StudentInfo? studentInfo;
  final ParentInfo? parentInfo;

  UserAccountHeader({
    required this.accountId,
    required this.accountType,
    //
    required this.firstName,
    required this.middleName,
    required this.lastName,
    //
    required this.personalPhoto,
    required this.lastLoginTime,
    //
    required this.teacherInfo,
    required this.studentInfo,
    required this.parentInfo,
  });

  String get fullname => '$firstName'
      '${middleName == null ? '' : ' $middleName'}'
      '${lastName == null ? '' : ' $lastName'}';

  @override
  String toString() {
    return 'UserAccountHeader{accountId: $accountId, accountType: $accountType, '
        'firstName: $firstName, middleName: $middleName, lastName: $lastName, '
        'personalPhoto: $personalPhoto, '
        'lastLoginTime: $lastLoginTime, '
        'teacherInfo: $teacherInfo, '
        'studentInfo: $studentInfo, '
        'parentInfo: $parentInfo}';
  }
}

class UserAccountHeaderMapper extends Mappable<UserAccountHeader> {
  @override
  UserAccountHeader fromMap(Map<String, dynamic> values) {
    /*
            "name": "student0",
            "accountStatus": "Active",
            "accountStatusNote": "",
            "email": null,
            "mobile": null,
            "dateOfBirth": "",
            "gender": "",
            "isFirstLogin": false,
            "teacherData": null,
     */
    return UserAccountHeader(
      accountId: values['id'] ?? values['accountId'],
      accountType: values['accountType'],
      firstName: values['firstName'] ?? values['name'],
      middleName: values['middleName'],
      lastName: values['lastName'],
      personalPhoto: values['personlPhoto'] ?? values['personalPhoto'],
      lastLoginTime: values['lastLoginTime'] ?? values['lastLoginTime'],
      //
      teacherInfo: values['teacherData'] == null
          ? null
          : TeacherInfoMapper().fromMap(values['teacherData']),
      studentInfo: values['studentData'] == null
          ? null
          : StudentInfoMapper().fromMap(values['studentData']),
      parentInfo: values['parentData'] == null
          ? null
          : ParentInfoMapper().fromMap(values['parentData']),
    );
  }

  @override
  Map<String, dynamic> toMap(UserAccountHeader object) {
    return {
      'accountId': object.accountId,
      'id': object.accountId,
      'accountType': object.accountType,
      'firstName': object.firstName,
      'middleName': object.middleName,
      'lastName': object.lastName,
      'personalPhoto': object.personalPhoto,
      'lastLoginTime': object.lastLoginTime,
      'teacherInfo': object.teacherInfo == null
          ? null
          : TeacherInfoMapper().toMap(object.teacherInfo!),
      'studentInfo': object.studentInfo == null
          ? null
          : StudentInfoMapper().toMap(object.studentInfo!),
      'parentInfo': object.parentInfo == null
          ? null
          : ParentInfoMapper().toMap(object.parentInfo!),
    };
  }
}

//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

class TeacherInfo {
  List<String> subjectNames;

  TeacherInfo({required this.subjectNames});

  @override
  String toString() {
    return 'TeacherInfo{subjectNames: $subjectNames}';
  }
}

class TeacherInfoMapper extends Mappable<TeacherInfo> {
  @override
  TeacherInfo fromMap(Map<String, dynamic> values) {
    return TeacherInfo(subjectNames: List.from(values['subjectNames']));
  }

  @override
  Map<String, dynamic> toMap(TeacherInfo object) {
    return {'subjectNames': object.subjectNames};
  }
}

//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

class StudentInfo {
  String learningStageName;
  String learningStageLevelName;
  String? classroomName;
  int? parentAccountId;

  StudentInfo({
    required this.learningStageName,
    required this.learningStageLevelName,
    required this.classroomName,
    required this.parentAccountId,
  });

  String get classroomPath {
    String path = learningStageName;
    path += ' • $learningStageLevelName';
    if (classroomName != null) {
      path += ' • $classroomName';
    }

    return path;
  }

  @override
  String toString() {
    return 'StudentInfo{learningStageName: $learningStageName, learningStageLevelName: $learningStageLevelName, classroomName: $classroomName, parentAccountId: $parentAccountId}';
  }
}

class StudentInfoMapper extends Mappable<StudentInfo> {
  @override
  StudentInfo fromMap(Map<String, dynamic> values) {
    /*
    "studdingYear": "2025",
    "schoolDepartment": null,
    "learningStage": null,
    "learningStageLevel": null,
    "classRoom": null,
    "teachers": null
     */
    return StudentInfo(
      learningStageName: values['learningStageName'],
      learningStageLevelName: values['learningStageLevelName'],
      classroomName: values['classRoomName'] ?? values['classroomName'],
      parentAccountId: values['parentAccountId'],
    );
  }

  @override
  Map<String, dynamic> toMap(StudentInfo object) {
    return {
      'learningStageName': object.learningStageName,
      'learningStageLevelName': object.learningStageLevelName,
      'classroomName': object.classroomName,
      'parentAccountId': object.parentAccountId,
    };
  }
}

//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

class ParentInfo {
  List<StudentInfo> students;

  ParentInfo({required this.students});

  @override
  String toString() {
    return 'ParentInfo{students: $students}';
  }
}

class ParentInfoMapper extends Mappable<ParentInfo> {
  @override
  ParentInfo fromMap(Map<String, dynamic> values) {
    return ParentInfo(
      students: StudentInfoMapper().fromMapList(values['students']) ?? [],
    );
  }

  @override
  Map<String, dynamic> toMap(ParentInfo object) {
    return {
      'students': StudentInfoMapper().toMapList(object.students),
    };
  }
}
