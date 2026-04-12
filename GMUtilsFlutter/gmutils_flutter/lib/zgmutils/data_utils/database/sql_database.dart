import 'dart:async';

//yaml -> path:
import 'package:path/path.dart';
//yaml -> sqflite:
import 'package:sqflite/sqflite.dart';

import '../../utils/logs.dart';
import '../utils/mappable.dart';
import 'page_info.dart';
import 'sql_database_table.dart';
import 'sql_instructions.dart';

abstract class SQLDatabase {
  final String databaseName;
  final int version;
  Database? _database;

  SQLDatabase({
    required this.databaseName,
    required this.version,
  }) : assert(databaseName.isNotEmpty) {
    database.then(
      (db) => Logs.print(
        () => 'database ($databaseName, version: $version) '
            'created in constructor',
      ),
    );
  }

  //----------------------------------------------------------------------------

  List<SQLDatabaseTable> get tables;

  List<SQLInstruction> onUpgrade(
    int oldVer,
    int newVer,
    void Function(bool) setRecreateDb,
  ) =>
      [];

  //----------------------------------------------------------------------------

  bool _isCreateBusy = false;
  List<Completer<Database>>? _completers;

  Future<Database> get database async {
    if (_isCreateBusy) {
      var c = Completer<Database>();

      _completers ??= [];
      _completers?.add(c);

      return c.future;
    }

    if (_database == null || _database?.isOpen != true) {
      _isCreateBusy = true;

      _database = await openDatabase(
        join(await getDatabasesPath(), '$databaseName.db'),
        onCreate: _onCreateDb,
        onUpgrade: _onUpgradeDb,
        version: version,
      );

      _isCreateBusy = false;
      _completers?.forEach((completer) => completer.complete(_database));
    }

    return _database!;
  }

  //----------------------------------------------------------------------------

  FutureOr<void> _onCreateDb(Database db, int version) async {
    Logs.printMethod(extraInfo: () => 'version: $version');

    var tablesLst = tables;
    assert(tablesLst.isNotEmpty);

    for (var table in tablesLst) {
      var sql = table.getSQLInstruction();
      try {
        await db.execute(sql);
      } catch (e1) {
        _onExecuteSqlError(
          db: db,
          method: "onCreateDb",
          sql: sql,
          failedSql: table,
          executeSqlError: e1,
        );
      }
    }
  }

  FutureOr<void> _onUpgradeDb(
    Database db,
    int oldVersion,
    int newVersion,
  ) async {
    Logs.printMethod(extraInfo: () => 'newVersion: $newVersion, oldVersion: $oldVersion',);

    if (newVersion <= oldVersion) return;

    bool recreateDb = true;

    var instructions = onUpgrade(
      oldVersion,
      newVersion,
      (b) => recreateDb = b,
    );

    for (var ins in instructions) {
      try {
        await db.execute(ins.getSQLInstruction());
      } catch (e1) {
        _onExecuteSqlError(
          db: db,
          method: "onUpgradeDb",
          sql: ins.getSQLInstruction(),
          failedSql: ins,
          executeSqlError: e1,
        );
      }
    }

    if (recreateDb) await _onCreateDb(db, newVersion);
  }

  void _onExecuteSqlError({
    required Database db,
    required String method,
    required String sql,
    required SQLInstruction failedSql,
    required executeSqlError,
  }) async {
    List<SQLInstruction>? alterSqls = onExecuteSqlError(
      method: method,
      failedSql: failedSql,
      executeSqlError: executeSqlError,
    );

    if (alterSqls == null) {
      Logs.print(
        () => 'SQLDatabase.$method '
            '----> EXCEPTION@execute($sql): $executeSqlError',
      );

      throw executeSqlError;
    }
    //
    else {
      for (var sql in alterSqls) {
        try {
          await db.execute(sql.getSQLInstruction());
        } catch (e2) {
          Logs.print(
            () => 'SQLDatabase.$method '
                '----> after onExecuteSqlError '
                '----> EXCEPTION@execute(${sql.getSQLInstruction()}): $e2',
          );

          throw executeSqlError;
        }
      }
    }
  }

  List<SQLInstruction>? onExecuteSqlError({
    required String method,
    required SQLInstruction failedSql,
    required executeSqlError,
  }) {
    throw UnimplementedError(
      'implement this to handle the following error of "$method": "$executeSqlError" '
      'on table "${failedSql.tableName}" '
      'which occurred due to executing ${failedSql.getSQLInstruction()}',
    );
  }

  //----------------------------------------------------------------------------

  dynamic newTransaction({
    required String tableName,
    required Future<dynamic> Function(SQLDatabaseTable) task,
  }) async {
    SQLDatabaseTable table = await tableOf(tableName: tableName);
    final r = await task(table);
    table.dispose();
    return r;
  }

  int _tries = 0;

  ///it's preferred to use newTransaction(..)
  Future<SQLDatabaseTable> tableOf({required String tableName}) async {
    if (_tries <= 0) _tries = 3;

    Database? _database;
    while (_tries-- > 0 && _database == null) {
      try {
        _database = await database.timeout(const Duration(seconds: 5));
      } catch (e) {
        if (_tries == 0) {
          throw 'database may not created yet after 3 tries';
        }
      }
    }

    assert(_database != null);

    try {
      var t = tables.firstWhere((e) => e.tableName == tableName);
      t.setDatabase(_database);
      return t;
    } catch (e) {
      throw 'no table match with provided table name of $tableName';
    }
  }
}

//******************************************************************************

class DictionaryDatabase extends SQLDatabase {
  static const _TABLE_NAME_DICTIONARY = 'dictionary_table';
  static const COLUMN_NAME_WORD = 'word';
  static const COLUMN_NAME_MEANING = 'meaning';

  DictionaryDatabase()
      : super(
          databaseName: 'dictionary_test',
          version: 1,
        );

  @override
  List<SQLDatabaseTable> get tables => [
        SQLDatabaseTable.obj(
          tableName: _TABLE_NAME_DICTIONARY,
          mappable: VoidMapper(),
          columns: [
            SQLTableColumn(
                name: COLUMN_NAME_WORD,
                type: SQLTableColumn.TYPE_TEXT,
                constraints: null),
            SQLTableColumn(
                name: COLUMN_NAME_MEANING,
                type: SQLTableColumn.TYPE_TEXT,
                constraints: null),
          ],
          primaryColumns: null,
        ),
      ];

  Future<int> insert({
    required Map<String, dynamic> data,
    ConflictAlgorithm conflictAlgorithm = ConflictAlgorithm.replace,
  }) async {
    /*var table = await newTableInstance(tableName: _TABLE_NAME_DICTIONARY);
    return table.insertCustom(
      customData: data,
      conflictAlgorithm: conflictAlgorithm,
    );*/

    // OR //

    return newTransaction(
      tableName: _TABLE_NAME_DICTIONARY,
      task: (t) => t.insertCustom(
        customData: data,
        conflictAlgorithm: conflictAlgorithm,
      ),
    );
  }

  Future<List<T>> retrieve<T>({
    required T Function(Map<String, dynamic>) converter,
  }) async {
    return newTransaction(
      tableName: _TABLE_NAME_DICTIONARY,
      task: (t) => t.retrieveCustom(
        customColumns: null,
        converter: converter,
        whereCondition: null,
      ),
    );
  }

  Future<List<T>> retrieveCustom<T>({
    List<String>? columns,
    required SQLConditions? whereCondition,
    String? orderOn,
    bool? isOrderAsc,
    PageInfo? pageInfo,
    required T Function(Map<String, dynamic>) converter,
  }) async {
    return newTransaction(
      tableName: _TABLE_NAME_DICTIONARY,
      task: (t) => t.retrieveCustom(
        customColumns: columns,
        whereCondition: whereCondition,
        orderOn: orderOn,
        isOrderAsc: isOrderAsc,
        pageInfo: pageInfo,
        converter: converter,
      ),
    );
  }

  Future<int> update({
    required Map<String, dynamic> data,
    required SQLConditions? whereCondition,
  }) async {
    return newTransaction(
      tableName: _TABLE_NAME_DICTIONARY,
      task: (t) => t.update(
        data: data,
        whereCondition: whereCondition,
      ),
    );
  }

  Future<int> delete({
    required SQLConditions? whereCondition,
  }) async {
    return newTransaction(
      tableName: _TABLE_NAME_DICTIONARY,
      task: (t) => t.delete(
        whereCondition: whereCondition,
      ),
    );
  }
}
