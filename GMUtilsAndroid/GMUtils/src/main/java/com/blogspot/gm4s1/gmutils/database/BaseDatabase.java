package com.blogspot.gm4s1.gmutils.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;
import android.util.Log;

import com.blogspot.gm4s1.gmutils._bases.BaseApplication;
import com.blogspot.gm4s1.gmutils.database.annotations.AutoIncrement;
import com.blogspot.gm4s1.gmutils.database.annotations.Default;
import com.blogspot.gm4s1.gmutils.database.annotations.Entity;
import com.blogspot.gm4s1.gmutils.database.annotations.Ignore;
import com.blogspot.gm4s1.gmutils.database.annotations.Not_Null;
import com.blogspot.gm4s1.gmutils.database.annotations.PrimaryKey;
import com.blogspot.gm4s1.gmutils.database.annotations.Unique;
import com.blogspot.gm4s1.gmutils.database.sqlitecommands.Constraint;
import com.blogspot.gm4s1.gmutils.database.sqlitecommands.ConstraintKeywords;
import com.blogspot.gm4s1.gmutils.database.sqlitecommands.DataTypes;
import com.blogspot.gm4s1.gmutils.database.sqlitecommands.SqlCommands;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by Ahmed El-Sayed (Glory Maker)
 * Computer Engineer / 2012
 * Android/iOS Developer with (Java/Kotlin, Swift)
 * Have experience with:
 * - (C/C++, C#) languages
 * - .NET environment
 * - AVR Microcontrollers
 * a.elsayedabdo@gmail.com
 * +201022663988
 */
interface DatabaseCallbacks {
    void onCreate(SQLiteDatabase db);

    void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion);
}

public abstract class BaseDatabase implements DatabaseCallbacks {

    private static class Database extends SQLiteOpenHelper {
        private DatabaseCallbacks mDatabaseCallbacks;

        private Database(Context context, String databaseName, int databaseVersion, DatabaseCallbacks callbacks) {
            super(context, databaseName, null, databaseVersion);

            mDatabaseCallbacks = callbacks;

            SQLiteDatabase readableDatabase = getReadableDatabase();
            //String path = readableDatabase.getPath();
            //Log.e("*** database path", path);

            mDatabaseCallbacks = null;
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            if (mDatabaseCallbacks != null) {
                mDatabaseCallbacks.onCreate(db);
            }
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            if (mDatabaseCallbacks != null) {
                mDatabaseCallbacks.onUpgrade(db, oldVersion, newVersion);
            }
        }


    }

    //----------------------------------------------------------------------------------------------

    private Database mDatabase;

    public BaseDatabase(@NotNull Context context) {
        mDatabase = new Database(context, databaseName(), databaseVersion(), this);

        if (BaseApplication.current() != null) {
            BaseApplication.current().addCallback(BaseDatabase.class.getName(), new BaseApplication.Callbacks() {
                @Override
                public void onApplicationStartedFirstActivity() {
                }

                @Override
                public void onApplicationFinishedLastActivity() {
                    mDatabase = null;
                }
            });
        }
    }

    //region --- help methods ----------------------------------------------------------------------
    @NotNull
    protected abstract String databaseName();

    protected abstract int databaseVersion();

    @NotNull
    protected abstract Class<?>[] databaseEntities();

    private int primaryKeysCount = 0;

    @Nullable
    private Constraint[] getFieldConstraints(@NotNull Field field) {
        List<Constraint> constraints = new ArrayList<>();

        Annotation[] annotations = field.getDeclaredAnnotations();

        if (annotations != null && annotations.length > 0) {
            for (Annotation annotation : annotations) {
                if (annotation.annotationType() == PrimaryKey.class) {
                    constraints.add(new Constraint(ConstraintKeywords.PRIMARY_KEY));
                    primaryKeysCount++;

                } else if (annotation.annotationType() == AutoIncrement.class) {
                    constraints.add(new Constraint(ConstraintKeywords.AUTOINCREMENT));

                } else if (annotation.annotationType() == Not_Null.class) {
                    constraints.add(new Constraint(ConstraintKeywords.NOT_NULL));

                } else if (annotation.annotationType() == Default.class) {
                    Default aDefault = field.getAnnotation(Default.class);
                    String defVal = aDefault.value();
                    constraints.add(new Constraint(ConstraintKeywords.DEFAULT, defVal));

                } else if (annotation.annotationType() == Unique.class) {
                    constraints.add(new Constraint(ConstraintKeywords.UNIQUE));
                }
            }
        }

        if (constraints.size() > 0)
            return constraints.toArray(new Constraint[0]);
        else
            return null;
    }
    //endregion --- help methods -------------------------------------------------------------------

    @Override
    public void onCreate(SQLiteDatabase db) {
//        DateOp logDeadline = Logger.GET_LOG_DEADLINE();
//        Logger.SET_LOG_DEADLINE(30, 12, 2222);

        SqlCommands sqlCommands = new SqlCommands();

        Class<?>[] entities = databaseEntities();

        for (Class<?> entity : entities) {
            Class<?> cls = entity;

            String tableName = getTableName(cls);
            primaryKeysCount = 0;

            SqlCommands.CreateTable dbTable = sqlCommands.new CreateTable(tableName);

            while (cls != null) {
                Field[] fields = cls.getDeclaredFields();

                for (Field field : fields) {
                    boolean ignore = false;
                    Annotation[] fieldAnnotations = field.getDeclaredAnnotations();
                    if (fieldAnnotations != null && fieldAnnotations.length > 0) {
                        for (Annotation annotation : fieldAnnotations) {
                            if (annotation.annotationType() == Ignore.class) {
                                ignore = true;
                                break;
                            }
                        }
                    }

                    if (!ignore) {
                        DataTypes dataType;
                        Class<?> fieldType = field.getType();

                        if (fieldType == short.class || fieldType == Short.class)
                            dataType = DataTypes.INTEGER;
                        else if (fieldType == int.class || fieldType == Integer.class)
                            dataType = DataTypes.INTEGER;
                        else if (fieldType == long.class || fieldType == Long.class)
                            dataType = DataTypes.INTEGER;

                        else if (fieldType == boolean.class || fieldType == Boolean.class)
                            dataType = DataTypes.INTEGER;

                        else if (fieldType == float.class || fieldType == Float.class)
                            dataType = DataTypes.REAL;
                        else if (fieldType == double.class || fieldType == Double.class)
                            dataType = DataTypes.REAL;

                        else if (fieldType == String.class) dataType = DataTypes.TEXT;

                        else {
                            throw new IllegalArgumentException("only supported field types are: [short, int, long, float, double, String, boolean] for field name: [" + field.getName() + "], given type is: [" + fieldType + "]");
                        }

                        dbTable.addColumn(field.getName(), dataType, getFieldConstraints(field));
                    }
                }

                cls = cls.getSuperclass();
                if (cls == Object.class) break;
            }

            if (primaryKeysCount == 0) {
                throw new IllegalStateException(entity.getSimpleName() + " class doesn't contains primary key ... use the annotation: " + PrimaryKey.class.getName());
            }

            db.execSQL(dbTable.getCode());
        }

//        Logger.SET_LOG_DEADLINE(logDeadline);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        SqlCommands sqlCommands = new SqlCommands();

        Class<?>[] entities = databaseEntities();

        for (Class<?> entity : entities) {
            SqlCommands.DropTable tasksTable = sqlCommands.new DropTable(getTableName(entity));
            try {
                db.execSQL(tasksTable.getCode());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        onCreate(db);
    }

    private String getTableName(Class<?> entity) {
        String tableName = entity.getSimpleName();

        Annotation[] annotations = entity.getDeclaredAnnotations();
        if (annotations != null && annotations.length > 0) {
            for (Annotation annotation : annotations) {
                if (annotation.annotationType() == Entity.class) {
                    Entity anEntity = entity.getAnnotation(Entity.class);
                    tableName = anEntity.tableName();
                }
            }
        }

        return tableName;
    }

    //region--- SQL OP -----------------------------------------------------------------------------

    /* INSERT */
    public <T> List<Long> insert(@NotNull List<T> data) {
        return insert(data, false);
    }

    public <T> List<Long> insert(@NotNull List<T> data, boolean forceReplace) {
        List<Long> ids = new ArrayList<>();

        for (T d : data) {
            ContentValues values = convertDataToContentValues(d);
            long id = insert(d.getClass(), values, forceReplace);
            ids.add(id);
        }

        return ids;
    }

    public long insert(Class<?> entity, ContentValues values) {
        return insert(entity, values, false);
    }

    public long insert(Class<?> entity, ContentValues values, boolean forceReplace) {
        SQLiteDatabase db = mDatabase.getReadableDatabase();

        //insert into TableName(column1, ..., columnN) values(value1, ..., valuesN)
        long rowID = db.insert(
                getTableName(entity),
                null,
                values
        );

        if (forceReplace && rowID < 0) {
            String whereClause = "";
            String[] pfs = getPrimaryFields(entity);
            Map<String, Object> primaryFieldsValues = new HashMap<>();
            for (String pf : pfs) {
                primaryFieldsValues.put(pf, values.get(pf));
            }
            whereClause = generateWhereClauseFromPrimaryFields(primaryFieldsValues);
            int deleted = delete(entity, whereClause);
            if (deleted > 0) {
                db = mDatabase.getReadableDatabase();
                rowID = db.insert(
                        getTableName(entity),
                        null,
                        values
                );
            }
        }

        db.close();

        return rowID;
    }

    private String[] getPrimaryFields(Class<?> entity) {
        List<String> primaryFields = new ArrayList<>();

        Class<?> cls = entity;
        while (cls != null) {
            Field[] fields = cls.getDeclaredFields();

            for (Field field : fields) {
                Constraint[] constraints = getFieldConstraints(field);
                if (constraints != null) {
                    for (Constraint constraint : constraints) {
                        if (constraint.getConstraint() == ConstraintKeywords.PRIMARY_KEY) {
                            primaryFields.add(field.getName());
                        }
                    }
                }
            }

            cls = cls.getSuperclass();
            if (cls == Object.class) break;
        }

        return primaryFields.toArray(new String[0]);
    }

    public <T> ContentValues convertDataToContentValues(@NotNull T data) {
        Class<?> cls = data.getClass();
        ContentValues values = new ContentValues();

        while (cls != null) {
            Field[] fields = cls.getDeclaredFields();

            for (Field field : fields) {
                boolean ignore = false;
                Annotation[] fieldAnnotations = field.getDeclaredAnnotations();
                if (fieldAnnotations != null && fieldAnnotations.length > 0) {
                    for (Annotation annotation : fieldAnnotations) {
                        if (annotation.annotationType() == Ignore.class) {
                            ignore = true;
                            break;
                        }
                    }
                }

                if (ignore) continue;

                try {
                    field.setAccessible(true);
                    Object value = field.get(data);
                    Class<?> fieldType = field.getType();

                    if (fieldType == short.class || fieldType == Short.class)
                        values.put(field.getName(), (Short) value);

                    else if (fieldType == int.class || fieldType == Integer.class)
                        values.put(field.getName(), (Integer) value);

                    else if (fieldType == long.class || fieldType == Long.class)
                        values.put(field.getName(), (Long) value);

                    else if (fieldType == boolean.class || fieldType == Boolean.class) {
                        Integer bv = null;
                        if (value != null) bv = ((Boolean) value) ? 1 : 0;
                        values.put(field.getName(), bv);
                    } else if (fieldType == float.class || fieldType == Float.class)
                        values.put(field.getName(), (Float) value);

                    else if (fieldType == double.class || fieldType == Double.class)
                        values.put(field.getName(), (Double) value);

                    else if (fieldType == String.class)
                        values.put(field.getName(), (String) value);

                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }

            cls = cls.getSuperclass();
            if (cls == Object.class) break;
        }

        return values;
    }


    /* SELECT */
    public <T> T select(@NotNull Class<T> entity, @NotNull String whereClause) {
        return select(entity, whereClause, null);
    }

    public <T> T select(@NotNull Class<T> entity, @NotNull String whereClause, String orderBy) {
        T item = select(entity, collectColumns(entity), whereClause, orderBy);
        return item;
    }

    public <T> T select(@NotNull Class<T> entity, @NotNull String[] specialColumns, String whereClause, String orderBy) {
        JSONArray result = doSelect(entity, specialColumns, whereClause, orderBy);

        if (result.length() > 0) {
            T item = null;
            try {
                item = new Gson().fromJson(result.getJSONObject(0).toString(), entity);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return item;
        } else {
            return null;
        }
    }


    public <T> List<T> select(@NotNull Class<T> entity, @NotNull TypeToken<List<T>> typeToken) {
        return select(entity, typeToken, (String) null, (String) null);
    }

    public <T> List<T> select(@NotNull Class<T> entity, @NotNull TypeToken<List<T>> typeToken, String whereClause) {
        return select(entity, typeToken, whereClause, null);
    }

    public <T> List<T> select(@NotNull Class<T> entity, @NotNull TypeToken<List<T>> typeToken, String whereClause, String orderBy) {
        List<T> lst = select(entity, typeToken, collectColumns(entity), whereClause, orderBy);
        return lst;
    }


    public <T> List<T> select(@NotNull Class<T> entity, @NotNull TypeToken<List<T>> typeToken, @NotNull String[] specialColumns) {
        return select(entity, typeToken, specialColumns, null, null);
    }

    public <T> List<T> select(@NotNull Class<T> entity, @NotNull TypeToken<List<T>> typeToken, @NotNull String[] specialColumns, String whereClause) {
        return select(entity, typeToken, specialColumns, whereClause, null);
    }

    public <T> List<T> select(@NotNull Class<T> entity, @NotNull TypeToken<List<T>> typeToken, @NotNull String[] specialColumns, String whereClause, String orderBy) {
        JSONArray result = doSelect(entity, specialColumns, whereClause, orderBy);

        Type typeOfT = typeToken.getType();
        List<T> lst = new Gson().fromJson(result.toString(), typeOfT);
        return lst;
    }


    public <T> JSONArray doSelect(@NotNull Class<T> entity, @NotNull String[] specialColumns, String whereClause, String orderBy) {
        SQLiteDatabase db = mDatabase.getReadableDatabase();

        //select columnsNames from tableName where columnName1=value1 AND columnName1=value1
        Cursor query = db.query(
                getTableName(entity),
                specialColumns,
                whereClause,
                null,
                null,
                null,
                orderBy
        );

        JSONArray result = new JSONArray();

        if (specialColumns != null && specialColumns.length > 0) {
            if (query.moveToFirst()) {
                do {
                    JSONObject item = new JSONObject();

                    for (String colName : specialColumns) {
                        int columnIndex = query.getColumnIndex(colName);

                        Field field = null;
                        Class<?> cls = entity;
                        while (cls != null) {
                            try {
                                field = cls.getDeclaredField(colName);
                            } catch (NoSuchFieldException e) {
                                e.printStackTrace();
                            }

                            if (field != null) break;

                            cls = cls.getSuperclass();
                            if (cls == Object.class) break;
                        }

                        if (field == null) {
                            Log.e("*** " + Database.class.getSimpleName(), "there is no field with the name: [" + colName + "] in [" + getTableName(entity) + "] or its super classes");
                            continue;
                        }

                        Class<?> fieldType = field.getType();
                        Object value = null;

                        if (fieldType == short.class || fieldType == Short.class) {
                            if (!query.isNull(columnIndex)) {
                                value = query.getShort(columnIndex);
                            }
                        } else if (fieldType == boolean.class || fieldType == Boolean.class) {
                            if (!query.isNull(columnIndex)) {
                                value = query.getShort(columnIndex) == 1;
                            }
                        } else if (fieldType == int.class || fieldType == Integer.class) {
                            if (!query.isNull(columnIndex)) {
                                value = query.getInt(columnIndex);
                            }
                        } else if (fieldType == long.class || fieldType == Long.class) {
                            if (!query.isNull(columnIndex)) {
                                value = query.getLong(columnIndex);
                            }
                        } else if (fieldType == float.class || fieldType == Float.class) {
                            if (!query.isNull(columnIndex)) {
                                value = query.getFloat(columnIndex);
                            }
                        } else if (fieldType == double.class || fieldType == Double.class) {
                            if (!query.isNull(columnIndex)) {
                                value = query.getDouble(columnIndex);
                            }
                        } else if (fieldType == String.class) {
                            if (!query.isNull(columnIndex)) {
                                value = query.getString(columnIndex);
                            }
                        }

                        try {
                            item.put(field.getName(), value);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }

                    result.put(item);

                } while (query.moveToNext());
            }
        }

        query.close();
        db.close();

        return result;
    }

    private <T> String[] collectColumns(@NotNull Class<T> entity) {
        List<String> specialColumns = new ArrayList<>();

        Class<?> cls = entity;
        while (cls != null) {
            Field[] fields = cls.getDeclaredFields();

            for (Field field : fields) {
                boolean ignore = false;
                Annotation[] fieldAnnotations = field.getDeclaredAnnotations();
                if (fieldAnnotations != null && fieldAnnotations.length > 0) {
                    for (Annotation annotation : fieldAnnotations) {
                        if (annotation.annotationType() == Ignore.class) {
                            ignore = true;
                            break;
                        }
                    }
                }

                if (!ignore) {
                    specialColumns.add(field.getName());
                }
            }

            cls = cls.getSuperclass();
            if (cls == Object.class) break;
        }

        return specialColumns.toArray(new String[0]);
    }

    /* SPECIAL QUERIES */
    @Nullable
    public <T> List<Map<String, Object>> selectSpecial(@NotNull Class<T> entity, @NotNull String[] specialColumns) {
        return selectSpecial(entity, specialColumns, null, null);
    }

    @Nullable
    public <T> List<Map<String, Object>> selectSpecial(@NotNull Class<T> entity, @NotNull String[] specialColumns, String whereClause) {
        return selectSpecial(entity, specialColumns, whereClause, null);
    }

    @Nullable
    public <T> List<Map<String, Object>> selectSpecial(@NotNull Class<T> entity, @NotNull String[] specialColumns, String whereClause, String orderBy) {
        SQLiteDatabase db = mDatabase.getReadableDatabase();

        //select columnsNames from tableName where columnName1=value1 AND columnName1=value1
        Cursor cursor = db.query(
                getTableName(entity),
                specialColumns,
                whereClause,
                null,
                null,
                null,
                orderBy
        );

        List<Map<String, Object>> map = convertCursorToMap(cursor);

        cursor.close();
        db.close();

        return map;
    }


    private List<Map<String, Object>> convertCursorToMap(Cursor cursor) {
        List<Map<String, Object>> result = null;

        if (cursor.moveToFirst()) {
            result = new ArrayList<>();

            do {
                Map<String, Object> map = new HashMap<>();

                String[] columnNames = cursor.getColumnNames();
                for (String columnName : columnNames) {
                    int columnIndex = cursor.getColumnIndex(columnName);
                    Object value = null;

                    switch (cursor.getType(columnIndex)) {
                        case Cursor.FIELD_TYPE_NULL:
                            value = null;
                            break;

                        case Cursor.FIELD_TYPE_INTEGER:
                            value = cursor.getLong(columnIndex);
                            break;

                        case Cursor.FIELD_TYPE_FLOAT:
                            value = cursor.getDouble(columnIndex);
                            break;

                        case Cursor.FIELD_TYPE_STRING:
                            value = cursor.getString(columnIndex);
                            break;

                        case Cursor.FIELD_TYPE_BLOB:
                            value = cursor.getBlob(columnIndex);
                            break;
                    }

                    map.put(columnName, value);
                }

                result.add(map);

            } while (cursor.moveToNext());
        }

        return result;
    }

    public List<Map<String, Object>> sqlQuery(String sqlInstruction) {
        SQLiteDatabase db = mDatabase.getReadableDatabase();
        Cursor cursor = db.rawQuery(sqlInstruction, null);

        List<Map<String, Object>> map = convertCursorToMap(cursor);

        cursor.close();
        db.close();

        return map;
    }


    public <T> long getEntityCount(@NotNull Class<T> entity, String whereClause) {
        String sql = "SELECT COUNT(*) as count FROM " + getTableName(entity);
        if (!TextUtils.isEmpty(whereClause)) {
            sql += " WHERE " + whereClause;
        }
        List<Map<String, Object>> res = sqlQuery(sql);
        if (res != null && res.size() > 0) {
            if (res.get(0).size() > 0) {
                Object o = res.get(0).values().toArray()[0];
                return (long) o;
            } else {
                return -1;
            }
        } else {
            return -1;
        }
    }



    /* UPDATE */
    public <T> int update(@NotNull T data) {
        String whereClause = generateWhereClauseFromPrimaryFields(getPrimaryFieldsValues(data));

        if (!TextUtils.isEmpty(whereClause)) {
            ContentValues values = convertDataToContentValues(data);
            return update(data.getClass(), values, whereClause);
        } else {
            return 0;
        }
    }

    public <T> int update(@NotNull List<T> data) {
        int r = 0;
        for (T d : data) {
            int r0 = update(d);
            r += r0;
        }
        return r;
    }

    public int update(Class<?> entity, ContentValues values, String whereClause) {
        SQLiteDatabase db = mDatabase.getReadableDatabase();

        int c = db.update(
                getTableName(entity),
                values,
                whereClause,
                null
        );

        db.close();

        return c;
    }

    private <T> Map<String, Object> getPrimaryFieldsValues(@NotNull T data) {
        Map<String, Object> primaryFieldsValues = new HashMap<>();

        Class<?> cls = data.getClass();
        while (cls != null) {
            Field[] fields = cls.getDeclaredFields();

            for (Field field : fields) {
                Constraint[] constraints = getFieldConstraints(field);
                if (constraints != null) {
                    for (Constraint constraint : constraints) {
                        if (constraint.getConstraint() == ConstraintKeywords.PRIMARY_KEY) {
                            try {
                                field.setAccessible(true);
                                Object value = field.get(data);
                                primaryFieldsValues.put(field.getName(), value);
                            } catch (IllegalAccessException e) {
                                primaryFieldsValues.put(field.getName(), null);
                            }
                        }
                    }
                }
            }

            cls = cls.getSuperclass();
            if (cls == Object.class) break;
        }

        return primaryFieldsValues;
    }

    private String generateWhereClauseFromPrimaryFields(Map<String, Object> primaryFieldsValues) {
        String whereClause = "";
        Set<Map.Entry<String, Object>> entries = primaryFieldsValues.entrySet();
        for (Map.Entry<String, Object> entry : entries) {
            Object value = entry.getValue();
            if (value != null && value.getClass() == String.class) {
                whereClause += entry.getKey() + "='" + value + "'";
            } else {
                whereClause += entry.getKey() + "=" + value;
            }
        }
        return whereClause;
    }



    /* DELETE */
    public <T> int delete(@NotNull T data) {
        String whereClause = generateWhereClauseFromPrimaryFields(getPrimaryFieldsValues(data));

        if (!TextUtils.isEmpty(whereClause)) {
            return delete(data.getClass(), whereClause);
        } else {
            return 0;
        }
    }

    public int delete(Class<?> entity, String whereClause) {
        SQLiteDatabase db = mDatabase.getReadableDatabase();

        int c = db.delete(
                getTableName(entity),
                whereClause,
                null
        );

        db.close();

        return c;
    }



    /* SPECIAL QUERIES */
    public void sqlInstruction(String sqlInstruction) {
        SQLiteDatabase db = mDatabase.getReadableDatabase();
        db.execSQL(sqlInstruction);
        db.close();
    }

    //endregion--- SQL OP -----------------------------------------------------------------------------
}
