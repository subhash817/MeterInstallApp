package com.rjdev.meterinstallapp.DBHelper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.rjdev.meterinstallapp.Model.InstallDetailModel;
import com.rjdev.meterinstallapp.Model.MeterModel;

public class MeterDatabaseHelper extends SQLiteOpenHelper {

    private static final String TAG = "DatabaseHelper";

    private static final String DATABASE_NAME = "meterdetail.db";
    private static final String TABLE_NAME = "meterdetails";
    public static final String COL0 = "Id_No";
    public static final String COL1 = "Meter_No";
    public static final String COL2 = "Installation_Date";
    public static final String COL3 = "M_STATUS";



    public MeterDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "CREATE TABLE " +
                TABLE_NAME + " ( " +
                COL0 + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL1 + " TEXT, " +
                COL2 + " TEXT, " +
                COL3 + " TEXT )";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    /**
     * Insert a new installDeatailsModel into the database
     * @param installDeatailsModel
     * @return
     */
    public boolean addDetails(MeterModel installDeatailsModel) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL1, installDeatailsModel.getMeter_no());
        contentValues.put(COL2, installDeatailsModel.getInstallation_date());
        contentValues.put(COL3, installDeatailsModel.getM_status());
        long result = db.insert(TABLE_NAME, null, contentValues);

        if (result == -1) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * Retrieve all contacts from database
     * @return
     */
    public Cursor getAllDetails(){
        SQLiteDatabase db = this.getWritableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_NAME, null);
    }

    /**
     * Update a installDeatailsModel where id = @param 'id'
     * Replace the current installDeatailsModel with @param 'installDeatailsModel'
     * @param installDeatailsModel
     * @param id
     * @return
     */
    public boolean updateDetails(MeterModel installDeatailsModel, String id){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL1, installDeatailsModel.getMeter_no());
        contentValues.put(COL2, installDeatailsModel.getInstallation_date());
        contentValues.put(COL3, installDeatailsModel.getM_status());


        int update = db.update(TABLE_NAME, contentValues, COL1 + " = ? ", new String[] {id} );

        if(update != 1) {
            return false;
        }
        else{
            return true;
        }
    }

    /**
     * Retrieve the installDeatailsModel unique id from the database using @param
     * @param installDeatailsModel
     * @return
     */
    public Cursor getDetailsID(MeterModel installDeatailsModel){
        SQLiteDatabase db = this.getWritableDatabase();
        String sql = "SELECT * FROM " + TABLE_NAME  +
                " WHERE " + COL1 + " = '" + installDeatailsModel.getMeter_no() + "'";
        return db.rawQuery(sql, null);
    }
    public Cursor getDetailActive(MeterModel installDeatailsModel){
        SQLiteDatabase db = this.getWritableDatabase();
        String sql = "SELECT * FROM " + TABLE_NAME  +
                " WHERE " + COL3 + " = '" + installDeatailsModel.getM_status() + "'";
        return db.rawQuery(sql, null);
    }

    public Integer deleteDetails(int id){
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_NAME, "ID = ?", new String[] {String.valueOf(id)});
    }


    public void deleteAll(){
        SQLiteDatabase db = this.getWritableDatabase();
         db.execSQL("delete from "+ TABLE_NAME);
         db.close();
    }
}
