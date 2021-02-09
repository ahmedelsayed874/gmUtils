package com.blogspot.gm4s1.gmutils.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.blogspot.gm4s1.gmutils.DateOp;
import com.blogspot.gm4s1.gmutils.Logger;

import org.jetbrains.annotations.NotNull;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.TypeVariable;

public class BaseDatabase<C> extends SQLiteOpenHelper {
    public static final String DB_NAME = "tasks_db";
    public static final int DB_VERSION = 2;

    public BaseDatabase(Context context, @NotNull Class<C> dataClass) {
        super(context, DB_NAME, null, DB_VERSION);

        DateOp dateOp = Logger.GET_LOG_DEADLINE();
        Logger.SET_LOG_DEADLINE(30, 12, 2222);

        Class<?> cls =  dataClass;
        while (cls != null) {
            Method[] declaredMethods = cls.getDeclaredMethods();
            Field[] fields = cls.getDeclaredFields();

            String info = "+++++++++++++++++++++++++++++++++\n";
            info += cls.getName() + "\n";

            for (Field field : fields) {
                info += ".\n";
                info += field.toString() + "\n";
                info += "NAME: " + field.getName() + "\n";
                info += "TYPE: " + field.getType().getName() + "\n";
                info += "MODIFIERS: " + Modifier.toString(field.getModifiers()) + "\n";

            }

            cls = cls.getSuperclass();

            Logger.print(info + "\n");
        }

        Logger.SET_LOG_DEADLINE(dateOp);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        /*SqlCommands sqlCommands = new SqlCommands();

        SqlCommands.CreateTable tasksTable = sqlCommands.new CreateTable(TasksDB_tables.TASKS.TABLE_NAME);
        tasksTable.addColumn(TasksDB_tables.TASKS._ID                 , DataTypes.INTEGER, new Constraints[]{ Constraints.PRIMARY_Key, Constraints.AUTOINCREMENT });

        tasksTable.addColumn(TasksDB_tables.TASKS.START_DATE_I        , DataTypes.INTEGER, null);
        tasksTable.addColumn(TasksDB_tables.TASKS.START_TIME_I        , DataTypes.INTEGER, null);

        tasksTable.addColumn(TasksDB_tables.TASKS.END_DATE_I          , DataTypes.INTEGER, null);
        tasksTable.addColumn(TasksDB_tables.TASKS.END_TIME_I          , DataTypes.INTEGER, null);

        tasksTable.addColumn(TasksDB_tables.TASKS.INTERVAL_I          , DataTypes.INTEGER, null);

        tasksTable.addColumn(TasksDB_tables.TASKS.TASK_TEXT_S         , DataTypes.TEXT   , null);
        tasksTable.addColumn(TasksDB_tables.TASKS.TASK_AUDIO_PATH_S   , DataTypes.TEXT   , null);

        tasksTable.addColumn(TasksDB_tables.TASKS.BACKGROUND_COLOR_I  , DataTypes.INTEGER, null);
        tasksTable.addColumn(TasksDB_tables.TASKS.TEXT_COLOR_I        , DataTypes.INTEGER, null);

        tasksTable.addColumn(TasksDB_tables.TASKS.ALLOW_NOTIFICATION_I, DataTypes.INTEGER, null);
        tasksTable.addColumn(TasksDB_tables.TASKS.TONE_AUDIO_URI_S    , DataTypes.TEXT   , null);

        tasksTable.addColumn(TasksDB_tables.TASKS.REPETITION_TYPE_I   , DataTypes.INTEGER, null);
        tasksTable.addColumn(TasksDB_tables.TASKS.PARENT_ID_I         , DataTypes.INTEGER, null);

        db.execSQL(tasksTable.getCode());*/
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        /*SqlCommands sqlCommands = new SqlCommands();

        SqlCommands.DropTable tasksTable = sqlCommands.new DropTable(TasksDB_tables.TASKS.TABLE_NAME);
        db.execSQL(tasksTable.getCode());

        onCreate(db);*/
    }


    //---------------------------------------------------------------------//
    public static Cursor query(Context context, String[] columns, String whereClause, String orderBy) {
        /*TasksDB task_db = new TasksDB(context);
        SQLiteDatabase db = task_db.getReadableDatabase();

        *//*select columnsNames from tableName where columnName1=value1 AND columnName1=value1*//*
        Cursor query = db.query(
                TasksDB_tables.TASKS.TABLE_NAME,
                columns,
                whereClause,
                null, null, null,
                orderBy);

        return query;*/
        return null;
    }
    public static void insert(Context context, ContentValues values) {
        /*TasksDB task_db = new TasksDB(context);
        SQLiteDatabase db = task_db.getWritableDatabase();

        *//*insert into TableName(column1, ..., columnN) values(value1, ..., valuesN)*//*
        db.insert(TasksDB_tables.TASKS.TABLE_NAME, null, values);*/
    }
    public static void update(Context context, ContentValues values, String targetID) {
        /*TasksDB task_db = new TasksDB(context);
        SQLiteDatabase db = task_db.getWritableDatabase();

        db.update(
                TasksDB_tables.TASKS.TABLE_NAME,
                values,
                TasksDB_tables.TASKS._ID + "=?",
                new String[] { targetID }
        );*/
    }
    public static void delete(Context context, int taskID) {
        /*TasksDB task_db = new TasksDB(context);
        SQLiteDatabase db = task_db.getWritableDatabase();

        db.delete(
                TasksDB_tables.TASKS.TABLE_NAME,
                TasksDB_tables.TASKS._ID + "=?",
                new String[] { taskID + "" }
        );*/
    }
}
