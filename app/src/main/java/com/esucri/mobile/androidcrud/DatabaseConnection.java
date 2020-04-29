package com.esucri.mobile.androidcrud;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

public class DatabaseConnection {

    private static DatabaseConnection gw;
    private SQLiteDatabase db;

    private DatabaseConnection(Context ctx){
        DbHelper helper = new DbHelper(ctx);
        db = helper.getWritableDatabase();
    }

    public static DatabaseConnection getInstance(Context ctx){
        if(gw == null)
            gw = new DatabaseConnection(ctx);
        return gw;
    }

    public SQLiteDatabase getDatabase(){
        return this.db;
    }
}