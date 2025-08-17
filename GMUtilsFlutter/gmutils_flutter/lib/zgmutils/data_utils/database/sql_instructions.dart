abstract class SQLInstruction {
  final String tableName;

  SQLInstruction(this.tableName);

  String getSQLInstruction();

  List<SQLInstruction>? onExecuteSqlError(SQLInstruction failedSql, error) {
    throw UnimplementedError(
      'implement this to handle the following error: $error '
      'which occurred due to executing ${failedSql.getSQLInstruction()}',
    );
  }

  @override
  String toString() {
    return 'SQLInstruction = ${getSQLInstruction()}';
  }
}

//------------------------------------------------------------------------------

abstract class SQLCreateTable extends SQLInstruction {
  SQLCreateTable({
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
          'this table "$tableName" defines PRIMARY KEY in two places',
        );
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

  static SQLCreateTable obj({
    required String tableName,
    required List<SQLTableColumn> columns,
    required List<String>? primaryColumns,
  }) {
    return SQLCreateTableImpl(
      tableName,
      columns_: columns,
      primaryColumns_: primaryColumns,
    );
  }
}

typedef FieldType = String;

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
    required FieldType? Function(String fieldName, Type? valueType)?
        onFieldTypeError,
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
        var r = onFieldTypeError?.call(fieldName, element.value?.runtimeType);
        var t = (r ?? '').trim();
        if (t.isEmpty) {
          throw Exception(
            '"$fieldName" with type of "${element.value?.runtimeType}" is not define',
          );
        }
        //
        else {
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

class SQLCreateTableImpl extends SQLCreateTable {
  final List<SQLTableColumn> columns_;
  final List<String>? primaryColumns_;

  SQLCreateTableImpl(
    String tableName, {
    required this.columns_,
    required this.primaryColumns_,
  }) : super(tableName: tableName);

  @override
  List<SQLTableColumn> get columns => columns_;

  @override
  List<String>? get primaryColumns => primaryColumns_;
}

//------------------------------------------------------------------------------

class SQLDropTable extends SQLInstruction {
  SQLDropTable({
    required String tableName,
  }) : super(tableName);

  @override
  String getSQLInstruction() {
    return 'DROP TABLE $tableName';
  }
}

//------------------------------------------------------------------------------

class SQLInsert extends SQLInstruction {
  final List<SQLCellValue> values;

  SQLInsert({
    required String tableName,
    required this.values,
  }) : super(tableName);

  @override
  String getSQLInstruction() {
    String columns = '';
    String values = '';

    this.values.forEach((element) {
      if (columns.isNotEmpty) {
        columns += ', ';
        values += ', ';
      }

      columns += element.columnName;
      values += element.value;
    });

    return 'INSERT INTO $tableName($columns) VALUES($values)';
  }
}

class SQLCellValue {
  final String columnName;
  dynamic _value;

  SQLCellValue({required this.columnName, dynamic value}) {
    this._value = value;
  }

  String get value {
    if (_value is String) {
      return "'$_value'";
    } else {
      return _value;
    }
  }
}

//------------------------------------------------------------------------------

class SQLUpdate extends SQLInstruction {
  final List<SQLCellValue> values;
  final SQLConditions? conditions;

  SQLUpdate({
    required String tableName,
    required this.values,
    required this.conditions,
  }) : super(tableName);

  @override
  String getSQLInstruction() {
    String setStatement = '';
    values.forEach((element) {
      if (setStatement.isNotEmpty) setStatement += ', ';
      setStatement += '${element.columnName} = ${element.value}';
    });

    String whereClause = '';
    if (conditions != null) whereClause += ' WHERE ${conditions!.statement}';

    return 'UPDATE $tableName SET $setStatement$whereClause';
  }
}

class SQLConditions {
  String _statement = '';
  bool _setMethodIsLastUse = false;

  String get _exceptionMessage =>
      'you have to use \'set\' method before \'ana\' or \'or\' methods\ncurrent statement: $_statement';

  SQLConditions set(String columnName, String compareOperator, dynamic value) {
    if (_setMethodIsLastUse) throw Exception(_exceptionMessage);
    _setMethodIsLastUse = true;

    dynamic _value;
    if (value is String) {
      _value = "'$value'";
    } else {
      _value = value;
    }

    _statement += '$columnName $compareOperator $_value ';

    return this;
  }

  SQLConditions setBySQLConditions(SQLConditions sqlConditions) {
    if (_setMethodIsLastUse) throw Exception(_exceptionMessage);
    _setMethodIsLastUse = true;

    _statement += '(${sqlConditions.statement.trim()}) ';

    return this;
  }

  SQLConditions and() {
    if (!_setMethodIsLastUse) throw Exception(_exceptionMessage);
    _setMethodIsLastUse = false;
    _statement += 'AND ';
    return this;
  }

  SQLConditions or() {
    if (!_setMethodIsLastUse) throw Exception(_exceptionMessage);
    _setMethodIsLastUse = false;
    _statement += 'OR ';
    return this;
  }

  bool get mustAddRelationship => _setMethodIsLastUse;

  String get statement {
    if (!_setMethodIsLastUse) throw Exception(_exceptionMessage);
    if (_statement.isEmpty) throw Exception('you didn\'t set the statement');
    return _statement;
  }

  @override
  String toString() {
    return 'SQLConditions{$_statement}';
  }
}

//------------------------------------------------------------------------------

class SQLSelect extends SQLInstruction {
  final String selectingColumnNames;
  final SQLConditions? conditions;

  SQLSelect({
    required String tableName,
    this.selectingColumnNames = '*',
    this.conditions,
  })  : assert(selectingColumnNames.isNotEmpty),
        super(tableName);

  @override
  String getSQLInstruction() {
    String whereClause = '';
    if (conditions != null) whereClause += ' WHERE ${conditions!.statement}';
    return 'SELECT $selectingColumnNames FROM $tableName$whereClause';
  }
}

//------------------------------------------------------------------------------

class SQLDelete extends SQLInstruction {
  final SQLConditions? conditions;

  SQLDelete({
    required String tableName,
    this.conditions,
  }) : super(tableName);

  @override
  String getSQLInstruction() {
    String whereClause = '';
    if (conditions != null) whereClause += ' WHERE ${conditions!.statement}';
    return 'DELETE FROM $tableName$whereClause';
  }
}
