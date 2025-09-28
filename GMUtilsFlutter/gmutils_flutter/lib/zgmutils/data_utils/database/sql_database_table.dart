import 'package:sqflite/sqflite.dart';

import '../../utils/logs.dart';
import '../utils/mappable.dart';
import '../../utils/collections/pairs.dart';
import 'page_info.dart';
import 'sql_instructions.dart';

abstract class SQLDatabaseTable<T> extends SQLInstruction {
  final Mappable<T> mapper;

  SQLDatabaseTable({
    required String tableName,
    required this.mapper,
  }) : super(tableName);

  List<SQLTableColumn> get columns;

  List<String>? get primaryColumns => null;

  @override
  String getSQLInstruction() {
    return SQLCreateTable.obj(
      tableName: tableName,
      columns: columns,
      primaryColumns: primaryColumns,
    ).getSQLInstruction();
  }

  static SQLDatabaseTable obj<T>({
    required String tableName,
    required Mappable<T> mappable,
    required List<SQLTableColumn> columns,
    required List<String>? primaryColumns,
  }) {
    return SQLDatabaseTableImpl(
      tableName: tableName,
      mappable: mappable,
      columns_: columns,
      primaryColumns_: primaryColumns,
    );
  }

  //----------------------------------------------------------------------------

  Database? _database;

  void setDatabase(Database? db) => _database = db;

  Database get database {
    if (_database == null) {
      throw 'must use setDatabase before executing any transaction';
    }

    return _database!;
  }

  //----------------------------------------------------------------------------

  //region sql: insert
  Future<int> insert({
    required T data,
    ConflictAlgorithm conflictAlgorithm = ConflictAlgorithm.replace,
    CustomValueConverter? customValueConverter,
  }) =>
      insertCustom(
        customData: _columnsVsValues(
          data: data,
          customValueConverter: customValueConverter,
        ),
        conflictAlgorithm: conflictAlgorithm,
      );

  Future<int> insertCustom({
    required Map<String, dynamic> customData,
    ConflictAlgorithm conflictAlgorithm = ConflictAlgorithm.replace,
  }) async {
    return await database.insert(
      tableName,
      customData,
      conflictAlgorithm: conflictAlgorithm,
    );
  }

  Future<List<int>> insertMultiple({
    required List<T> data,
    ConflictAlgorithm conflictAlgorithm = ConflictAlgorithm.replace,
    CustomValueConverter? customValueConverter,
    void Function(int)? progress,
  }) =>
      insertMultipleCustom(
        customData: data
            .map(
              (e) => _columnsVsValues(
                data: e,
                customValueConverter: customValueConverter,
              ),
            )
            .toList(),
        conflictAlgorithm: conflictAlgorithm,
        progress: progress,
      );

  Future<List<int>> insertMultipleCustom({
    required List<Map<String, dynamic>> customData,
    ConflictAlgorithm conflictAlgorithm = ConflictAlgorithm.replace,
    void Function(int)? progress,
  }) async {
    List<int> ids = [];

    if (progress != null && customData.isEmpty) {
      progress(100);
    }

    for (int i = 0; i < customData.length; i++) {
      var singleData = customData[i];

      var id = await database.insert(
        tableName,
        singleData,
        conflictAlgorithm: conflictAlgorithm,
      );

      ids.add(id);

      if (progress != null) {
        progress((i + 1) % customData.length);
      }
    }

    return ids;
  }

  Map<String, dynamic> _columnsVsValues({
    required T data,
    required CustomValueConverter? customValueConverter,
  }) {
    final customData = mapper.toMap(data);

    /*
    if (customValueConverter != null) {
      Map<String, dynamic> newData = {};

      for (var entry in customData.entries) {
        if (entry.value != null) {
          var newValue = customValueConverter(entry.key, entry.value);
          if (newValue != null) {
            newData[entry.key] = newValue.value;
          }
        }
      }

      customData.addAll(newData);
    }
     */

    Map<String, dynamic> newData = {};
    for (var entry in customData.entries) {
      if (entry.value != null) {
        if (entry.value is bool) {
          var newValue = entry.value ? 1 : 0;
          newData[entry.key] = newValue;
        }
        //
        else {
          var newValue = customValueConverter?.call(entry.key, entry.value);
          if (newValue != null) {
            newData[entry.key] = newValue.value;
          }
        }
      }
    }
    customData.addAll(newData);

    return customData;
  }

//endregion

  //----------------------------------------------------------------------------

  //region sql: select | retrieve
  Future<List<T>> retrieve({
    List<String>? moreColumns,
    required SQLConditions? whereCondition,
    String? orderOn,
    bool? isOrderAsc,
    PageInfo? pageInfo,
    bool? distinct,
    CustomValueConverter? customValueConverter,
  }) {
    List<String>? customColumns;
    if (moreColumns?.isNotEmpty == true) {
      customColumns = this.columns.map((c) => c.name).toList();
      customColumns.addAll(moreColumns!);
    }

    return retrieveCustom(
      customColumns: customColumns,
      whereCondition: whereCondition,
      orderOn: orderOn,
      isOrderAsc: isOrderAsc,
      pageInfo: pageInfo,
      distinct: distinct,
      converter: (map) => _dataConverter(
        data: map,
        customValueConverter: customValueConverter,
      ),
    );
  }

  Future<List<Ct>> retrieveCustom<Ct>({
    required List<String>? customColumns,
    required SQLConditions? whereCondition,
    String? orderOn,
    bool? isOrderAsc,
    PageInfo? pageInfo,
    bool? distinct,
    required Ct Function(Map<String, dynamic>) converter,
  }) async {
    return await _retrieve(
      returnSet: false,
      customColumns: customColumns,
      whereCondition: whereCondition,
      orderOn: orderOn,
      isOrderAsc: isOrderAsc,
      pageInfo: pageInfo,
      distinct: distinct,
      converter: converter,
    );
  }

  Future<Set<T>> retrieveSet({
    List<String>? moreColumns,
    required SQLConditions? whereCondition,
    String? orderOn,
    bool? isOrderAsc,
    PageInfo? pageInfo,
    bool? distinct,
    CustomValueConverter? customValueConverter,
  }) {
    List<String>? customColumns;
    if (moreColumns?.isNotEmpty == true) {
      customColumns = this.columns.map((c) => c.name).toList();
      customColumns.addAll(moreColumns!);
    }

    return retrieveCustomSet(
      customColumns: customColumns,
      whereCondition: whereCondition,
      orderOn: orderOn,
      isOrderAsc: isOrderAsc,
      pageInfo: pageInfo,
      distinct: distinct,
      converter: (map) => _dataConverter(
        data: map,
        customValueConverter: customValueConverter,
      ),
    );
  }

  Future<Set<Ct>> retrieveCustomSet<Ct>({
    required List<String>? customColumns,
    required SQLConditions? whereCondition,
    String? orderOn,
    bool? isOrderAsc,
    PageInfo? pageInfo,
    bool? distinct,
    required Ct Function(Map<String, dynamic>) converter,
  }) async {
    return await _retrieve(
      returnSet: true,
      customColumns: customColumns,
      whereCondition: whereCondition,
      orderOn: orderOn,
      isOrderAsc: isOrderAsc,
      pageInfo: pageInfo,
      distinct: distinct,
      converter: converter,
    );
  }

  Future<dynamic> _retrieve<T2>({
    bool returnSet = false,
    List<String>? customColumns,
    required SQLConditions? whereCondition,
    String? orderOn,
    bool? isOrderAsc,
    PageInfo? pageInfo,
    bool? distinct,
    required T2 Function(Map<String, dynamic>) converter,
  }) async {
    String? orderBy;
    if (orderOn != null) {
      orderBy = orderOn;
      if (isOrderAsc != null) {
        orderBy += isOrderAsc == true ? ' ASC' : ' DESC';
      }
    }

    // Query the table for all

    final List<Map<String, dynamic>> maps = await database.query(
      tableName,
      columns: customColumns,
      where: whereCondition?.statement,
      orderBy: orderBy,
      limit: pageInfo?.limit,
      offset: pageInfo?.offset,
      distinct: distinct,
    );

    if (returnSet) {
      Set<T2> set = {};
      for (int i = 0; i < maps.length; i++) {
        set.add(converter(maps[i]));
      }
      return set;
    }
    //
    else {
      // Convert the List<Map<String, dynamic> into a List
      return List.generate(maps.length, (i) {
        return converter(maps[i]);
      });
    }
  }

  //------------------------------------------------------------

  Future<T?> retrieveSingle({
    required SQLConditions? whereCondition,
    CustomValueConverter? customValueConverter,
  }) =>
      retrieveCustomSingle(
        customColumns: null,
        whereCondition: whereCondition,
        converter: (map) => _dataConverter(
          data: map,
          customValueConverter: customValueConverter,
        ),
      );

  Future<Ct?> retrieveCustomSingle<Ct>({
    required List<String>? customColumns,
    required SQLConditions? whereCondition,
    required Ct Function(Map<String, dynamic>) converter,
  }) async {
    // Query the table for all

    final List<Map<String, dynamic>> maps = await database.query(
      tableName,
      columns: customColumns,
      where: whereCondition?.statement,
    );

    // Convert the List<Map<String, dynamic> into a List
    return maps.isEmpty ? null : converter(maps[0]);
  }

  //------------------------------------------------------------

  T _dataConverter({
    required Map<String, dynamic> data,
    required CustomValueConverter? customValueConverter,
  }) {
    Map<String, dynamic> mutableData = Map.from(data);

    if (customValueConverter != null) {
      Map<String, dynamic> newData = {};

      for (var entry in data.entries) {
        if (entry.value != null) {
          var newValue = customValueConverter(entry.key, entry.value);
          if (newValue != null) {
            newData[entry.key] = newValue.value;
          }
        }
      }

      mutableData.addAll(newData);
    }

    try {
      return mapper.fromMap(mutableData);
    } catch (e, s) {
      Logs.print(() => 'SQLDatabaseTable._dataConverter '
          '---> EXCEPTION at decoding data (($e)) '
          '----> StackTrace:: $s');
      rethrow;
    }
  }

  //endregion

  //----------------------------------------------------------------------------

  //region sql: update
  Future<int> update({
    required T data,
    required SQLConditions? whereCondition,
    CustomValueConverter? customValueConverter,
  }) =>
      updateCustom(
        data: _columnsVsValues(
          data: data,
          customValueConverter: customValueConverter,
        ),
        whereCondition: whereCondition,
      );

  Future<int> updateCustom({
    required Map<String, dynamic> data,
    required SQLConditions? whereCondition,
  }) async {
    // Update
    return await database.update(
      tableName,
      data,
      where: whereCondition?.statement,
    );
  }

  Future<List<int>> updateMultiple({
    required List<Pair<T, SQLConditions?>> data,
    CustomValueConverter? customValueConverter,
  }) async {
    return await updateMultiple2(
      length: data.length,
      data: (i) => _columnsVsValues(
        data: data[i].value1,
        customValueConverter: customValueConverter,
      ),
      whereCondition: (i) => data[i].value2,
    );
  }

  Future<List<int>> updateMultiple2({
    required int length,
    required Map<String, dynamic> Function(int index) data,
    required SQLConditions? Function(int index) whereCondition,
  }) async {
    List<int> ids = [];

    for (int i = 0; i < length; i++) {
      var id = await database.update(
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
  Future<int> delete({
    required SQLConditions? whereCondition,
  }) async {
    // Remove the Dog from the database.
    return await database.delete(
      tableName,
      where: whereCondition?.statement,
    );
  }
//endregion
}

//------------------------------------------------------------------------------

class NewValue {
  final dynamic value;

  NewValue(this.value);

  @override
  String toString() {
    return 'NewValue{value: $value}';
  }
}

typedef CustomValueConverter = NewValue? Function(
  String columnName,
  dynamic value,
);

//------------------------------------------------------------------------------

class SQLDatabaseTableImpl<T> extends SQLDatabaseTable {
  final List<SQLTableColumn> columns_;
  final List<String>? primaryColumns_;

  SQLDatabaseTableImpl({
    required String tableName,
    required Mappable<T> mappable,
    required this.columns_,
    required this.primaryColumns_,
  }) : super(tableName: tableName, mapper: mappable);

  @override
  List<SQLTableColumn> get columns => columns_;

  @override
  List<String>? get primaryColumns => primaryColumns_;
}
