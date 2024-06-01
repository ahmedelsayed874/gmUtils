import 'package:sqflite/sqflite.dart';

import 'page_info.dart';
import 'sql_instructions.dart';

abstract class SQLDatabaseTable extends SQLInstruction {
  SQLDatabaseTable({
    required String tableName,
  }) : super(tableName);

  List<SQLTableColumn> get columns;

  List<String>? get primaryColumns => null;

  @override
  String getSQLInstruction() {
    String columnsStr = '';
    for (var element in columns) {
      if (columnsStr.isNotEmpty) columnsStr += ", ";
      columnsStr += element.getSQLInstruction();
    }

    String primaryColumnsStr = '';
    if (primaryColumns != null && primaryColumns?.isNotEmpty == true) {
      if (columnsStr.contains(SQLTableColumn.CONSTRAINTS_PRIMARY_KEY)) {
        throw Exception(
            'this table "$tableName" defines PRIMARY KEY in two places');
      }

      primaryColumnsStr = ', ${SQLTableColumn.CONSTRAINTS_PRIMARY_KEY} (';
      final length = primaryColumnsStr.length;
      for (var element in primaryColumns!) {
        if (primaryColumnsStr.length > length) primaryColumnsStr += ", ";
        primaryColumnsStr += element;
      }
      primaryColumnsStr += ')';
    }

    return 'CREATE TABLE $tableName ($columnsStr$primaryColumnsStr)';
  }

  static SQLDatabaseTable obj(
      {required String tableName,
      required List<SQLTableColumn> columns,
      required List<String>? primaryColumns}) {
    return SQLDatabaseTableImpl(
      tableName,
      columns_: columns,
      primaryColumns_: primaryColumns,
    );
  }

  //----------------------------------------------------------------------------

  Future<Database> Function()? _database;

  void setDatabase(Future<Database> Function() database) => _database = database;

  Future<Database> get database async {
    if (_database == null) {
      throw 'You have to set Database before starting any transaction';
    }

    try {
      var db = await _database!.call().timeout(const Duration(seconds: 1));
      return db;
    } catch (e) {
      throw 'database is not created yet';
    }
  }

  //----------------------------------------------------------------------------

  //region sql: insert
  Future<int> doInsert({
    required Map<String, dynamic> data,
    ConflictAlgorithm conflictAlgorithm = ConflictAlgorithm.replace,
  }) async {
    var db = await database;
    return await db.insert(
      tableName,
      data,
      conflictAlgorithm: conflictAlgorithm,
    );
  }

  Future<List<int>> doInsertMultiple({
    required List<Map<String, dynamic>> data,
    ConflictAlgorithm conflictAlgorithm = ConflictAlgorithm.replace,
  }) async {
    var db = await database;
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
    required int length,
    required Map<String, dynamic> Function(int index) data,
    ConflictAlgorithm conflictAlgorithm = ConflictAlgorithm.replace,
  }) async {
    var db = await database;
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
  }

//endregion

  //----------------------------------------------------------------------------

  //region sql: select | retrieve
  Future<List<T>> doRetrieve<T>({
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
    List<String>? columns,
    required SQLConditions? whereCondition,
    String? orderOn,
    bool? isOrderAsc,
    PageInfo? pageInfo,
    bool? distinct,
    required T Function(Map<String, dynamic>) converter,
  }) async {
    String? orderBy;
    if (orderOn != null) {
      orderBy = orderOn;
      if (isOrderAsc != null) {
        orderBy += isOrderAsc == true ? ' ASC' : ' DESC';
      }
    }

    // Query the table for all
    var db = await database;
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
    List<String>? columns,
    required SQLConditions? whereCondition,
    required T Function(Map<String, dynamic>) converter,
  }) async {
    // Query the table for all
    var db = await database;
    final List<Map<String, dynamic>> maps = await db.query(
      tableName,
      columns: columns,
      where: whereCondition?.statement,
    );

    // Convert the List<Map<String, dynamic> into a List
    return maps.isEmpty ? null : converter(maps[0]);
  }

  //endregion

  //----------------------------------------------------------------------------

  //region sql: update
  Future<int> doUpdate({
    required Map<String, dynamic> data,
    required SQLConditions? whereCondition,
  }) async {
    // Get a reference to the database.
    var db = await database;

    // Update
    return await db.update(
      tableName,
      data,
      where: whereCondition?.statement,
    );
  }

  Future<List<int>> doUpdateMultiple({
    required int length,
    required Map<String, dynamic> Function(int index) data,
    required SQLConditions? Function(int index) whereCondition,
  }) async {
    var db = await database;
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
  }

  //endregion

  //----------------------------------------------------------------------------

  //region sql: delete
  Future<int> doDelete({
    required SQLConditions? whereCondition,
  }) async {
    // Get a reference to the database.
    var db = await database;

    // Remove the Dog from the database.
    return await db.delete(
      tableName,
      where: whereCondition?.statement,
    );
  }
//endregion
}

class SQLTableColumn extends SQLInstruction {
  static const TYPE_INTEGER_NUMBER = 'INTEGER';
  static const TYPE_REAL_NUMBER = 'REAL';
  static const TYPE_TEXT = 'TEXT';

  static const CONSTRAINTS_PRIMARY_KEY = 'PRIMARY KEY';
  static const CONSTRAINTS_PRIMARY_KEY_AUTOINCREMENT =
      'PRIMARY KEY autoincrement';
  static const CONSTRAINTS_FOREIGN_KEY = 'FOREIGN KEY';
  static const CONSTRAINTS_NOT_NULL = 'NOT NULL';
  static const CONSTRAINTS_UNIQUE = 'UNIQUE';
  static const CONSTRAINTS_DEFAULT = 'DEFAULT';

  final String name;
  final String type;
  final List<String>? constraints;

  SQLTableColumn({
    required this.name,
    required this.type,
    required this.constraints,
  }) : super('');

  @override
  String getSQLInstruction() {
    String constraintsStr = '';
    if (constraints != null && constraints!.isNotEmpty) {
      constraintsStr = ' ${constraints!.join(' ')}';
    }
    return '$name $type$constraintsStr';
  }

  static List<SQLTableColumn> createFromMap(
    Map map, {
    required FieldType? Function(String fieldName) onFieldTypeError,
    required List<String>? Function(String fieldName) constraints,
  }) {
    List<SQLTableColumn> cols = [];

    for (var element in map.entries) {
      var fieldName = element.key;
      String fieldType = '';

      if (element.value is String) {
        fieldType = SQLTableColumn.TYPE_TEXT;
      }
      //
      else if (element.value is int) {
        fieldType = SQLTableColumn.TYPE_INTEGER_NUMBER;
      }
      //
      else if (element.value is num) {
        fieldType = SQLTableColumn.TYPE_REAL_NUMBER;
      }
      //
      else if (element.value is double) {
        fieldType = SQLTableColumn.TYPE_REAL_NUMBER;
      }
      //
      else if (element.value is bool) {
        fieldType = SQLTableColumn.TYPE_INTEGER_NUMBER;
      }
      //
      else {
        var t = (onFieldTypeError.call(fieldName) ?? '').trim();
        if (t.isEmpty) {
          throw Exception(
              '"$fieldName" with type of "${element.value.runtimeType}" is not define');
        } else {
          fieldType = t;
        }
      }

      cols.add(SQLTableColumn(
        name: fieldName,
        type: fieldType,
        constraints: constraints.call(fieldName),
      ));
    }

    return cols;
  }
}

typedef FieldType = String;

//------------------------------------------------------------------------------

class SQLDatabaseTableImpl extends SQLDatabaseTable {
  final List<SQLTableColumn> columns_;
  final List<String>? primaryColumns_;

  SQLDatabaseTableImpl(
    String tableName, {
    required this.columns_,
    required this.primaryColumns_,
  }) : super(tableName: tableName);

  @override
  List<SQLTableColumn> get columns => columns_;

  @override
  List<String>? get primaryColumns => primaryColumns_;
}
