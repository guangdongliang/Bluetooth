package hust.example.bluetooth;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.HashMap;

/**
 * Created by 相信小东 on 2016/5/5.
 */
public class BlueDatabase {
    private final DatabaseHelper dbHelper;
    public BlueDatabase(Context context){
        dbHelper=new DatabaseHelper(context);
   /*     SQLiteDatabase database=dbHelper.getWritableDatabase();
        Cursor cursor=database.rawQuery("select * from " + DatabaseHelper.DEVICE_LOCK_TABLE_NAME, null);
        if(cursor.getCount()==0){
            insert();
        }*/

    }

    public void insert(String address,String lockNames,String messages)
    {
        String sql="insert into "+DatabaseHelper.DEVICE_LOCK_TABLE_NAME;
        sql+=" (address,lock_list,messages) values(?,?,?)";
        SQLiteDatabase sqLiteDatabase=dbHelper.getWritableDatabase();
        sqLiteDatabase.execSQL(sql,new String[]{address,lockNames,messages});
        sqLiteDatabase.close();
    }
    public void insertLockState(String name,String state)
    {
        String sql="insert into "+DatabaseHelper.LOCK_STATE_TABLE;
        sql+="(lock_name,state) values(?,?)";
        SQLiteDatabase sqLiteDatabase=dbHelper.getWritableDatabase();
        sqLiteDatabase.execSQL(sql,new String[]{name,state});
        sqLiteDatabase.close();
    }

    public void update(String address,String lockNames)
    {
        SQLiteDatabase sqLiteDatabase=dbHelper.getWritableDatabase();
        String sql=("update "+DatabaseHelper.DEVICE_LOCK_TABLE_NAME+" set  lock_list=? where address=?");
        sqLiteDatabase.execSQL(sql,
                new String[]{lockNames,address});
        sqLiteDatabase.close();
    }
    public void updateMessages(String address,String messages)
    {
        SQLiteDatabase sqLiteDatabase=dbHelper.getWritableDatabase();
        String sql=("update "+DatabaseHelper.DEVICE_LOCK_TABLE_NAME+" set  messages=? where address=?");
        sqLiteDatabase.execSQL(sql,
                new String[]{messages,address});
        sqLiteDatabase.close();
    }

    public void updateLockState(String name,String state)
    {
        SQLiteDatabase sqLiteDatabase=dbHelper.getWritableDatabase();
        String sql=("update "+DatabaseHelper.LOCK_STATE_TABLE+" set state=? where lock_name=?");
        sqLiteDatabase.execSQL(sql,
                new String[]{state,name});
        sqLiteDatabase.close();
    }
    public String query(String address){

        SQLiteDatabase sqLiteDatabase=dbHelper.getWritableDatabase();

        Cursor cursor=sqLiteDatabase.rawQuery("select * from " + DatabaseHelper.DEVICE_LOCK_TABLE_NAME + " where address=" + "'" + address + "'", null);
        if(cursor.getCount()>0){
            cursor.moveToFirst();
            return cursor.getString(2);

        }else{
            return "null";

        }
    }

    public String queryMessages(String address){

        SQLiteDatabase sqLiteDatabase=dbHelper.getWritableDatabase();

        Cursor cursor=sqLiteDatabase.rawQuery("select * from " + DatabaseHelper.DEVICE_LOCK_TABLE_NAME + " where address=" + "'" + address + "'", null);
        if(cursor.getCount()>0){
            cursor.moveToFirst();
            return cursor.getString(3);
        }else{
            return "null";

        }
    }
    public String queryLockState(String name){
        SQLiteDatabase sqLiteDatabase=dbHelper.getWritableDatabase();
        Cursor cursor=sqLiteDatabase.rawQuery("select * from "+DatabaseHelper.LOCK_STATE_TABLE+" where lock_name="+"'"+name+"'",null);
        if(cursor.getCount()>0){
            cursor.moveToFirst();
            return cursor.getString(2);

        }else{
            return "null";

        }
    }

    public HashMap<String,Boolean> queryAllLockState(){
        SQLiteDatabase sqLiteDatabase=dbHelper.getWritableDatabase();
        Cursor cursor=sqLiteDatabase.rawQuery("select * from "+DatabaseHelper.LOCK_STATE_TABLE ,null);
        int a=cursor.getCount();
        Log.d("个数",String.valueOf(a));
        if(cursor.getCount()>0){
            HashMap<String,Boolean> hashMap=new HashMap<>();
            for(cursor.moveToFirst();!cursor.isAfterLast();cursor.moveToNext()){
                hashMap.put(cursor.getString(1),cursor.getInt(2) == 1 ? true : false);
            }

            return hashMap;

        }else{
            return null;

        }
    }

    public void destroy(){ dbHelper.close();}
}
