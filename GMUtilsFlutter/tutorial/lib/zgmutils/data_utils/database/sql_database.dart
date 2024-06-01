import 'dart:async';

//yaml -> path:
import 'package:path/path.dart';
//yaml -> sqflite:
import 'package:sqflite/sqflite.dart';

import '../../utils/logs.dart';
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
    _init();
  }

  Future<List<SQLDatabaseTable>> get tables;

  List<SQLInstruction> onUpgrade(int oldVer, int newVer) => [];

  List<SQLInstruction>? onExecuteSqlError(String source, SQLInstruction failedSql, error);

  void _init() async {
    _database = await openDatabase(
      join(await getDatabasesPath(), '$databaseName.db'),
      onCreate: (db, version) async {
        var tb = await tables;
        assert(tb.isNotEmpty);
        tb.forEach((element) async {
          var sql = element.getSQLInstruction();
          try {
            await db.execute(sql);
          } catch (e) {
            var alterSqls = onExecuteSqlError('onCreate', element, e);
            if (alterSqls == null) {
              Logs.print(() => ['************', e]);
              rethrow;
            } else {
              for (var sql in alterSqls) {
                try {
                  await db.execute(sql.getSQLInstruction());
                } catch (x) {
                  Logs.print(() => ['************', x]);
                  rethrow;
                }
              }
            }
          }
        });
      },
      onUpgrade: (db, oldVer, newVer) async {
        if (newVer > oldVer) {
          var instructions = onUpgrade(oldVer , newVer);
          for (var ins in instructions) {
            try {
              await db.execute(ins.getSQLInstruction());
            } catch (e) {
              var alterSqls = onExecuteSqlError('onUpgrade', ins, e);
              if (alterSqls == null) {
                Logs.print(() => ['************', e]);
                rethrow;
              } else {
                for (var sql in alterSqls) {
                  try {
                    await db.execute(sql.getSQLInstruction());
                  } catch (e) {
                    Logs.print(() => ['************', e]);
                    rethrow;
                  }
                }
              }
            }
          }
        }
      },
      version: version,
    );
  }

  Future<Database> get database async {
    while (_database == null) {
      await Future.delayed(const Duration(milliseconds: 300));
    }
    return _database!;
  }

  //----------------------------------------------------------------------------

  //reg ion sql: insert
  /*Future<int> doInsert({
    required String tableName,
    required Map<String, dynamic> data,
    ConflictAlgorithm conflictAlgorithm = ConflictAlgorithm.replace,
  }) async {
    final db = await database;
    return await db.insert(
      tableName,
      data,
      conflictAlgorithm: conflictAlgorithm,
    );
  }

  Future<List<int>> doInsertMultiple({
    required String tableName,
    required List<Map<String, dynamic>> data,
    ConflictAlgorithm conflictAlgorithm = ConflictAlgorithm.replace,
  }) async {
    final db = await database;

    List<int> ids = [];

    for (var singleData in data) {
      var id = await db.insert(
        tableName,
        singleData,
        conflictAlgorithm: conflictAlgorithm,
      );

      ids.add(id);
    }

    return ids;
  }

  Future<List<int>> doInsertMultiple2({
    required String tableName,
    required int length,
    required Map<String, dynamic> Function(int index) data,
    ConflictAlgorithm conflictAlgorithm = ConflictAlgorithm.replace,
  }) async {
    final db = await database;

    List<int> ids = [];

    for (int i = 0; i < length; i++) {
      var singleData = data(i);

      var id = await db.insert(
        tableName,
        singleData,
        conflictAlgorithm: conflictAlgorithm,
      );

      ids.add(id);
    }

    return ids;
  }*/
  //end region

  //----------------------------------------------------------------------------

  //reg ion sql: select | retrieve
  /*Future<List<T>> doRetrieve<T>({
    required String tableName,
    List<String>? columns,
    required SQLConditions? whereCondition,
    String? orderOn,
    bool? isOrderAsc,
    PageInfo? pageInfo,
    bool? distinct,
    required T Function(Map<String, dynamic>) converter,
  }) async {
    return await _doRetrieve(
        returnSet: false,
        tableName: tableName,
        columns: columns,
        whereCondition: whereCondition,
        orderOn: orderOn,
        isOrderAsc: isOrderAsc,
      pageInfo: pageInfo,
        converter: converter,
      distinct: distinct,
    );
  }

  Future<Set<T>> doRetrieve2<T>({
    required String tableName,
    List<String>? columns,
    required SQLConditions? whereCondition,
    String? orderOn,
    bool? isOrderAsc,
    PageInfo? pageInfo,
    bool? distinct,
    required T Function(Map<String, dynamic>) converter,
  }) async {
    return await _doRetrieve(
      returnSet: true,
      tableName: tableName,
      columns: columns,
      whereCondition: whereCondition,
      orderOn: orderOn,
      isOrderAsc: isOrderAsc,
      pageInfo: pageInfo,
      converter: converter,
      distinct: distinct,
    );
  }

  Future<dynamic> _doRetrieve<T>({
    bool returnSet = false,
    required String tableName,
    List<String>? columns,
    required SQLConditions? whereCondition,
    String? orderOn,
    bool? isOrderAsc,
    PageInfo? pageInfo,
    bool? distinct,
    required T Function(Map<String, dynamic>) converter,
  }) async {
    final db = await database;

    String? orderBy;
    if (orderOn != null) {
      orderBy = orderOn;
      if (isOrderAsc != null) {
        orderBy += isOrderAsc == true ? ' ASC' : ' DESC';
      }
    }

    // Query the table for all
    final List<Map<String, dynamic>> maps = await db.query(
        tableName,
        columns: columns,
        where: whereCondition?.statement,
        orderBy: orderBy,
        limit: pageInfo?.limit,
        offset: pageInfo?.offset,
      distinct: distinct,
    );

    if (returnSet) {
      Set<T> set = {};
      for (int i = 0; i < maps.length; i++) {
        set.add(converter(maps[i]));
      }
      return set;
    } else {
      // Convert the List<Map<String, dynamic> into a List
      return List.generate(maps.length, (i) {
        return converter(maps[i]);
      });
    }
  }

  Future<T?> doRetrieveSingle<T>({
    required String tableName,
    List<String>? columns,
    required SQLConditions? whereCondition,
    required T Function(Map<String, dynamic>) converter,
  }) async {
    final db = await database;

    // Query the table for all
    final List<Map<String, dynamic>> maps = await db.query(
        tableName,
        columns: columns,
        where: whereCondition == null ? null : whereCondition.statement,
    );

    // Convert the List<Map<String, dynamic> into a List
    return maps.isEmpty ? null : converter(maps[0]);
  }*/
  //end region

  //----------------------------------------------------------------------------

  //reg ion sql: update
  /*Future<int> doUpdate({
    required String tableName,
    required Map<String, dynamic> data,
    required SQLConditions? whereCondition,
  }) async {
    // Get a reference to the database.
    final db = await database;

    // Update
    return await db.update(
      tableName,
      data,
      where: whereCondition?.statement,
    );
  }

  Future<List<int>> doUpdateMultiple({
    required String tableName,
    required int length,
    required Map<String, dynamic> Function(int index) data,
    required SQLConditions? Function(int index) whereCondition,
  }) async {
    final db = await database;

    List<int> ids = [];

    for (int i = 0; i < length; i++) {
      var id = await db.update(
        tableName,
        data(i),
        where: whereCondition(i)?.statement,
      );

      ids.add(id);
    }

    return ids;
  }*/
  //end region

  //----------------------------------------------------------------------------

  //reg ion sql: delete
  /*Future<int> doDelete({
    required String tableName,
    required SQLConditions? whereCondition,
  }) async {
    // Get a reference to the database.
    final db = await database;

    // Remove the Dog from the database.
    return await db.delete(
      tableName,
      where: whereCondition == null ? null : whereCondition.statement,
    );
  }*/
//end region

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
  Future<List<SQLDatabaseTable>> get tables async => [ await dictionaryTable ];

  Future<SQLDatabaseTable> get dictionaryTable async {
    var t = SQLDatabaseTable.obj(
      tableName: _TABLE_NAME_DICTIONARY,
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
    );

    t.setDatabase(() => database);

    return t;
  }

  Future<int> insert({
    required Map<String, dynamic> data,
    ConflictAlgorithm conflictAlgorithm = ConflictAlgorithm.replace,
  }) async {
    var t = await dictionaryTable;
    return t.doInsert(
      //tableName: _TABLE_NAME_DICTIONARY,
      data: data,
      conflictAlgorithm: conflictAlgorithm,
    );
  }

  Future<List<T>> retrieve<T>({
    required T Function(Map<String, dynamic>) converter,
  }) async {
    var t = await dictionaryTable;
    return t.doRetrieve(
      //tableName: _TABLE_NAME_DICTIONARY,
      converter: converter,
      whereCondition: null,
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
    var t = await dictionaryTable;
    return t.doRetrieve(
      //tableName: _TABLE_NAME_DICTIONARY,
      columns: columns,
      whereCondition: whereCondition,
      orderOn: orderOn,
      isOrderAsc: isOrderAsc,
      pageInfo: pageInfo,
      converter: converter,
    );
  }

  Future<int> update({
    required Map<String, dynamic> data,
    required SQLConditions? whereCondition,
  }) async {
    var t = await dictionaryTable;
    return t.doUpdate(
      //tableName: _TABLE_NAME_DICTIONARY,
      data: data,
      whereCondition: whereCondition,
    );
  }

  Future<int> delete({
    required SQLConditions? whereCondition,
  }) async {
    var t = await dictionaryTable;
    return t.doDelete(
      //tableName: _TABLE_NAME_DICTIONARY,
      whereCondition: whereCondition,
    );
  }

  @override
  List<SQLInstruction>? onExecuteSqlError(String source, SQLInstruction failedSql, error) => null;
}
