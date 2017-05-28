package com.dvlchm.fsa;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by User on 5/27/2017.
 */
public class AssignmentDataBaseAdapter {
    static final String DATABASE_NAME = "fsa.db";
    static final int DATABASE_VERSION = 1;
    private String[] allColumns = {"ID_USER", "ID_LIST", "ID_LOKASI", "PLACE","DONE"
    ,"KEB_TM","KEB_AM","N_KUALITAS","ALASAN","REAL_ADDRESS","IMAGE","LAT","LONG"};
    // TODO: Create public field for each column in your table.
    // SQL Statement to create a new database.
    static final String DATABASE_CREATE = "create table "+"ASSIGNMENT"+
            "( " +"ID_LIST"+" text primary key,"+ "ID_USER text, ID_LOKASI text, PLACE text, DONE integer" +
            "KEB_TM integer, KEB_AM integer, N_KUALITAS integer, ALASAN text, REAL_ADDRESS text" +
            "IMAGE text, LAT text, LONG text); ";
    // Variable to hold the database instance
    public SQLiteDatabase db;
    // Context of the application using the database.
    private Context context;
    // Database open/upgrade helper
    private DataBaseHelper dbHelper;
    public  AssignmentDataBaseAdapter(Context _context)
    {
        context = _context;
        dbHelper = new DataBaseHelper(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    public  AssignmentDataBaseAdapter open() throws SQLException

    {
        db = dbHelper.getWritableDatabase();
        return this;
    }
    public void close()
    {
        db.close();
    }

    public  SQLiteDatabase getDatabaseInstance()
    {
        return db;
    }

    public void insertEntry(String id_list,String tempat, String id_lokasi, String id)
    {
        ContentValues newValues = new ContentValues();
        // Assign values for each row.
        newValues.put("ID_USER", id);
        newValues.put("PLACE", tempat);
        newValues.put("ID_LOKASI", id_lokasi);
        newValues.put("ID_LIST", id_list);
        newValues.put("DONE",0);
        // Insert the row into your table
        long errorcode = db.insert("ASSIGNMENT", null, newValues);
        //if(errorcode==-1)
        //Toast.makeText(context, "error = "+tempat, Toast.LE`NGTH_LONG).show();
    }
    public int deleteEntry(String id_list)
    {
        //String id=String.valueOf(ID);
        String where="ID-LIST="+id_list;
        // Toast.makeText(context, "Number fo Entry Deleted Successfully : "+numberOFEntriesDeleted, Toast.LENGTH_LONG).show();
        return db.delete("ASSIGNMENT", where, null);
    }

    public List<AssignmentObject> getAllEntry(String username)
    {
        String id= cek_username(username);
        List<AssignmentObject> tasks=new ArrayList<AssignmentObject>();
        Cursor cursor = db.query("ASSIGNMENT",allColumns,"ID_USER="+id,null,null,null,null);
        cursor.moveToFirst();
        while(!cursor.isAfterLast())
        {
            Log.e("get data","getAllentry");
            AssignmentObject task = cursorToComment(cursor);
            tasks.add(task);
            cursor.moveToNext();
        }
        return tasks;
    }

    private String cek_username(String username) {
        LoginDataBaseAdapter login_db = new LoginDataBaseAdapter(context);
        Log.e("username",username);
        login_db.open();
        String id=login_db.cek_username(username);
        login_db.close();
        return id;
    }

    private AssignmentObject cursorToComment(Cursor cursor) {
        Log.e("get data","cursorToComment start");
        String id_user= cursor.getString(cursor.getColumnIndex("ID_USER"));
        String place= cursor.getString(cursor.getColumnIndex("PLACE"));
        String id_lokasi= cursor.getString(cursor.getColumnIndex("ID_LOKASI"));
        String id_list= cursor.getString(cursor.getColumnIndex("ID_LIST"));
        Integer done = cursor.getInt(cursor.getColumnIndex("DONE"));
        if(done==1) {
            String alasan, real_address, image,lat,longitude;
            Integer keb_TM,keb_AM,nil_K;
            if (!cursor.getString(cursor.getColumnIndex("KEB_TM")).equals("")) {
                keb_TM = cursor.getInt(cursor.getColumnIndex("KEB_TM"));
            }
            else keb_TM=0;
            if (!cursor.getString(cursor.getColumnIndex("KEB_AM")).equals("")) {
                keb_AM = cursor.getInt(cursor.getColumnIndex("KEB_AM"));
            }
            else keb_AM=0;
            if (!cursor.getString(cursor.getColumnIndex("N_KUALITAS")).equals("")) {
                nil_K = cursor.getInt(cursor.getColumnIndex("N_KUALITAS"));
            }
            else nil_K=0;
            if (!cursor.getString(cursor.getColumnIndex("ALASAN")).equals("")) {
                alasan = cursor.getString(cursor.getColumnIndex("ALASAN"));
            }
            else alasan="belum diisi";
            if (!cursor.getString(cursor.getColumnIndex("REAL_ADDRESS")).equals("")) {
                real_address = cursor.getString(cursor.getColumnIndex("REAL_ADDRESS"));
            }
            else real_address="belum diisi";
            if (!cursor.getString(cursor.getColumnIndex("IMAGE")).equals("")) {
                image = cursor.getString(cursor.getColumnIndex("IMAGE"));
            }
            else image="belum diisi";
            if (!cursor.getString(cursor.getColumnIndex("LAT")).equals("")) {
                lat = cursor.getString(cursor.getColumnIndex("LAT"));
            }
            else lat="belum diisi";
            if (!cursor.getString(cursor.getColumnIndex("LONG")).equals("")) {
                longitude = cursor.getString(cursor.getColumnIndex("LONG"));
            }
            else longitude="belum diisi";
            AssignmentObject task = new AssignmentObject(id_list,place,id_lokasi,id_user,done,keb_TM,keb_AM,nil_K,alasan,real_address,image);
            Log.e("get data","cursorToComment end");
            return task;
        }
        AssignmentObject task = new AssignmentObject(id_list,place,id_lokasi,id_user,done);
        Log.e("get data","cursorToComment end");
        return task;
    }

    public AssignmentObject getSinlgeEntry(String id_list)
    {
        Cursor cursor=db.query("ASSIGNMENT", null, "ID_LIST="+id_list, null, null, null, null);
        if(cursor.getCount()<1) // UserName Not Exist
        {
            cursor.close();
            return null;
        }
        cursor.moveToFirst();
        return cursorToComment(cursor);
    }

    public boolean cek_id_list(String s) {
        Cursor cursor=db.query("ASSIGNMENT", null, "ID_LIST="+s, null, null, null, null);
        if(cursor.getCount()<1) // task Not Exist
        {
            cursor.close();
            return true;
        }
        cursor.moveToFirst();
        Log.e("place",cursor.getString(cursor.getColumnIndex("PLACE")));
        return false;
    }

    public void updateDone(String id_list, String alasan, String Keb_TM, String Keb_AM,String n_kualitas, String lokasi, String Image)
    {
        ContentValues updatedValues = new ContentValues();
        // Assign values for each row.
        updatedValues.put("DONE", 1);
        updatedValues.put("KEB_TM", Keb_TM);
        updatedValues.put("KEB_AM", Keb_AM);
        updatedValues.put("N_KUALITAS", n_kualitas);
        updatedValues.put("REAL_ADDRESS", lokasi);
        updatedValues.put("IMAGE", Image);
//        updatedValues.put("LAT",);
//        updatedValues.put("LONG", 1);

        String where="ID_LIST = ?";
        db.update("ASSIGNMENT",updatedValues, where,new String[]{id_list});
    }

    public int delteTask(String id_assignment) {
        String where="ID-LIST="+id_assignment;
        // Toast.makeText(context, "Number fo Entry Deleted Successfully : "+numberOFEntriesDeleted, Toast.LENGTH_LONG).show();
        return db.delete("ASSIGNMENT", where, null);
    }
}
