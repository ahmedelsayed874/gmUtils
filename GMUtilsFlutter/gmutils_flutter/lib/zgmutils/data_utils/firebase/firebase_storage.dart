import 'dart:io';

import 'package:firebase_core/firebase_core.dart';
import 'package:firebase_storage/firebase_storage.dart' as firebase_storage;
import 'package:path_provider/path_provider.dart';

import '../../utils/date_op.dart';
import '../../utils/logs.dart';
import '../../utils/collections/string_set.dart';
import 'firebase_utils.dart';
import 'response.dart';

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
    Logs.print(() => 'FirebaseStorage[Call].uploadTo('
        'reference: ${reference.fullPath}, '
        'file: ${file.path})');

    try {
      await reference.putFile(file);
      var link = await reference.getDownloadURL();
      Logs.print(() => 'FirebaseStorage[Response].uploadTo(reference: ${reference.fullPath}) ----> $link');
      return Response.success(data: link);
    } on FirebaseException catch (e) {
      Logs.print(() => 'FirebaseStorage[Response.Exception].uploadTo() ----> $e');
      return Response.failed(error: StringSet(e.code));
    }
  }

  //----------------------------------------------------------------------------

  @override
  Future<Response<String>> getDownloadURL(String firebasePath) async {
    Logs.print(() => 'FirebaseStorage[Call].getDownloadURL(firebasePath: $firebasePath)');

    try {
      firebasePath = _refinePath(firebasePath);
      var reference = storage.ref(firebasePath);
      var url = await reference.getDownloadURL();
      Logs.print(() => 'FirebaseStorage[Response].getDownloadURL(firebasePath: $firebasePath) ---> $url');
      return Response.success(data: url);
    } catch (e) {
      Logs.print(() => 'FirebaseStorage[Response.Exception].getDownloadURL ---> $e');
      if (e is FirebaseException) {
        return Response.failed(error: StringSet(e.code));
      } else {
        return Response.failed(error: StringSet('$e'));
      }
    }
  }

  //----------------------------------------------------------------------------

  @override
  Future<Response<File>> download(
    String fromPath, {
    File? suggestedOutFile,
  }) async {
    Logs.print(() => 'FirebaseStorage[Call].download(fromPath: $fromPath)');

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
      Logs.print(() => 'FirebaseStorage[Response].download(fromPath: $fromPath) ---> file downloaded to: ${outFile.path}');
      return Response.success(data: outFile);
    } on FirebaseException catch (e) {
      Logs.print(() => 'FirebaseStorage[Response.Exception].download() ---> $e');
      return Response.failed(error: StringSet(e.code));
    }
  }

  //----------------------------------------------------------------------------

  @override
  Future<Response<bool>> delete(String atPath) async {
    Logs.print(() => 'FirebaseStorage[Call].delete(atPath: $atPath)');

    try {
      atPath = _refinePath(atPath);
      var reference = storage.ref(atPath);
      await reference.delete();
      Logs.print(() => 'FirebaseStorage[Response].delete(atPath: $atPath) -----> true');
      return Response.success(data: true);
    } catch (e) {
      Logs.print(() => 'FirebaseStorage[Response].delete() -----> EXCEPTION:: $e');
      if (e is FirebaseException) {
        return Response.failed(error: StringSet(e.code, e.code));
      } else {
        return Response.failed(error: StringSet('$e', '$e'));
      }
    }
  }
}
