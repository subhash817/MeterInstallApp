package com.rjdev.meterinstallapp.Model;

import android.os.Parcel;
import android.os.Parcelable;

public class InstallDetailModel implements Parcelable {

    private String consumer_no;
    private String meter_no;
    private String protocol_no;
    private String installation_date;
    private String installation_type;

    private String gps_coordinate;
    private String address;
    private String bit_meterimage;
    private String bit_protocol;

    public InstallDetailModel(String consumer_no, String meter_no, String protocol_no, String installation_date, String installation_type
    ,String gps_coordinate,String address,String bit_meterimage,String bit_protocol ) {
        this.consumer_no = consumer_no;
        this.meter_no = meter_no;
        this.protocol_no = protocol_no;
        this.installation_date = installation_date;
        this.installation_type = installation_type;
        this.gps_coordinate = gps_coordinate;
        this.address = address;
        this.bit_meterimage = bit_meterimage;
        this.bit_protocol = bit_protocol;
    }


    protected InstallDetailModel(Parcel in) {

        consumer_no = in.readString();
        meter_no = in.readString();
        protocol_no = in.readString();
        installation_date = in.readString();
        installation_type = in.readString();
        gps_coordinate = in.readString();
        address = in.readString();
        bit_meterimage = in.readString();
        bit_protocol = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(consumer_no);
        dest.writeString(meter_no);
        dest.writeString(protocol_no);
        dest.writeString(installation_date);
        dest.writeString(installation_type);
        dest.writeString(gps_coordinate);
        dest.writeString(address);
        dest.writeString(bit_meterimage);
        dest.writeString(bit_protocol);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<InstallDetailModel> CREATOR = new Creator<InstallDetailModel>() {
        @Override
        public InstallDetailModel createFromParcel(Parcel in) {
            return new InstallDetailModel(in);
        }

        @Override
        public InstallDetailModel[] newArray(int size) {
            return new InstallDetailModel[size];
        }
    };

    public String getconsumer_no() {
        return consumer_no;
    }

    public void setconsumer_no(String consumer_no) {
        this.consumer_no = consumer_no;
    }

    public String getmeter_no() {
        return meter_no;
    }

    public void setmeter_no(String meter_no) {
        this.meter_no = meter_no;
    }

    public String getprotocol_no() {
        return protocol_no;
    }

    public void setprotocol_no(String protocol_no) {
        this.protocol_no = protocol_no;
    }

    public String getinstallation_date() {
        return installation_date;
    }

    public void setinstallation_date(String installation_date) {
        this.installation_date = installation_date;
    }

    public String getinstallation_type() {
        return installation_type;
    }

    public void setinstallation_type(String installation_type) {
        this.installation_type = installation_type;
    }


    public String getGps_coordinate() {
        return gps_coordinate;
    }

    public void setGps_coordinate(String gps_coordinate) {
        this.gps_coordinate = gps_coordinate;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getBit_meterimage() {
        return bit_meterimage;
    }

    public void setBit_meterimage(String bit_meterimage) {
        this.bit_meterimage = bit_meterimage;
    }

    public String getBit_protocol() {
        return bit_protocol;
    }

    public void setBit_protocol(String bit_protocol) {
        this.bit_protocol = bit_protocol;
    }

    @Override
    public String toString() {
        return "InstallDeatailsModel{" +
                "consumer_no='" + consumer_no + '\'' +
                ", meter_no='" + meter_no + '\'' +
                ", protocol_no='" + protocol_no + '\'' +
                ", installation_date='" + installation_date + '\'' +
                ", installation_type='" + installation_type + '\'' +
                ", installation_type='" + address + '\'' +
                ", installation_type='" + bit_meterimage + '\'' +
                ", installation_type='" + bit_protocol + '\'' +
                '}';
    }
}
