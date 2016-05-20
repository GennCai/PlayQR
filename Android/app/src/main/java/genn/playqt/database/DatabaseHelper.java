package genn.playqt.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static DatabaseHelper dbHelper;
    public static final String TAG = "ConstraintException";
    public static DatabaseHelper getInstance(Context context) {
        if (dbHelper == null) {
            dbHelper = new DatabaseHelper(context, "PlayQR", null, 3);
        }
        return dbHelper;
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

    private DatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
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
                db.execSQL("drop table if exists users");
                db.execSQL("drop table if exists images");
                onCreate(db);
            case 3:
        }
    }

    public int getUserId(SQLiteDatabase db,  String username) {
        int id;
        Cursor cursor = db.rawQuery("select id from users where username=?", new String[]{username});
        id = cursor.moveToFirst() ? cursor.getInt(cursor.getColumnIndex("id")) : -1 ;
        cursor.close();
        return id;
    }

    public void insertImage(Image image) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.execSQL("insert into images (path, data, time, location, is_uploaded, user_id) " +
                "values(?, ?, ?, ? ,? ,?)", new Object[]{image.getName(), image.getDecodeData(), image.getTakeTime()
        , image.getLocation(), false, User.getInstance().getId()});
    }

    public void updateImage(Image image, String oldName) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        try {
            db.execSQL("update images set path=?, data=?, time=?, location=? where path=?",
                    new String[]{image.getName(), image.getDecodeData(), image.getTakeTime(), image.getLocation(), oldName});
        } catch (SQLiteConstraintException e) {
            Log.e(TAG, "您设置的图片名称已经存在!");
        }
    }

    public void updateImageState(String imageName, int state) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.execSQL("update images set is_uploaded=" + state);
    }
    public Image queryImage(String fileName) {
        Image image = null;
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from images where path=?", new String[]{fileName});
        if (cursor.moveToFirst()) {
            image = new Image();
            image.setName(cursor.getString(cursor.getColumnIndex("path")));
            image.setDecodeData(cursor.getString(cursor.getColumnIndex("data")));
            image.setTakeTime(cursor.getString(cursor.getColumnIndex("time")));
            image.setLocation(cursor.getString(cursor.getColumnIndex("location")));
            image.setUploaded(cursor.getInt(cursor.getColumnIndex("is_uploaded"))==1);
        }
        cursor.close();
        return image;
    }

    public List<Image> queryImages() {
        List<Image> images = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from images where user_id=?", new String[]{User.getInstance().getId() + ""});
        if (cursor.moveToFirst()) {
            do {
                Image image = new Image();
                image.setName(cursor.getString(cursor.getColumnIndex("path")));
                image.setDecodeData(cursor.getString(cursor.getColumnIndex("data")));
                image.setTakeTime(cursor.getString(cursor.getColumnIndex("time")));
                image.setLocation(cursor.getString(cursor.getColumnIndex("location")));
                image.setUploaded(cursor.getInt(cursor.getColumnIndex("is_uploaded"))==1);
                images.add(image);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return images;
    }

    public void deleteImage(String imageName) {
        dbHelper.getWritableDatabase().execSQL("delete from images where path=?", new String[]{imageName});
    }
}
