package com.gettext;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
 
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
 
public class MyDatabase extends SQLiteOpenHelper {
 
	
    private static final int DATABASE_VERSION = 2;
 
    // Database Name
    private static final String DATABASE_NAME = "GetTextData";
 
    // Table name
    private static final String TABLE_TEXT = "TextTable";
 
    // Table Columns names
    private static final String TEXT_CONTENT = "nothing";
    private static final String TEXT_DATETIME = "current";
    private static final String TEXT_TRANSLATED = "abcd";
 
    
 
    public MyDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
 
    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        
    	String CREATE_TABLE_TEXT = "CREATE TABLE " + TABLE_TEXT + "("
                + TEXT_DATETIME + " TEXT," + TEXT_CONTENT + " TEXT,"   + TEXT_TRANSLATED + " TEXT" + ")";
    	db.execSQL(CREATE_TABLE_TEXT);
    }
 
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TEXT); 
        onCreate(db);
    }
 
    
 
    
    void addText(String str, String str1) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        long date = System.currentTimeMillis(); 
        SimpleDateFormat sdf = new SimpleDateFormat("MMM d, yyyy h:mm a");
        String dateString = sdf.format(date); 
        values.put(TEXT_DATETIME, dateString);
        values.put(TEXT_CONTENT, str);
        if("".equals(str1)){
        	str1 = "No translated Required";
        }
        values.put(TEXT_TRANSLATED, "Tranlated Text: " + str1);
        db.insert(TABLE_TEXT, null, values);
        db.close();
    }
 /*
    void addTranslation(String str) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(TEXT_TRANSLATED, str);
        db.insert(TABLE_TEXT, null, values);
        db.close();
    }
    */
   
    public String getAllText() {
        List<String> list = new ArrayList<String>();
        String selectQuery = "SELECT  * FROM " + TABLE_TEXT;
 
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                list.add(cursor.getString(0));
                list.add(" : ");
                list.add("\t");
            	list.add(cursor.getString(1));
            	list.add("\n");
            	list.add(cursor.getString(2));
            	list.add("\n");
            
            } while (cursor.moveToNext());
        }
        String s = list.toString();
        s = s.replaceAll(",","");
        return s;
    }
   
 
}

