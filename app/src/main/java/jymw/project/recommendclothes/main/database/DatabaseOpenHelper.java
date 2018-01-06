package jymw.project.recommendclothes.main.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseOpenHelper extends SQLiteOpenHelper {
    private String tableName;

    public DatabaseOpenHelper(Context context, String name) {
        super(context, name, null, 1);
        tableName = name.substring(0, name.indexOf("."));
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "create table " + tableName + " (" + "id integer primary key autoincrement" + ", ";
        switch (tableName) {
            case "weatherInfo":
                sql += "latitude text not null" + ", " + "longitude text not null" + ");";
                break;
            default:
                break;
        }
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}