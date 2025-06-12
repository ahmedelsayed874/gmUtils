import 'dart:async';

import 'package:firebase_core/firebase_core.dart';
import 'package:firebase_database/firebase_database.dart';
import 'package:ogtech_app_store/zgmutils/utils/text_utils.dart';

import '../../utils/logs.dart';
import '../../utils/mappable.dart';
import '../../utils/string_set.dart';
import 'firebase_utils.dart';
import 'response.dart';

abstract class IFirebaseDatabaseOp<T> {
  final Mappable<T> mappable;
  final String rootNodeName;

  IFirebaseDatabaseOp({
    required this.mappable,
    required this.rootNodeName,
  });

  //----------------------------------------------------------------------------

  Future<bool> isConnectionAvailable() {
    return FirebaseUtils.isConnectionAvailable();
  }

  //----------------------------------------------------------------------------

  Future<Response<bool>> saveData(T data, {required String? subNodePath});

  Future<Response<bool>> saveMultipleData({
    required Map<String, T> nodesAndData,
  });

  //----------------------------------------------------------------------------

  Future<Response<List<T>>> retrieveAll({
    FBFilterOption? filterOption,
    List<Map> Function(Object value)? collectionSource,
  });

  //----------------------------------------------------------------------------

  Future<Response<T>> retrieveOnly({
    required String? subNodePath,
  });

  //----------------------------------------------------------------------------

  void listenToChanges({
    required String? subNodePath,
    required Function(T) onChange,
    void Function()? onDone,
    void Function(Object, StackTrace)? onError,
  });

  void listenToChangesSpecific<N>({
    required String? subNodePath,
    required Function(N) onChange,
    void Function()? onDone,
    void Function(Object, StackTrace)? onError,
  });

  void listenToAdding({
    required String? subNodePath,
    required Function(T) onAdd,
    void Function()? onDone,
    void Function(Object, StackTrace)? onError,
  });

  void listenToAddingSpecific<N>({
    required String? subNodePath,
    required Function(N) onAdd,
    void Function()? onDone,
    void Function(Object, StackTrace)? onError,
  });

  void removeListeners({required String? subNodePath});

  //----------------------------------------------------------------------------

  Future<Response<bool>> clear();

  Future<Response<bool>> removeNode({required String subNodePath});
}

class FBFilterOption {
  final FBFilterTypes type;
  final String key;
  final Object? args;
  final int? limit;

  FBFilterOption({
    required this.type,
    required this.key,
    required this.args,
    required this.limit,
  });

  @override
  String toString() {
    return 'FBFilterOption{type: $type, key: $key, args: $args, limit: $limit}';
  }
}

enum FBFilterTypes {
  equal,
  greaterThan,
  greaterThanOrEqual,
  lessThan,
  lessThanOrEqual,
}

//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

class FirebaseDatabaseOp<T> extends IFirebaseDatabaseOp<T> {
  FirebaseDatabaseOp({
    required super.mappable,
    required super.rootNodeName,
  });

  //----------------------------------------------------------------------------

  DatabaseReference? _databaseReference;

  Future<DatabaseReference> get databaseReference async {
    if (_databaseReference == null) {
      init() {
        var databaseReference = FirebaseDatabase.instance.ref();
        _databaseReference = databaseReference.child(rootNodeName);
      }

      try {
        init();
      } catch (e) {
        Logs.print(() => 'FirebaseDatabaseOp.databaseReference ---> Exception:: $e');
        await Firebase.initializeApp();
        init();
      }
    }

    return _databaseReference!;
  }

  //----------------------------------------------------------------------------

  String _refineKeyName(String name) => FirebaseUtils.refineKeyName(name);

  Future<DatabaseReference> _getReferenceOfNode(String? subNodePath) async {
    if (subNodePath == null) return databaseReference;

    subNodePath = FirebaseUtils.refinePathFragmentNames(subNodePath);

    var ref = await databaseReference;
    return ref.child(subNodePath);
  }

  //----------------------------------------------------------------------------

  @override
  Future<Response<bool>> saveData(T data, {required String? subNodePath}) async {
    DatabaseReference ref = await _getReferenceOfNode(subNodePath);
    return saveDataTo(ref, data);
  }

  Future<Response<bool>> saveDataTo(DatabaseReference ref, T data) async {
    try {
      bool added = false;

      await ref
          .set(mappable.toMap(data))
          .whenComplete(() async => added = true)
          .onError((error, stackTrace) async => added = false);

      Logs.print(() => 'FirebaseDatabaseOp.saveDataTo'
          '(ref: ${ref.path}, data: ${TextUtils().trimEnd('$data')}) '
          '---> added: $added');

      return Response.success(data: added);
    } catch (e) {
      Logs.print(() => 'FirebaseDatabaseOp.saveDataTo(ref: ${ref.path}) ---> Exception:: $e');
      return Response.failed(
        error: StringSet(e.toString()),
        connectionFailed: true,
      );
    }
  }

  @override
  Future<Response<bool>> saveMultipleData({required Map<String, T> nodesAndData,}) async {
    DatabaseReference ref = await _getReferenceOfNode(null);
    return saveMultipleDataTo(ref, nodesAndData: nodesAndData);
  }

  Future<Response<bool>> saveMultipleDataTo(
      DatabaseReference ref, {
        required Map<String, T> nodesAndData,
      }) async {
    try {
      bool added = false;

      var nodesAndMappedData = nodesAndData.map(
            (key, value) => MapEntry(
          _refineKeyName(key),
          mappable.toMap(value),
        ),
      );

      await ref
          .set(nodesAndMappedData)
          .whenComplete(() async => added = true)
          .onError((error, stackTrace) async => added = false);

      Logs.print(() => 'FirebaseDatabaseOp.saveMultipleDataTo'
          '(ref: ${ref.path}, data-length: ${nodesAndData.length}, data: ${TextUtils().trimEnd('$nodesAndData')}) '
          '---> added: $added');

      return Response.success(data: added);
    } catch (e) {
      Logs.print(() => 'FirebaseDatabaseOp.saveMultipleDataTo(ref: ${ref.path}) ---> Exception:: $e');
      return Response.failed(
        error: StringSet(e.toString()),
        connectionFailed: true,
      );
    }
  }

  //----------------------------------------------------------------------------

  @override
  Future<Response<List<T>>> retrieveAll({
    FBFilterOption? filterOption,
    List<Map> Function(Object value)? collectionSource,
  }) async {
    if (await isConnectionAvailable() == false) {
      return Response.failed(
        error: StringSet('No Connection', 'لا يوجد اتصال'),
        connectionFailed: true,
      );
    }

    var ref = await databaseReference;
    DataSnapshot? snapshot;

    try {
      Query? query;
      if (filterOption != null) {
        query = ref.orderByChild(filterOption.key);

        if (filterOption.type == FBFilterTypes.equal) {
          query = query.equalTo(filterOption.args);
        }
        //
        else if (filterOption.type == FBFilterTypes.greaterThan) {
          query = query.startAfter(filterOption.args);
        }
        //
        else if (filterOption.type == FBFilterTypes.greaterThanOrEqual) {
          query = query.startAt(filterOption.args);
        }
        //
        else if (filterOption.type == FBFilterTypes.lessThan) {
          query = query.endBefore(filterOption.args);
        }
        //
        else if (filterOption.type == FBFilterTypes.lessThanOrEqual) {
          query = query.endAt(filterOption.args);
        }

        if (filterOption.limit != null) {
          query = query.limitToFirst(filterOption.limit!);
        }
      }

      if (query == null) {
        var result = await ref.once();
        snapshot = result.snapshot;
      }
      //
      else {
        //snapshot = await query.get();
        var result = await query.once();
        snapshot = result.snapshot;
      }


      Response<List<T>> response;
      
      if (snapshot.exists) {
        List<T> list = await _mapData(
          snapshot,
          collectionSource,
        );

        response = Response.success(data: list);
      }
      //
      else {
        response = Response.failed(
          error: StringSet('No data', "لا توجد بيانات"),
        );
      }

      Logs.print(() => 'FirebaseDatabaseOp.retrieveAll'
          '(ref: ${ref.path}, filterOption: $filterOption) '
          '---> '
          'response.data-length: ${response.data?.length}, '
          'response.data: ${TextUtils().trimEnd('${response.data}')}, '
          'response.message: ${response.error}');
      
      return response;
    } catch (e) {
      Logs.print(() => 'FirebaseDatabaseOp.retrieveAll(ref: ${ref.path}) ---> snapshot: ${snapshot?.value} ---> Exception:: $e');
      return Response.failed(
        error: StringSet(e.toString()),
        connectionFailed: true,
      );
    }
  }

  Future<List<T>> _mapData(
      DataSnapshot dataSnapshot,
      List<Map> Function(Object value)? collectionSource,
      ) async {
    List<T> list = [];

    if (dataSnapshot.value != null) {
      dynamic values;

      if (collectionSource == null) {
        if (dataSnapshot.value is List) {
          values = dataSnapshot.value;
        }
        //
        else {
          List<Map> list = [];
          for (var entry in (dataSnapshot.value as Map).entries) {
            list.add(entry.value);
          }
          values = list;
        }
      }
      //
      else {
        values = collectionSource(dataSnapshot);
      }

      for (dynamic item in values) {
        var map = Map<String, dynamic>.from(item);
        list.add(mappable.fromMap(map));
      }
    }

    return list;
  }

  //----------------------------------------------------------------------------

  @override
  Future<Response<T>> retrieveOnly({required String? subNodePath}) async {
    var ref = await _getReferenceOfNode(subNodePath);
    return await retrieveOnlyFrom(ref: ref);
  }

  Future<Response<T>> retrieveOnlyFrom({
    required DatabaseReference ref,
  }) async {
    if (await isConnectionAvailable() == false) {
      return Response.failed(
        error: StringSet('No Connection', 'لا يوجد اتصال'),
        connectionFailed: true,
      );
    }

    var map;

    try {
      var event = await ref.once();
      var snapshot = event.snapshot;

      Response<T> response;
      
      if (snapshot.exists) {
        if (snapshot.value is List) {
          var data = snapshot.value as List;
          map = Map<String, dynamic>.from(data[0]);
        }
        //
        else {
          var data = snapshot.value as Map;
          var nodeName = ref.path.substring(ref.path.lastIndexOf("/") + 1);
          if (data[nodeName] == null) {
            map = Map<String, dynamic>.from(data);
          } else {
            map = Map<String, dynamic>.from(data[nodeName]);
          }
        }

        response = Response.success(data: mappable.fromMap(map));
      }
      //
      else {
        response = Response.failed(
          error: StringSet('No data', "لا توجد بيانات"),
        );
      }

      Logs.print(() => 'FirebaseDatabaseOp.retrieveOnlyFrom(ref: ${ref.path}) '
          '---> '
          'response.data: ${TextUtils().trimEnd('${response.data}')}, '
          'response.message: ${response.error}',
      );

      return response;
    } catch (e) {
      Logs.print(() => 'FirebaseDatabaseOp.retrieveOnlyFrom(ref: ${ref.path}) ---> result: $map ---> Exception:: $e');
      
      return Response.failed(
        error: StringSet(e.toString()),
        connectionFailed: true,
      );
    }
  }

  //----------------------------------------------------------------------------

  @override
  void listenToChanges({
    required String? subNodePath,
    required Function(T) onChange,
    void Function()? onDone,
    void Function(Object, StackTrace)? onError,
  }) async {
    return listenToChangesOn(
      ref: await _getReferenceOfNode(subNodePath),
      onChange: onChange,
      onDone: onDone,
      onError: onError,
    );
  }

  void listenToChangesOn({
    required DatabaseReference ref,
    required Function(T) onChange,
    void Function()? onDone,
    void Function(Object, StackTrace)? onError,
  }) async {
    return _listenTo(
      ref: ref,
      listenToAnyChange: true,
      onNewUpdate: onChange,
      onDone: onDone,
      onError: onError,
    );
  }

  @override
  void listenToAdding({
    required String? subNodePath,
    required Function(T) onAdd,
    void Function()? onDone,
    void Function(Object, StackTrace)? onError,
  }) async {
    return listenToAddingTo(
        ref: await _getReferenceOfNode(subNodePath),
        onAdd: onAdd,
        onDone: onDone,
        onError: onError);
  }

  void listenToAddingTo({
    required DatabaseReference ref,
    required Function(T) onAdd,
    void Function()? onDone,
    void Function(Object, StackTrace)? onError,
  }) async {
    return _listenTo(
      ref: ref,
      listenToAnyChange: false,
      onNewUpdate: onAdd,
      onDone: onDone,
      onError: onError,
    );
  }

  void _listenTo({
    required DatabaseReference ref,
    required bool listenToAnyChange,
    required Function(T) onNewUpdate,
    void Function()? onDone,
    void Function(Object, StackTrace)? onError,
  }) async {
    onData(event) => (event) {
      Map<String, dynamic>? map;
      try {
        var snapshot = event.snapshot;
        map = Map.from(snapshot.value as Map);
        onNewUpdate(mappable.fromMap(map));
      } catch (e) {
        Logs.print(
              () => '***** FirebaseDatabaseOp.setOnChildAddedListener() **** '
              'args(into: ${ref.path}) \n'
              'result: $map \n'
              '$e',
        );
      }
    };

    if (listenToAnyChange) {
      ref.onChildChanged.listen(onData, onDone: onDone, onError: onError);
    } else {
      ref.onChildAdded.listen(onData, onDone: onDone, onError: onError);
    }
  }

  @override
  void removeListeners({required String? subNodePath}) async {
    var ref = await _getReferenceOfNode(subNodePath);
    removeListenersOf(ref: ref);
  }

  void removeListenersOf({required DatabaseReference ref}) {
    ref.onChildChanged.listen(null);
    ref.onChildAdded.listen(null);
  }

  //----------------------------------------------------------------------------

  @override
  Future<Response<bool>> clear() async {
    try {
      bool suc = true;
      var ref = await databaseReference;
      await ref.remove().onError((error, stackTrace) => suc = false);
      Logs.print(() => 'FirebaseDatabaseOp.clear() ---> success:: $suc');
      return Response.success(data: suc);
    } catch (e) {
      Logs.print(() => 'FirebaseDatabaseOp.clear() ---> Exception:: $e');
      return Response.failed(
        error: StringSet(e.toString()),
        connectionFailed: true,
      );
    }
  }

  @override
  Future<Response<bool>> removeNode({required String subNodePath}) async {
    var ref = await _getReferenceOfNode(subNodePath);
    try {
      bool deleted = true;
      await ref.remove().onError((error, stackTrace) => deleted = false);
      Logs.print(() => 'FirebaseDatabaseOp.removeNode(ref: ${ref.path}) ---> deleted:: $deleted');
      return Response.success(data: deleted);
    } catch (e) {
      Logs.print(() => 'FirebaseDatabaseOp.removeNode(ref: ${ref.path}) ---> Exception:: $e');
      return Response.failed(
        error: StringSet(e.toString()),
        connectionFailed: true,
      );
    }
  }

  @override
  void listenToAddingSpecific<N>({required String? subNodePath, required Function(N p1) onAdd, void Function()? onDone, void Function(Object p1, StackTrace p2)? onError}) {
    throw 'implement listenToAddingSpecific';
  }

  @override
  void listenToChangesSpecific<N>({required String? subNodePath, required Function(N p1) onChange, void Function()? onDone, void Function(Object p1, StackTrace p2)? onError}) {
    throw 'implement listenToChangesSpecific';
  }
}
