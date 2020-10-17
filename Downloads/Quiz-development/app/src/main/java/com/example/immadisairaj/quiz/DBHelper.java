package com.example.immadisairaj.quiz;
import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;


public class DBHelper extends SQLiteOpenHelper {

        public static final String DATABASE_NAME = "MyDBName.db";
        public static final String USERS_TABLE_NAME = "users";
        public static final String USERS_COLUMN_ID = "id";
        public static final String USERS_COLUMN_NAME = "name";
        public static final String USERS_COLUMN_SCORE = "score";

        public DBHelper(Context context) {
            super(context, DATABASE_NAME , null, 1);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            // TODO Auto-generated method stub
            db.execSQL(
                    "create table users " +
                            "(id integer primary key, name text,score integer)"
            );
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            // TODO Auto-generated method stub
            db.execSQL("DROP TABLE IF EXISTS users");
            onCreate(db);
        }

        public boolean insertUser (String name, Integer score) {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put("name", name);
            contentValues.put("score",score);
            db.insert("users", null, contentValues);
            return true;
        }


        public boolean updateUser (String name,Integer score) {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put("name", name);
            contentValues.put("score",score);
            db.update("users", contentValues, "name = ? ", new String[] { name } );
            return true;
        }

        public void deleteUser() {
            SQLiteDatabase db = this.getWritableDatabase();
            db.delete("users",null,null);
        }

        public ArrayList<String> getAllUsers() {
            ArrayList<String> array_list = new ArrayList<String>();

            SQLiteDatabase db = this.getReadableDatabase();
            Cursor res =  db.rawQuery( "select * from users order by score desc limit 3", null );
            res.moveToFirst();

            while(res.isAfterLast() == false){
                array_list.add(res.getString(res.getColumnIndex(USERS_COLUMN_NAME)));
                array_list.add(res.getString(res.getColumnIndex(USERS_COLUMN_SCORE)));
                res.moveToNext();
            }
            return array_list;
        }
        public int getScore(String name) {
            SQLiteDatabase db = this.getReadableDatabase();
            Log.i("INSERTED",name);
            Cursor res =  db.rawQuery( "select * from users where name =?",  new String[] { String.valueOf(name) } );
            res.moveToFirst();

            Log.i("INSERTED",DatabaseUtils.dumpCursorToString(res));
            Log.i("INSERTED",res.getString(res.getColumnIndex(USERS_COLUMN_SCORE)));

                int score = Integer.parseInt(res.getString(res.getColumnIndex(USERS_COLUMN_SCORE)));
                Log.i("INSERTED",String.valueOf(score));
                return score;


        }
    }

