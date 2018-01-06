package jymw.project.recommendclothes.main.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseClass {
    private String tableName;
    private SQLiteDatabase readableDb, writableDb;

    public DatabaseClass(Context context, String tableName) {
        Log.e("DatabaseClass", "super()");
        this.tableName = tableName;
        SQLiteOpenHelper sqLiteOpenHelper = new DatabaseOpenHelper(context, tableName.concat(".db"));
        readableDb = sqLiteOpenHelper.getReadableDatabase();
        writableDb = sqLiteOpenHelper.getWritableDatabase();
    }

    /**
     * 행 갯수를 조회하는 함수
     * @return 행 갯수
     */
    public int getRowCount() {
        String sql = "select * from " + tableName + ";";
        Cursor cursor = readableDb.rawQuery(sql, null);
        int count = cursor.getCount();
        cursor.close();
        return count;
    }

    /**
     * 위도, 경도를 저장하는 함수
     *
     * @param latitude 위도
     * @param longitude 경도
     */
    public void insertData(Double latitude, Double longitude) {
        String sql = "insert into " + tableName + " values(NULL, '" + String.valueOf(latitude) + "', '" + String.valueOf(longitude) + "');";
        writableDb.execSQL(sql);
    }

    /**
     * 열 이름과 id값으로 저장된 값을 검색하는 함수
     *
     * @param columnName 열 이름
     * @param id id 값
     * @return Double형 숫자
     */
    public Double selectData(String columnName, int id) {
        String sql = "select " + columnName + " from " + tableName + " where id = " + String.valueOf(id) + ";";
        Cursor cursor = readableDb.rawQuery(sql, null);
        Double data = null;
        if (cursor.moveToPosition(id - 1)) {
            data = cursor.getDouble(id - 1);
        }
        cursor.close();
        return data;
    }
}