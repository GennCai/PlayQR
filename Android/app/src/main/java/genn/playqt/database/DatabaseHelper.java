package genn.playqt.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static DatabaseHelper dbHelper;
    public static DatabaseHelper getInstance(Context context) {
        return dbHelper == null ? new DatabaseHelper(context, "PlayQR", null, 2) : dbHelper;
    }
    Context mContext;
    public static final String CREATE_USER = "create table users (" +
            " id integer primary key autoincrement," +
            " username text unique)";
    public static final String CREATE_IMAGE = "create table images(" +
            " id integer primary key autoincrement," +
            " path text unique," +
            " data text," +
            " time text," +
            " location text," +
            " is_uploaded boolean," +
            " user_id integer references users(id))";

    //"drop table if exists users"

    public DatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        mContext = context;
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_USER);
        db.execSQL(CREATE_IMAGE);
        Toast.makeText(mContext, "Database Create Succeeded!", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        switch (oldVersion) {
            case 1:
                db.execSQL("alter table images add column is_uploaded boolean");
            case 2:
            case 3:
        }
    }
}