package ru.startandroid;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import ru.startandroid.User;
import android.provider.BaseColumns;

public class Database extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION =1;
    private SQLiteDatabase database;

    public static final String TABLE_NAME = "Users";
    public static final String COLUMN_NAME_LOGIN= "Login";
    public static final String COLUMN_NAME_PASSWORD = "Password";

    String SQL_CREATE = "CREATE TABLE" + TABLE_NAME + "(" + COLUMN_NAME_LOGIN + "TEXT PRIMARY KEY" +
            COLUMN_NAME_PASSWORD + "TEXT" + ");";

    String SQL_CREATE_USERS = SQL_CREATE;

    public Database(Context context, String DATABASE_NAME){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        database.execSQL(SQL_CREATE_USERS);
//        database = this.getWritableDatabase();
//        ContentValues contentValues = new ContentValues();
//        contentValues.put(COLUMN_NAME_LOGIN, "admin");
//        contentValues.put(COLUMN_NAME_PASSWORD, "admin");
//        database.insert(TABLE_NAME,null,contentValues);
        System.out.println("Database created");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public void addUser(User user){
        database = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_NAME_LOGIN, user.getLogin());
        contentValues.put(COLUMN_NAME_PASSWORD, user.getPassword());
        database.insert(TABLE_NAME, null, contentValues);
        System.out.println("User added");
    }

    public User getUser(String login){
        database = this.getReadableDatabase();
        User user = null;
        Cursor cursor = database.query(TABLE_NAME, new String[]{COLUMN_NAME_LOGIN, COLUMN_NAME_PASSWORD},
                COLUMN_NAME_LOGIN + " = " + "'" + login + "'", null, null, null, null);
        if(cursor.moveToNext()){
            user = new User(cursor.getString(0), cursor.getString(1));
            System.out.println("User found" + cursor.getString(0));
            System.out.println("Password" + cursor.getString(1));
        }
        return user;
    }

}
