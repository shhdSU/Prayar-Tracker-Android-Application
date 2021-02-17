package com.example.prayartracker;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import androidx.annotation.Nullable;
public class DataBase extends SQLiteOpenHelper {
    public static final String DBNAME = "PrayerDB.db";
    public DataBase(Context context) {
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
        contentValues.put("email", email);
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
}