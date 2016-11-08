package hust.example.bluetooth;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by 相信小东 on 2016/5/5.
 */
public class DatabaseHelper  extends SQLiteOpenHelper{
    public static final String BLUE_DATABASE_NAME="blue";
    public static final String DEVICE_LOCK_TABLE_NAME="device_lock";
    public static final String LOCK_STATE_TABLE="lock_state_table";
    public static final String CREATE_LOCK_TABLE="create table "+DEVICE_LOCK_TABLE_NAME+" (_id integer primary key autoincrement," +
            "address text," +
            "lock_list text," +
            "messages text)";
    public static final String CREATE_LOCK_STATE_TABLE="create table "+LOCK_STATE_TABLE+" (_id integer primary key autoincrement," +
            "lock_name text," +
            "state INTEGER)";

    public DatabaseHelper(Context context) {
        super(context, BLUE_DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_LOCK_TABLE);
        db.execSQL(CREATE_LOCK_STATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}

