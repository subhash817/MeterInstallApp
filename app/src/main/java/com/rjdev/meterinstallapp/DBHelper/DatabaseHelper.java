package com.rjdev.meterinstallapp.DBHelper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.rjdev.meterinstallapp.Model.InstallDetailModel;

public class DatabaseHelper  extends SQLiteOpenHelper {

    private static final String TAG = "DatabaseHelper";

    private static final String DATABASE_NAME = "insatlldetail.db";
    private static final String TABLE_NAME = "meterinstall";
    public static final String COL0 = "Id_No";
    public static final String COL1 = "Customer_Number";
    public static final String COL2 = "Meter_No";
    public static final String COL3 = "Protocol_No";
    public static final String COL4 = "Installation_Date";
    public static final String COL5 = "Installation_Type";
    public static final String COL6 = "GPS_Coordinates";
    public static final String COL7 = "Address";
    public static final String COL8 = "Bit_Meter_Image";
    public static final String COL9 = "Bit_Protocol_Image";


    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "CREATE TABLE " +
                TABLE_NAME + " ( " +
                COL0 + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL1 + " TEXT, " +
                COL2 + " TEXT, " +
                COL3 + " TEXT, " +
                COL4 + " TEXT, " +
                COL5 + " TEXT, " +
                COL6 + " TEXT, " +
                COL7 + " TEXT, " +
                COL8 + " TEXT, " +
                COL9 + " TEXT )";
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
    public boolean addDetails(InstallDetailModel installDeatailsModel) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL1, installDeatailsModel.getconsumer_no());
        contentValues.put(COL2, installDeatailsModel.getmeter_no());
        contentValues.put(COL3, installDeatailsModel.getprotocol_no());
        contentValues.put(COL4, installDeatailsModel.getinstallation_date());
        contentValues.put(COL5, installDeatailsModel.getinstallation_type());
        contentValues.put(COL6, installDeatailsModel.getGps_coordinate());
        contentValues.put(COL7, installDeatailsModel.getAddress());
        contentValues.put(COL8, installDeatailsModel.getBit_meterimage());
        contentValues.put(COL9, installDeatailsModel.getBit_protocol());

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
    public boolean updateDetails(InstallDetailModel installDeatailsModel, int id){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL1, installDeatailsModel.getconsumer_no());
        contentValues.put(COL2, installDeatailsModel.getmeter_no());
        contentValues.put(COL3, installDeatailsModel.getprotocol_no());
        contentValues.put(COL4, installDeatailsModel.getinstallation_date());
        contentValues.put(COL5, installDeatailsModel.getinstallation_type());
        contentValues.put(COL6, installDeatailsModel.getGps_coordinate());
        contentValues.put(COL7, installDeatailsModel.getAddress());
        contentValues.put(COL8, installDeatailsModel.getBit_meterimage());
        contentValues.put(COL9, installDeatailsModel.getBit_protocol());

        int update = db.update(TABLE_NAME, contentValues, COL0 + " = ? ", new String[] {String.valueOf(id)} );

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
    public Cursor getDetailsID(InstallDetailModel installDeatailsModel){
        SQLiteDatabase db = this.getWritableDatabase();
        String sql = "SELECT * FROM " + TABLE_NAME  +
                " WHERE " + COL1 + " = '" + installDeatailsModel.getconsumer_no() + "'";
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
