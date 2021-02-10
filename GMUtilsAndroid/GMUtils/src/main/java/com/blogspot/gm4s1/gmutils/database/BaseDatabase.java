package com.blogspot.gm4s1.gmutils.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.blogspot.gm4s1.gmutils.database.annotations.AutoIncrement;
import com.blogspot.gm4s1.gmutils.database.annotations.Default;
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
    }

    @NotNull
    protected abstract String databaseName();

    protected abstract int databaseVersion();

    @NotNull
    protected abstract Class<?>[] databaseEntities();

    @Nullable
    private Constraint[] getFieldConstraints(@NotNull Field field) {
        List<Constraint> constraints = new ArrayList<>();

        Annotation[] annotations = field.getDeclaredAnnotations();

        if (annotations != null && annotations.length > 0) {
            for (Annotation annotation : annotations) {
                if (annotation.annotationType() == PrimaryKey.class) {
                    constraints.add(new Constraint(ConstraintKeywords.PRIMARY_KEY));

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

    @Override
    public void onCreate(SQLiteDatabase db) {
//        DateOp logDeadline = Logger.GET_LOG_DEADLINE();
//        Logger.SET_LOG_DEADLINE(30, 12, 2222);

        SqlCommands sqlCommands = new SqlCommands();

        Class<?>[] entities = databaseEntities();

        for (Class<?> entity : entities) {
            Class<?> cls = entity;

            SqlCommands.CreateTable dbTable = sqlCommands.new CreateTable(cls.getSimpleName());

            while (cls != null) {
                Field[] fields = cls.getDeclaredFields();

                /*String info = "+++++++++++++++++++++++++++++++++\n";
                info += cls.getName() + "\n";

                for (Field field : fields) {
                    info += ".\n";
                    info += field.toString() + "\n";
                    info += "NAME: " + field.getName() + "\n";
                    info += "TYPE: " + field.getType().getName() + "\n";
                    info += "MODIFIERS: " + Modifier.toString(field.getModifiers()) + "\n";
                }

                Logger.print(info + "\n");*/

                /*
                    private java.lang.Long com.blogspot.gm4s.gmutileexample.Entity0.longField0
                    NAME: longField0
                    TYPE: java.lang.Long
                    MODIFIERS: private
                */

                for (Field field : fields) {
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

                    else
                        throw new IllegalArgumentException("only supported field types are: [short, int, long, float, double, String, boolean] for field name: [" + field.getName() + "], given type is: [" + fieldType + "]");

                    dbTable.addColumn(field.getName(), dataType, getFieldConstraints(field));
                }

                cls = cls.getSuperclass();
                if (cls == Object.class) break;
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
            SqlCommands.DropTable tasksTable = sqlCommands.new DropTable(entity.getSimpleName());
            try {
                db.execSQL(tasksTable.getCode());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        onCreate(db);
    }

    /*--- SQL OP ---------------------------------------------------------------------------------*/

    /* SELECT */
    public <T> T select(@NotNull Class<T> entity, @NotNull String whereClause, String orderBy) {
        T item = select(entity, collectColumns(entity), whereClause, orderBy);
        return item;
    }

    public <T> T select(@NotNull Class<T> entity, @NotNull String[] specialColumns, String whereClause, String orderBy) {
        JSONArray result = doSelect(entity, specialColumns, whereClause, orderBy);

        T item = new Gson().fromJson(result.toString(), entity);
        return item;
    }

    public <T> List<T> select(@NotNull Class<T> entity, @NotNull TypeToken<List<T>> typeToken, String whereClause, String orderBy) {
        List<T> lst = select(entity, typeToken, collectColumns(entity), whereClause, orderBy);
        return lst;
    }

    public <T> List<T> select(@NotNull Class<T> entity, @NotNull TypeToken<List<T>> typeToken, @NotNull String[] specialColumns, String whereClause, String orderBy) {
        JSONArray result = doSelect(entity, specialColumns, whereClause, orderBy);

        //Type typeOfT = new TypeToken<List<T>>(){}.getType();
        Type typeOfT = typeToken.getType();
        List<T> lst = new Gson().fromJson(result.toString(), typeOfT);
        return lst;
    }

    public <T> JSONArray doSelect(@NotNull Class<T> entity, @NotNull String[] specialColumns, String whereClause, String orderBy) {
        SQLiteDatabase db = mDatabase.getReadableDatabase();

        //select columnsNames from tableName where columnName1=value1 AND columnName1=value1
        Cursor query = db.query(
                entity.getSimpleName(),
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
                            Log.e("*** " + Database.class.getSimpleName(), "there is no field with the name: [" + colName + "] in [" + entity.getSimpleName() + "] or its super classes");
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

                query.close();
            }
        }

        return result;
    }

    private <T> String[] collectColumns(@NotNull Class<T> entity) {
        List<String> specialColumns = new ArrayList<>();

        Class<?> cls = entity;
        while (cls != null) {
            Field[] fields = cls.getDeclaredFields();

            for (Field field : fields) {
                specialColumns.add(field.getName());
            }

            cls = cls.getSuperclass();
            if (cls == Object.class) break;
        }

        return specialColumns.toArray(new String[0]);
    }


    /* INSERT */
    public <T> List<Long> insert(@NotNull List<T> data) {
        List<Long> ids = new ArrayList<>();

        for (T d : data) {
            ContentValues values = collectContentValues(d);
            long id = insert(d.getClass(), values);
            ids.add(id);
        }

        return ids;
    }

    public long insert(Class<?> entity, ContentValues values) {
        SQLiteDatabase db = mDatabase.getReadableDatabase();

        //insert into TableName(column1, ..., columnN) values(value1, ..., valuesN)
        long rowID = db.insert(
                entity.getSimpleName(),
                null,
                values
        );

        return rowID;
    }

    public <T> ContentValues collectContentValues(@NotNull T data) {
        Class<?> cls = data.getClass();
        ContentValues values = new ContentValues();

        while (cls != null) {
            Field[] fields = cls.getDeclaredFields();

            for (Field field : fields) {
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
                    }

                    else if (fieldType == float.class || fieldType == Float.class)
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


    /* UPDATE */
    public <T> int update(@NotNull T data) {
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

        if (primaryFieldsValues.size() > 0) {
            String whereClause = "";
            Set<Map.Entry<String, Object>> entries = primaryFieldsValues.entrySet();
            for (Map.Entry<String, Object> entry : entries) {
                whereClause += entry.getKey() + "=" + entry.getValue();
            }
            return update(data, whereClause);
        } else {
            return 0;
        }
    }

    public <T> int update(@NotNull T data, String whereClause) {
        List<T> dataList = new ArrayList<>();
        dataList.add(data);
        return update(dataList, whereClause);
    }

    public <T> int update(@NotNull List<T> data, String whereClause) {
        int r = 0;
        for (T d : data) {
            ContentValues values = collectContentValues(d);
            int r0 = update(d.getClass(), values, whereClause);
            r += r0;
        }
        return r;
    }

    public int update(Class<?> entity, ContentValues values, String whereClause) {
        SQLiteDatabase db = mDatabase.getReadableDatabase();

        return db.update(
                entity.getSimpleName(),
                values,
                whereClause,
                null
        );
    }


    /* DELETE */
    public <T> int delete(@NotNull T data) {
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

        if (primaryFieldsValues.size() > 0) {
            String whereClause = "";
            Set<Map.Entry<String, Object>> entries = primaryFieldsValues.entrySet();
            for (Map.Entry<String, Object> entry : entries) {
                whereClause += entry.getKey() + "=" + entry.getValue();
            }
            return delete(data.getClass(), whereClause);
        } else {
            return 0;
        }
    }

    public int delete(Class<?> entity, String whereClause) {
        SQLiteDatabase db = mDatabase.getReadableDatabase();

        return db.delete(
                entity.getSimpleName(),
                whereClause,
                null
        );
    }
}
