package com.rjdev.meterinstallapp.Model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class MeterModel implements Parcelable{

    private String meter_no;
    private String installation_date;
    private String m_status;

    public MeterModel(String meter_no, String installation_date, String m_status) {
        this.meter_no = meter_no;
        this.installation_date = installation_date;
        this.m_status = m_status;

    }

    protected MeterModel(Parcel in) {


        meter_no = in.readString();
        installation_date = in.readString();
        m_status = in.readString();

    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeString(meter_no);
        dest.writeString(installation_date);
        dest.writeString(m_status);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Parcelable.Creator<MeterModel> CREATOR = new Parcelable.Creator<MeterModel>() {
        @Override
        public MeterModel createFromParcel(Parcel in) {
            return new MeterModel(in);
        }

        @Override
        public MeterModel[] newArray(int size) {
            return new MeterModel[size];
        }
    };
    public String getMeter_no() {
        return meter_no;
    }

    public void setMeter_no(String meter_no) {
        this.meter_no = meter_no;
    }

    public String getInstallation_date() {
        return installation_date;
    }

    public void setInstallation_date(String installation_date) {
        this.installation_date = installation_date;
    }

    public String getM_status() {
        return m_status;
    }

    public void setM_status(String m_status) {
        this.m_status = m_status;
    }
}
