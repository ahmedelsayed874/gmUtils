import 'dart:io';

import 'package:firebase_core/firebase_core.dart';
import 'package:firebase_storage/firebase_storage.dart' as firebase_storage;
import 'package:pallora/zgmutils/data_utils/firebase/response.dart';
import 'package:pallora/zgmutils/utils/date_op.dart';
import 'package:pallora/zgmutils/utils/string_set.dart';
import 'package:path_provider/path_provider.dart';

import '../../gm_main.dart';
import '../../utils/logs.dart';
import 'firebase_utils.dart';

abstract class IFirebaseStorage {
  Future<Response<String>> upload(File file, {required String toPath});

  Future<Response<String>> getDownloadURL(String firebasePath);

  Future<Response<File>> download(String fromPath, {File? suggestedOutFile});

  Future<Response<bool>> delete(String atPath);
}

class FirebaseStorage implements IFirebaseStorage {
  late final firebase_storage.FirebaseStorage storage;

  FirebaseStorage() {
    //Firebase.initializeApp();
    storage = firebase_storage.FirebaseStorage.instance;
  }

  String _refinePath(String path) {
    path = FirebaseUtils.refinePathFragmentNames(path);
    return path;
  }

  //----------------------------------------------------------------------------

  @override
  Future<Response<String>> upload(File file, {required String toPath}) async {
    toPath = _refinePath(toPath);
    var ref = storage.ref(toPath);
    return uploadTo(ref, file);
  }

  Future<Response<String>> uploadTo(
    firebase_storage.Reference reference,
    File file,
  ) async {
    try {
      await reference.putFile(file);
      var link = await reference.getDownloadURL();
      return Response.success(data: link);
    } on FirebaseException catch (e) {
      Logs.print(() => e);
      return Response.failed(error: StringSet(e.code, e.code));
    }
  }

  //----------------------------------------------------------------------------

  Future<Response<String>> getDownloadURL(String firebasePath) async {
    try {
      firebasePath = _refinePath(firebasePath);
      var reference = storage.ref(firebasePath);
      var url = await reference.getDownloadURL();
      return Response.success(data: url);
    } catch (e) {
      Logs.print(() => e);
      if (e is FirebaseException) {
        return Response.failed(error: StringSet(e.code, e.code));
      } else {
        return Response.failed(error: StringSet('$e', '$e'));
      }
    }
  }

  //----------------------------------------------------------------------------

  Future<Response<File>> download(
    String fromPath, {
    File? suggestedOutFile,
  }) async {
    File outFile;
    if (suggestedOutFile != null) {
      outFile = suggestedOutFile;
    } else {
      var i = fromPath.lastIndexOf('/');
      String fileName;
      if (i >= 0 && i < fromPath.length) {
        fileName = fromPath.substring(i + 1);
      } else {
        fileName = DateOp()
            .formatForDatabase(DateTime.now(), dateOnly: false)
            .replaceAll(
              "-",
              '',
            );
      }

      Directory appDocDir = await getDownloadsDirectory() ??
          await getExternalStorageDirectory() ??
          await getApplicationDocumentsDirectory();
      outFile = File('${appDocDir.path}/$fileName');
    }

    if (!outFile.existsSync()) {
      outFile.createSync(recursive: true);
    }

    try {
      fromPath = _refinePath(fromPath);
      await storage.ref(fromPath).writeToFile(outFile);
      return Response.success(data: outFile);
    } on FirebaseException catch (e) {
      // e.g, e.code == 'canceled'
      Logs.print(() => e);
      return Response.failed(error: StringSet(e.code, e.code));
    }
  }

  //----------------------------------------------------------------------------

  Future<Response<bool>> delete(String atPath) async {
    try {
      atPath = _refinePath(atPath);
      var reference = storage.ref(atPath);
      await reference.delete();
      return Response.success(data: true);
    } catch (e) {
      Logs.print(() => e);
      if (e is FirebaseException) {
        return Response.failed(error: StringSet(e.code, e.code));
      } else {
        return Response.failed(error: StringSet('$e', '$e'));
      }
    }
  }
}
