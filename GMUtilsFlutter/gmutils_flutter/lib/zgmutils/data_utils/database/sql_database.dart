import 'dart:async';

//yaml -> path:
import 'package:path/path.dart';

//yaml -> sqflite:
import 'package:sqflite/sqflite.dart';

import '../../utils/logs.dart';
import '../../utils/mappable.dart';
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
  }) : assert(databaseName.isNotEmpty);

  //----------------------------------------------------------------------------

  List<SQLDatabaseTable> get tables;

  List<SQLInstruction> onUpgrade(int oldVer, int newVer) => [];

  //----------------------------------------------------------------------------

  Future<Database> get database async {
    _database ??= await openDatabase(
      join(await getDatabasesPath(), '$databaseName.db'),
      onCreate: _onCreateDb,
      onUpgrade: _onUpgradeDb,
      version: version,
    );
    return _database!;
  }

  FutureOr<void> _onCreateDb(Database db, int version) async {
    var tablesLst = this.tables;
    assert(tablesLst.isNotEmpty);

    for (var table in tablesLst) {
      var sql = table.getSQLInstruction();
      try {
        await db.execute(sql);
      } catch (e1) {
        var alterSqls = table.onExecuteSqlError(table, e1);
        if (alterSqls == null) {
          Logs.print(() =>
              'SQLDatabase.onCreateDb ----> EXCEPTION@execute($sql): $e1');
          rethrow;
        }
        //
        else {
          for (var sql in alterSqls) {
            try {
              await db.execute(sql.getSQLInstruction());
            } catch (e2) {
              Logs.print(() =>
                  'SQLDatabase.onCreateDb--> after onExecuteSqlError ----> EXCEPTION@execute(${sql.getSQLInstruction()}): $e2');
              rethrow;
            }
          }
        }
      }
    }
  }

  FutureOr<void> _onUpgradeDb(
    Database db,
    int oldVersion,
    int newVersion,
  ) async {
    if (newVersion <= oldVersion) return;

    var instructions = onUpgrade(oldVersion, newVersion);
    for (var ins in instructions) {
      try {
        await db.execute(ins.getSQLInstruction());
      } catch (e1) {
        var alterSqls = ins.onExecuteSqlError(ins, e1);
        if (alterSqls == null) {
          Logs.print(() =>
              'SQLDatabase._onUpgradeDb ----> EXCEPTION@execute(${ins.getSQLInstruction()}): $e1');
          rethrow;
        } else {
          for (var sql in alterSqls) {
            try {
              await db.execute(sql.getSQLInstruction());
            } catch (e2) {
              Logs.print(() =>
                  'SQLDatabase.onCreateDb--> after onExecuteSqlError ----> EXCEPTION@execute(${sql.getSQLInstruction()}): $e2');
              rethrow;
            }
          }
        }
      }
    }
  }

  //----------------------------------------------------------------------------

  dynamic newTransaction({
    required String tableName,
    required Future<dynamic> Function(SQLDatabaseTable) task,
  }) async {
    SQLDatabaseTable table = await newTableInstance(tableName: tableName);
    final r = await task(table);
    table.setDatabase(null);
    return r;
  }

  ///it's preferred to use newTransaction(..)
  Future<SQLDatabaseTable> newTableInstance({required String tableName}) async {
    Database _database;
    try {
      _database = await database.timeout(const Duration(seconds: 1));
    } catch (e) {
      throw 'database may not created yet';
    }

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
