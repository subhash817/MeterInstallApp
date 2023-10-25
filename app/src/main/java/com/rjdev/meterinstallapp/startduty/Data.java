
package com.rjdev.meterinstallapp.startduty;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class Data {

    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("0")
    @Expose
    private String _0;
    @SerializedName("daily_schedule_id")
    @Expose
    private String dailyScheduleId;
    @SerializedName("1")
    @Expose
    private String _1;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String get0() {
        return _0;
    }

    public void set0(String _0) {
        this._0 = _0;
    }

    public String getDailyScheduleId() {
        return dailyScheduleId;
    }

    public void setDailyScheduleId(String dailyScheduleId) {
        this.dailyScheduleId = dailyScheduleId;
    }

    public String get1() {
        return _1;
    }

    public void set1(String _1) {
        this._1 = _1;
    }

}