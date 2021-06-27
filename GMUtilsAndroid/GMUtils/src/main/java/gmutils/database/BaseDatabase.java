package gmutils.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;
import android.util.Log;

import gmutils.database.sqlitecommands.CreateTable;
import gmutils.database.sqlitecommands.DropTable;
import gmutils.database.sqlitecommands.WhereClause;
import gmutils.app.BaseApplication;
import gmutils.database.annotations.AutoIncrement;
import gmutils.database.annotations.Default;
import gmutils.database.annotations.Entity;
import gmutils.database.annotations.Ignore;
import gmutils.database.annotations.Not_Null;
import gmutils.database.annotations.PrimaryKey;
import gmutils.database.annotations.Unique;
import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
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

    private Database database;

    public BaseDatabase(@NotNull Context context) {
        try {
            Class.forName("com.google.gson.Gson");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            throw new IllegalStateException("add this line to gradle script file:\n" +
                    "implementation 'com.google.code.gson:gson:2.8.6'");
        }

        database = new Database(context, databaseName(), databaseVersion(), this);

        if (BaseApplication.current() != null) {
            BaseApplication.current().setOnApplicationFinishedLastActivity(() -> {
                try {
                    db().close();
                } catch (Exception e) {
                }

                database = null;
            });
        }
    }

    private SQLiteDatabase db() {
        return database.getWritableDatabase();
    }

    //region --- help methods ----------------------------------------------------------------------
    @NotNull
    protected abstract String databaseName();

    protected abstract int databaseVersion();

    @NotNull
    protected abstract Class<?>[] databaseEntities();

    @Nullable
    protected String onConvertDataTypeToJsonRequired(Class<?> entity, String fieldName, Class<?> fieldType, Object value) {
        throw new IllegalStateException("You have to override onConvertDataTypeRequired because it's needed to know" +
                "the alternative value of: [" + fieldName + "] " +
                "of type: [" + fieldType + "] " +
                "in entity:[" + entity + "] .... \n" +
                "current value: [" + value + "]");
    }

    /*
    *
     * for List use TypeToken of Gson
     * @return
    @Nullable
    protected Type onConvertingDataRequireClassType(Class<?> entity, String fieldName, Class<?> fieldType) {
        throw new IllegalStateException("You have to override onConvertingDataRequireClassType because it's needed to know" +
                "the alternative value of: [" + fieldName + "] " +
                "of type: [" + fieldType + "] " +
                "in entity:[" + entity + "]");
    }*/

    private static class EntityProperties {
        String tableName;
        String[] ignoredFields;
    }

    private EntityProperties getEntityProperties(Class<?> entity) {
        EntityProperties entityProperties = new EntityProperties();
        entityProperties.tableName = entity.getSimpleName();

        Annotation[] annotations = entity.getDeclaredAnnotations();
        if (annotations != null && annotations.length > 0) {
            for (Annotation annotation : annotations) {
                if (annotation.annotationType() == Entity.class) {
                    Entity anEntity = entity.getAnnotation(Entity.class);
                    if (!TextUtils.isEmpty(anEntity.tableName())) {
                        entityProperties.tableName = anEntity.tableName();
                    }
                    entityProperties.ignoredFields = anEntity.ignoredFields();
                }
            }
        }

        return entityProperties;
    }

    private String getTableName(Class<?> entity) {
        EntityProperties prop = getEntityProperties(entity);
        return prop.tableName;
    }

    private int primaryKeysCount = 0;

    @Nullable
    private CreateTable.Constraint[] getFieldConstraints(@NotNull Field field) {
        List<CreateTable.Constraint> constraints = new ArrayList<>();

        Annotation[] annotations = field.getDeclaredAnnotations();

        if (annotations != null && annotations.length > 0) {
            for (Annotation annotation : annotations) {
                if (annotation.annotationType() == PrimaryKey.class) {
                    constraints.add(new CreateTable.Constraint(CreateTable.ConstraintKeywords.PRIMARY_KEY));
                    primaryKeysCount++;

                } else if (annotation.annotationType() == AutoIncrement.class) {
                    constraints.add(new CreateTable.Constraint(CreateTable.ConstraintKeywords.AUTOINCREMENT));

                } else if (annotation.annotationType() == Not_Null.class) {
                    constraints.add(new CreateTable.Constraint(CreateTable.ConstraintKeywords.NOT_NULL));

                } else if (annotation.annotationType() == Default.class) {
                    Default def = field.getAnnotation(Default.class);
                    String defVal = def.value();
                    boolean isFunction = def.isFunction();
                    if (field.getType() == boolean.class || field.getType() == Boolean.class) {
                        if ("true".equalsIgnoreCase(defVal)) {
                            defVal = "1";
                        } else if ("false".equalsIgnoreCase(defVal)) {
                            defVal = "0";
                        } else {
                            defVal = "";
                        }
                    }
                    constraints.add(new CreateTable.Constraint(CreateTable.ConstraintKeywords.DEFAULT, defVal));

                } else if (annotation.annotationType() == Unique.class) {
                    constraints.add(new CreateTable.Constraint(CreateTable.ConstraintKeywords.UNIQUE));
                }
            }
        }

        if (constraints.size() > 0)
            return constraints.toArray(new CreateTable.Constraint[0]);
        else
            return null;
    }


    //endregion --- help methods -------------------------------------------------------------------

    @Override
    public void onCreate(SQLiteDatabase db) {
//        DateOp logDeadline = Logger.GET_LOG_DEADLINE();
//        Logger.SET_LOG_DEADLINE(30, 12, 2222);

        Class<?>[] entities = databaseEntities();

        for (Class<?> entity : entities) {
            Class<?> cls = entity;

            EntityProperties entityProperties = getEntityProperties(entity);
            primaryKeysCount = 0;

            CreateTable dbTable = new CreateTable(entityProperties.tableName);

            while (cls != null) {
                Field[] fields = cls.getDeclaredFields();

                for (Field field : fields) {
                    if (Modifier.isStatic(field.getModifiers()))
                        continue;

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
                        if (entityProperties.ignoredFields != null) {
                            for (String fName : entityProperties.ignoredFields) {
                                if (TextUtils.equals(fName, field.getName())) {
                                    ignore = true;
                                    break;
                                }
                            }
                        }
                    }

                    if (!ignore) {
                        CreateTable.DataTypes dataType;
                        Class<?> fieldType = field.getType();

                        if (fieldType == short.class || fieldType == Short.class)
                            dataType = CreateTable.DataTypes.INTEGER;
                        else if (fieldType == int.class || fieldType == Integer.class)
                            dataType = CreateTable.DataTypes.INTEGER;
                        else if (fieldType == long.class || fieldType == Long.class)
                            dataType = CreateTable.DataTypes.INTEGER;

                        else if (fieldType == boolean.class || fieldType == Boolean.class)
                            dataType = CreateTable.DataTypes.INTEGER;

                        else if (fieldType == float.class || fieldType == Float.class)
                            dataType = CreateTable.DataTypes.REAL;
                        else if (fieldType == double.class || fieldType == Double.class)
                            dataType = CreateTable.DataTypes.REAL;

                        else if (fieldType == String.class)
                            dataType = CreateTable.DataTypes.TEXT;

                        else {
                            dataType = CreateTable.DataTypes.TEXT;

                            //check if end code has converter
                            onConvertDataTypeToJsonRequired(cls, field.getName(), fieldType, null);

                            /*throw new IllegalArgumentException("only supported field types are: " +
                                    "[short, int, long, float, double, String, boolean] " +
                                    "for field name: [" + field.getName() + "], " +
                                    "of type: [" + fieldType + "], " +
                                    "in entity: [" +cls.getName() +"]");*/
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
        Class<?>[] entities = databaseEntities();

        for (Class<?> entity : entities) {
            DropTable tasksTable = new DropTable(getTableName(entity));
            try {
                db.execSQL(tasksTable.getCode());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        onCreate(db);
    }

    //region--- SQL OP -----------------------------------------------------------------------------

    //region INSERT --------
    public <T> Long insert(@NotNull T data) {
        return insert(data, false);
    }

    public <T> Long insert(@NotNull T data, boolean forceReplace) {
        ContentValues values = collectDataFields(data);
        return insert(data.getClass(), values, forceReplace);
    }

    public <T> List<Long> insert(@NotNull List<T> data) {
        return insert(data, false);
    }

    public <T> List<Long> insert(@NotNull List<T> data, boolean forceReplace) {
        List<Long> ids = new ArrayList<>();

        for (T d : data) {
            long id = insert(d, forceReplace);
            ids.add(id);
        }

        return ids;
    }

    public long insert(Class<?> entity, ContentValues values) {
        return insert(entity, values, false);
    }

    public long insert(Class<?> entity, ContentValues values, boolean forceReplace) {
        //insert into TableName(column1, ..., columnN) values(value1, ..., valuesN)
        long rowID = db().insert(
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
                rowID = db().insert(
                        getTableName(entity),
                        null,
                        values
                );
            }
        }

        return rowID;
    }

    public <T> ContentValues collectDataFields(@NotNull T data) {
        Class<?> cls = data.getClass();
        ContentValues values = new ContentValues();

        while (cls != null) {
            Field[] fields = cls.getDeclaredFields();

            for (Field field : fields) {
                if (Modifier.isStatic(field.getModifiers()))
                    continue;

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

                    else {
                        String newValue = onConvertDataTypeToJsonRequired(
                                cls,
                                field.getName(),
                                fieldType,
                                value
                        );
                        values.put(field.getName(), newValue);
                    }

                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }

            cls = cls.getSuperclass();
            if (cls == Object.class) break;
        }

        return values;
    }

    //endregion INSERT --------------


    //region SELECT --------

    //region select multiple items
    public <T> List<T> select(@NotNull Class<T> entity, @NotNull TypeToken<List<T>> typeToken) {
        return select(entity, typeToken, (WhereClause) null, (Boolean) null);
    }

    public <T> List<T> select(@NotNull Class<T> entity, @NotNull TypeToken<List<T>> typeToken, @NotNull String[] specialColumns) {
        return select(entity, typeToken, specialColumns, (WhereClause) null, null);
    }

    public <T> List<T> select(@NotNull Class<T> entity, @NotNull TypeToken<List<T>> typeToken, @NotNull String[] specialColumns, WhereClause whereClause) {
        return select(entity, typeToken, specialColumns, whereClause == null ? null : whereClause.getCode());
    }

    public <T> List<T> select(@NotNull Class<T> entity, @NotNull TypeToken<List<T>> typeToken, @NotNull String[] specialColumns, String whereClause) {
        return select(entity, typeToken, specialColumns, whereClause, null);
    }

    public <T> List<T> select(@NotNull Class<T> entity, @NotNull TypeToken<List<T>> typeToken, @NotNull String[] specialColumns, WhereClause whereClause, Boolean orderAscending) {
        return select(entity, typeToken, specialColumns, whereClause == null ? null : whereClause.getCode(), orderAscending);
    }

    public <T> List<T> select(@NotNull Class<T> entity, @NotNull TypeToken<List<T>> typeToken, @NotNull String[] specialColumns, String whereClause, Boolean orderAscending) {
        JSONArray result = doSelect(entity, specialColumns, whereClause, orderAscending);

        Type typeOfT = typeToken.getType();
        List<T> lst = new Gson().fromJson(result.toString(), typeOfT);
        return lst;
    }


    public <T> List<T> select(@NotNull Class<T> entity, @NotNull TypeToken<List<T>> typeToken, WhereClause whereClause) {
        return select(entity, typeToken, whereClause == null ? null : whereClause.getCode(), null);
    }

    public <T> List<T> select(@NotNull Class<T> entity, @NotNull TypeToken<List<T>> typeToken, String whereClause) {
        return select(entity, typeToken, whereClause, null);
    }

    public <T> List<T> select(@NotNull Class<T> entity, @NotNull TypeToken<List<T>> typeToken, WhereClause whereClause, Boolean orderAscending) {
        return select(entity, typeToken, whereClause == null? null : whereClause.getCode(), orderAscending);
    }

    public <T> List<T> select(@NotNull Class<T> entity, @NotNull TypeToken<List<T>> typeToken, String whereClause, Boolean orderAscending) {
        List<T> lst = select(entity, typeToken, collectColumns(entity), whereClause, orderAscending);
        return lst;
    }
    //endregion select multiple items

    //region select single item
    public <T> T select(@NotNull Class<T> entity, @NotNull WhereClause whereClause) {
        return select(entity, whereClause.getCode());
    }

    public <T> T select(@NotNull Class<T> entity, @NotNull String whereClause) {
        return select(entity, whereClause, null);
    }


    public <T> T select(@NotNull Class<T> entity, @NotNull WhereClause whereClause, Boolean orderAscending) {
        return select(entity, whereClause.getCode(), orderAscending);
    }

    public <T> T select(@NotNull Class<T> entity, @NotNull String whereClause, Boolean orderAscending) {
        T item = select(entity, collectColumns(entity), whereClause, orderAscending);
        return item;
    }


    public <T> T select(@NotNull Class<T> entity, @NotNull String[] specialColumns, @NotNull WhereClause whereClause, Boolean orderAscending) {
        return select(entity, specialColumns, whereClause.getCode(), orderAscending);
    }

    public <T> T select(@NotNull Class<T> entity, @NotNull String[] specialColumns, @NotNull String whereClause, Boolean orderAscending) {
        JSONArray result = doSelect(entity, specialColumns, whereClause, orderAscending);

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
    //endregion select single item

    public <T> JSONArray doSelect(@NotNull Class<T> entity, @NotNull String[] specialColumns, WhereClause whereClause, Boolean orderAscending) {
        return doSelect(entity, specialColumns, whereClause == null? null : whereClause.getCode(), orderAscending);
    }

    public <T> JSONArray doSelect(@NotNull Class<T> entity, @NotNull String[] specialColumns, String whereClause, Boolean orderAscending) {
        //select columnsNames from tableName where columnName1=value1 AND columnName1=value1
        JSONArray result = new JSONArray();

        if (specialColumns != null && specialColumns.length > 0) {

            Cursor query = db().query(
                    getTableName(entity),
                    specialColumns,
                    whereClause,
                    null,
                    null,
                    null,
                    orderAscending == null? null : (orderAscending? "ASC" : "DESC")
            );

            if (query.moveToFirst()) {
                do {
                    JSONObject itemJson = new JSONObject();

                    for (String colName : specialColumns) {
                        int columnIndex = query.getColumnIndex(colName);

                        Field field = null;
                        Class<?> cls = entity;
                        while (cls != null) {
                            try {
                                field = cls.getDeclaredField(colName);
                            } catch (NoSuchFieldException e) {
                                //e.printStackTrace();
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

                        if (!query.isNull(columnIndex)) {
                            if (fieldType == short.class || fieldType == Short.class) {
                                value = query.getShort(columnIndex);

                            } else if (fieldType == boolean.class || fieldType == Boolean.class) {
                                value = query.getShort(columnIndex) == 1;

                            } else if (fieldType == int.class || fieldType == Integer.class) {
                                value = query.getInt(columnIndex);

                            } else if (fieldType == long.class || fieldType == Long.class) {
                                value = query.getLong(columnIndex);

                            } else if (fieldType == float.class || fieldType == Float.class) {
                                value = query.getFloat(columnIndex);

                            } else if (fieldType == double.class || fieldType == Double.class) {
                                value = query.getDouble(columnIndex);

                            } else if (fieldType == String.class) {
                                value = query.getString(columnIndex);

                            } else {
                                String json = query.getString(columnIndex);
                                try {
                                    if (json.startsWith("{")) {
                                        value = new JSONObject(json);

                                    } else if (json.startsWith("[")) {
                                        value = new JSONArray(json);
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }

                        try {
                            itemJson.put(getSerializedFieldName(field), value);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }

                    result.put(itemJson);

                } while (query.moveToNext());
            }

            query.close();
        }

        return result;
    }


    /* SPECIAL QUERIES */
    @Nullable
    public <T> List<Map<String, Object>> selectSpecial(@NotNull Class<T> entity, @NotNull String[] specialColumns) {
        return selectSpecial(entity, specialColumns, (String) null, null);
    }

    @Nullable
    public <T> List<Map<String, Object>> selectSpecial(@NotNull Class<T> entity, @NotNull String[] specialColumns, WhereClause whereClause) {
        return selectSpecial(entity, specialColumns, whereClause == null? null : whereClause.getCode());
    }

    public <T> List<Map<String, Object>> selectSpecial(@NotNull Class<T> entity, @NotNull String[] specialColumns, String whereClause) {
        return selectSpecial(entity, specialColumns, whereClause, null);
    }

    @Nullable
    public <T> List<Map<String, Object>> selectSpecial(@NotNull Class<T> entity, @NotNull String[] specialColumns, WhereClause whereClause, Boolean orderAscending) {
        return selectSpecial(entity, specialColumns, whereClause == null ? null : whereClause.getCode(), orderAscending);
    }

    @Nullable
    public <T> List<Map<String, Object>> selectSpecial(@NotNull Class<T> entity, @NotNull String[] specialColumns, String whereClause, Boolean orderAscending) {
        //select columnsNames from tableName where columnName1=value1 AND columnName1=value1
        Cursor cursor = db().query(
                getTableName(entity),
                specialColumns,
                whereClause,
                null,
                null,
                null,
                orderAscending == null? null : (orderAscending? "ASC" : "DESC")
        );

        List<Map<String, Object>> map = convertCursorToMap(cursor);

        cursor.close();

        return map;
    }


    public List<Map<String, Object>> sqlQuery(String sqlInstruction) {
        Cursor cursor = db().rawQuery(sqlInstruction, null);

        List<Map<String, Object>> map = convertCursorToMap(cursor);

        cursor.close();

        return map;
    }


    public <T> long getEntityCount(@NotNull Class<T> entity) {
        return getEntityCount(entity, (WhereClause) null);
    }

    public <T> long getEntityCount(@NotNull Class<T> entity, WhereClause whereClause) {
        return getEntityCount(entity, whereClause == null ? null : whereClause.getCode());
    }

    public <T> long getEntityCount(@NotNull Class<T> entity, String whereClause) {
        return getEntityCount(entity, "*", whereClause);
    }

    public <T> long getEntityCount(@NotNull Class<T> entity, String columnName, WhereClause whereClause) {
        return getEntityCount(entity, columnName, whereClause == null? null : whereClause.getCode());
    }

    public <T> long getEntityCount(@NotNull Class<T> entity, String columnName, String whereClause) {
        String sql = "SELECT COUNT(" + columnName + ") FROM " + getTableName(entity);
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

    //endregion SELECT -----------------


    //region UPDATE --------
    public <T> int update(@NotNull T data) {
        String whereClause = generateWhereClauseFromPrimaryFields(getPrimaryFieldsValues(data));

        if (!TextUtils.isEmpty(whereClause)) {
            ContentValues values = collectDataFields(data);
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

    public int update(Class<?> entity, ContentValues values, WhereClause whereClause) {
        return update(entity, values, whereClause == null ? null : whereClause.getCode());
    }

    public int update(Class<?> entity, ContentValues values, String whereClause) {
        int c = db().update(
                getTableName(entity),
                values,
                whereClause,
                null
        );

        return c;
    }

    //endregion UPDATE -------------


    //region DELETE --------
    public <T> int delete(@NotNull List<T> data) {
        int r = 0;
        for (T d : data) {
            int r0 = delete(d);
            r += r0;
        }
        return r;
    }

    public <T> int delete(@NotNull T data) {
        String whereClause = generateWhereClauseFromPrimaryFields(getPrimaryFieldsValues(data));

        if (!TextUtils.isEmpty(whereClause)) {
            return delete(data.getClass(), whereClause);
        } else {
            return 0;
        }
    }

    public int deleteAll(Class<?> entity) {
        return delete(entity, (String) null);
    }

    public int delete(Class<?> entity, WhereClause whereClause) {
        return delete(entity, whereClause == null ? null : whereClause.getCode());
    }

    public int delete(Class<?> entity, String whereClause) {
        int c = db().delete(
                getTableName(entity),
                whereClause,
                null
        );

        return c;
    }

    //endregion DELETE -----------


    // SPECIAL QUERIES
    public void sqlInstruction(String sqlInstruction) {
        db().execSQL(sqlInstruction);
    }

    //--------------------------------------------

    private String[] getPrimaryFields(Class<?> entity) {
        List<String> primaryFields = new ArrayList<>();

        Class<?> cls = entity;
        while (cls != null) {
            Field[] fields = cls.getDeclaredFields();

            for (Field field : fields) {
                if (Modifier.isStatic(field.getModifiers()))
                    continue;

                CreateTable.Constraint[] constraints = getFieldConstraints(field);
                if (constraints != null) {
                    for (CreateTable.Constraint constraint : constraints) {
                        if (constraint.getConstraint() == CreateTable.ConstraintKeywords.PRIMARY_KEY) {
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

    private <T> String[] collectColumns(@NotNull Class<T> entity) {
        List<String> specialColumns = new ArrayList<>();

        Class<?> cls = entity;
        while (cls != null) {
            Field[] fields = cls.getDeclaredFields();

            for (Field field : fields) {
                if (Modifier.isStatic(field.getModifiers()))
                    continue;

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

    private <T> Map<String, Object> getPrimaryFieldsValues(@NotNull T data) {
        Map<String, Object> primaryFieldsValues = new HashMap<>();

        Class<?> cls = data.getClass();
        while (cls != null) {
            Field[] fields = cls.getDeclaredFields();

            for (Field field : fields) {
                if (Modifier.isStatic(field.getModifiers()))
                    continue;

                CreateTable.Constraint[] constraints = getFieldConstraints(field);
                if (constraints != null) {
                    for (CreateTable.Constraint constraint : constraints) {
                        if (constraint.getConstraint() == CreateTable.ConstraintKeywords.PRIMARY_KEY) {
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

    private String getSerializedFieldName(@NotNull Field field) {
        String fieldName = field.getName();

        Annotation[] annotations = field.getDeclaredAnnotations();

        if (annotations != null && annotations.length > 0) {
            for (Annotation annotation : annotations) {
                if (annotation.annotationType() == SerializedName.class) {
                    SerializedName serializedName = field.getAnnotation(SerializedName.class);
                    fieldName = serializedName.value();
                    break;
                }
            }
        }

        return fieldName;
    }

    //endregion--- SQL OP -----------------------------------------------------------------------------
}
