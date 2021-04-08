package com.example.prayartracker;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;
public class DatabaseHelper extends SQLiteOpenHelper {
    public static final String DBNAME = "PrayerDB.db";
    public DatabaseHelper(Context context) {
        super(context, "PrayerDB.db", null, 1);
    }
    @Override
    public void onCreate(SQLiteDatabase PDB) {
        PDB.execSQL("create Table users(email TEXT primary key, username TEXT, password TEXT)");
    }
    @Override
    public void onUpgrade(SQLiteDatabase PDB, int i, int i1) {
        PDB.execSQL("drop Table if exists users");
    }
    public Boolean insertData(String email, String username, String password){
        SQLiteDatabase PDB = this.getWritableDatabase();
        ContentValues contentValues= new ContentValues();
        contentValues.put("email", email.toLowerCase());
        contentValues.put("username", username);
        contentValues.put("password", password);
        long result = PDB.insert("users", null, contentValues);
        if(result==-1) return false;
        else
            return true;
    }
    public Boolean checkemail(String email) {
        SQLiteDatabase PDB = this.getWritableDatabase();
        Cursor cursor = PDB.rawQuery("Select * from users where email = ?", new String[]{email});
        if (cursor.getCount() > 0)
            return true;
        else
            return false;
    }
    public Boolean checkemailpassword(String email, String password){
        SQLiteDatabase PDB = this.getWritableDatabase();
        Cursor cursor = PDB.rawQuery("Select * from users where email = ? and password = ?", new String[] {email,password});
        if(cursor.getCount()>0)
            return true;
        else
            return false;
    }
    public boolean validateLogin(String email, String password){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cr = db.rawQuery("Select email, password from users where email = ? and password = ?", new String[] {email.toLowerCase(),password});
        Log.d("a", ""+cr.getCount());
        if(cr.getCount()>0){
            return true;
        }
        else return false;
    }
}